package io.geewit.core.utils.tree;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 树遍历工具类
 *
 * @author geewit
 */
@Builder
@FieldNameConstants
@Setter
@Getter
public class TreeTraverseContext<N extends SignedTreeNode<N, Key>, Key extends Serializable> {
    /**
     * 输入的初始化的节点列表
     */
    private List<N> nodes;

    /**
     * 获取树的根节点id
     */
    private Key rootId;

    /**
     * key: id, value: 下级节点列表
     */
    private Map<Key, N> nodeMap;

    /**
     * 树的根节点
     */
    private List<N> roots;

    /**
     * 是否向下传递标记(sign)
     */
    @Builder.Default
    private boolean transmission = Boolean.TRUE;

    /**
     * 是否覆盖 是: 如果没有传 id则设置sign == 0
     */
    @Builder.Default
    private boolean overwrite = Boolean.TRUE;
    /**
     * 是否需要压缩标记(sign)
     */
    @Builder.Default
    private boolean needCompress = Boolean.FALSE;

    /**
     * 输入的节点标记参数集合
     */
    private Collection<NodeSignParameter<Key>> signParameters;

    /**
     * 输入的节点标记参数Map(方便获取)
     */
    private Map<Key, NodeSignParameter<Key>> signParametersMap;

    /**
     * 节点sign传递逻辑 first param: 当前节点已存在的sign, secend param: 传入的sign
     */
    private SignChildConsumer<N, Key> signChildConsumer;
    /**
     * 根据父节点sign和传入的sign以及当前sign计算当前节点sign逻辑 first param: 当前节点已存在的sign, secend param: SignParams.parentSign 传入的父节点sign, SignParams.sign 传入的sign
     */
    private SignParentConsumer<N, Key> signParentConsumer;

    /**
     * parentNode向下childNode传递sign的处理逻辑
     */
    private TransmissionChildConsumer<N, Key> transmissionChildConsumer;

    /**
     * 压缩子节点的sign, 根据父节点处理子节点的处理逻辑
     */
    private CompressChildConsumer<N, Key> compressChildConsumer;

    private void buildTree() {
        if (this.nodes == null || this.nodes.isEmpty()) {
            this.roots = Collections.emptyList();
            return;
        }
        this.roots = new ArrayList<>();
        nodeMap = nodes.stream().collect(Collectors.toMap(TreeNode::getId, node -> {
            if (node.sign == null) {
                node.setSign(0);
            }
            return node;
        }));
        nodes.forEach(node -> {
            if (node.parentId == null) {
                roots.add(node);
            } else {
                N parent = nodeMap.get(node.parentId);
                if (parent != null) {
                    parent.children.add(node);
                }
            }
        });
    }

    /**
     * 根据 signParameters 构造 signMap
     */
    private void buildSignParametersMap() {
        if (signParameters == null || signParameters.isEmpty()) {
            return;
        }
        signParametersMap = signParameters.stream()
                .filter(s -> s.getId() != null && s.getSign() != null)
                .collect(Collectors.toMap(NodeSignParameter::getId,
                        s -> NodeSignParameter.<Key>builder()
                                .id(s.getId())
                                .sign(s.getSign())
                                .transmission(s.getTransmission())
                                .build()));
    }


    /**
     * 从根节点开始递归标记所有节点
     */
    public void cascadeSign() {
        if (nodes == null || nodes.isEmpty()) {
            return;
        }
        if (signParameters == null || signParameters.isEmpty()) {
            return;
        }
        this.buildTree();
        if (roots == null || roots.isEmpty()) {
            return;
        }
        //region 向下传递sign, 修复已存在sign可能缺漏
        if (transmission) {
            this.transmissionAndCompressDownSign(Boolean.FALSE);
        }
        //endregion

        //region 构造 signMap
        this.buildSignParametersMap();
        //endregion

        this.signByParameters();

        if (transmission) {
            this.transmissionAndCompressDownSign(Boolean.TRUE);
        }
    }

    /**
     * 自下而上根据 signParameters 标记树节点
     */
    private void signByParameters() {
        for (N root : roots) {
            // 自上而下的缓存栈
            Stack<N> stack = new Stack<>();
            stack.push(root);
            // 自下而上的缓存栈
            Stack<N> nodeStack = new Stack<>();

            while (!stack.isEmpty()) {
                N node = stack.pop();
                nodeStack.push(node);
                node.children.forEach(stack::push);
            }

            // 修改过sign的节点缓存栈
            Stack<N> changedNodeStack = new Stack<>();

            while (!nodeStack.isEmpty()) {
                N parentNode = nodeStack.pop();
                Integer originParentSign = parentNode.sign;
                NodeSignParameter<Key> parentSignParameter = this.nodeSign(parentNode);
                parentNode.setSign(parentSignParameter.getSign());
                if (parentNode.children != null && !parentNode.children.isEmpty()) {
                    Integer allChildrenSign = null;
                    for (N childNode : parentNode.children) {
                        //传入的sign
                        NodeSignParameter<Key> signParameter = this.nodeSign(childNode);
                        //父节点sign传递逻辑, 根据父节点sign和传入的sign设置当前节点sign
                        signChildConsumer.accept(parentNode, childNode, signParameter);
                        Integer sign = childNode.sign;
                        if (sign > 0) {
                            if (allChildrenSign == null) {
                                allChildrenSign = sign;
                            } else {
                                if (sign == (allChildrenSign & sign)) {
                                    allChildrenSign = sign;
                                }
                            }
                        } else {
                            allChildrenSign = 0;
                        }

                        if (childNode.children != null && !childNode.children.isEmpty()) {
                            nodeStack.push(childNode);
                        }
                    }
                    if (allChildrenSign > 0) {
                        signParentConsumer.accept(parentNode, allChildrenSign, transmission);
                    }
                }
                if (!Objects.equals(parentNode.sign, originParentSign)) {
                    changedNodeStack.push(parentNode);
                }
            }
            while (!changedNodeStack.isEmpty()) {
                N changedNode = changedNodeStack.pop();
                if (changedNode == null) {
                    continue;
                }
                Integer allChildrenSign = changedNode.sign;
                //region 处理 siblings
                if (changedNode.parentId != null) {
                    N changedNodeParent = nodeMap.get(changedNode.parentId);
                    if (changedNodeParent == null) {
                        continue;
                    }

                    List<N> siblings = changedNodeParent.children;
                    if (siblings != null && !siblings.isEmpty()) {
                        for (N sibling : siblings) {
                            if (Objects.equals(sibling.id, changedNode.id)) {
                                continue;
                            }
                            if (allChildrenSign != null && allChildrenSign > 0) {
                                if (allChildrenSign != (sibling.sign & allChildrenSign)) {
                                    allChildrenSign = 0;
                                }
                            }
                        }
                        if (allChildrenSign != null && allChildrenSign > 0) {
                            Integer originParentSign = changedNodeParent.sign;
                            signParentConsumer.accept(changedNodeParent, allChildrenSign, transmission);
                            if (!Objects.equals(originParentSign, allChildrenSign)) {
                                changedNodeStack.push(changedNodeParent);
                            }
                        }
                    }
                }
                //endregion

                List<N> children = changedNode.children;
                if (children != null && !children.isEmpty()) {
                    for (N child : children) {
                        Integer originChildSign = child.sign;
                        transmissionChildConsumer.accept(changedNode, child);
                        if (!Objects.equals(child.sign, originChildSign)) {
                            changedNodeStack.push(child);
                        }
                    }
                }
            }
        }
    }

    /**
     * 自下而上地根据父节点传递下级节点的sign, 并在需要时压缩sign
     */
    private void transmissionAndCompressDownSign(Boolean compress) {

        for (N root : roots) {
            Stack<N> stack = new Stack<>();
            stack.push(root);
            Stack<N> nodeStack = new Stack<>();
            while (!stack.isEmpty()) {
                N node = stack.pop();
                nodeStack.push(node);
                node.children.forEach(stack::push);
            }
            while (!nodeStack.isEmpty()) {
                N node = nodeStack.pop();
                if (node.parentId != null) {
                    N parentNode = nodeMap.get(node.parentId);
                    if (parentNode != null) {
                        if (transmission) {
                            transmissionChildConsumer.accept(parentNode, node);
                        }
                        if (compress && needCompress) {
                            compressChildConsumer.accept(parentNode, node);
                        }
                    }
                }
            }
        }
    }

    private NodeSignParameter<Key> nodeSign(N node) {
        NodeSignParameter<Key> variableSimpleNodeSign = signParametersMap.get(node.id);
        NodeSignParameter<Key> simpleNodeSign;
        if (variableSimpleNodeSign == null) {
            if (overwrite) {
                simpleNodeSign = NodeSignParameter.<Key>builder()
                        .id(node.id)
                        .sign(0)
                        .transmission(node.transmission)
                        .build();
            } else {
                simpleNodeSign = NodeSignParameter.<Key>builder()
                        .id(node.id)
                        .sign(node.sign)
                        .transmission(node.transmission)
                        .build();
            }
        } else {
            simpleNodeSign = variableSimpleNodeSign;
        }
        return simpleNodeSign;
    }
}
