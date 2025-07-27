package io.geewit.utils.core.tree;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.io.Serializable;
import java.util.*;
import java.util.function.Predicate;
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
    private boolean overwrite = Boolean.FALSE;
    /**
     * 是否需要压缩标记(sign)
     */
    @Builder.Default
    private boolean compress = Boolean.FALSE;

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

    private Predicate<N> rootPredicate;

    public void clear() {
        if (this.nodes != null) {
            this.nodes.clear();
            this.nodes = null;
        }
        this.rootId = null;
        if (this.nodeMap != null) {
            this.nodeMap.clear();
            this.nodeMap = null;
        }
        if (this.roots != null) {
            this.roots.clear();
            this.roots = null;
        }
        if (this.signParameters != null) {
            this.signParameters.clear();
            this.signParameters = null;
        }
        if (this.signParametersMap != null) {
            this.signParametersMap.clear();
            this.signParametersMap = null;
        }
        this.signChildConsumer = null;
        this.signParentConsumer = null;
        this.transmissionChildConsumer = null;
        this.compressChildConsumer = null;
        this.rootPredicate = null;
    }

    private void buildTree() {
        if (this.nodes == null || this.nodes.isEmpty()) {
            this.roots = Collections.emptyList();
            return;
        }
        if (this.roots != null) {
            return;
        }
        this.roots = new ArrayList<>();
        this.nodeMap = nodes.stream().collect(Collectors.toMap(TreeNode::getId, node -> {
            if (node.sign == null) {
                node.setSign(0);
            }
            return node;
        }, (oldValue, newValue) -> oldValue));
        nodes.forEach(node -> {
            if (node.parentId == null || (rootPredicate != null && rootPredicate.test(node))) {
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
                        s -> s, (oldValue, newValue) -> oldValue));
        signParameters.forEach(sp -> {
            if ((sp.getTransmissionDown() != null && sp.getTransmissionDown()) || (sp.getTransmissionUp() != null && sp.getTransmissionUp())) {
                if (sp.getSign() != null && sp.getSign() == 0) {
                    N node = nodeMap.get(sp.getId());
                    if (node != null) {
                        if (sp.getTransmissionDown() != null && sp.getTransmissionDown()) {
                            Stack<N> downStack = new Stack<>();
                            downStack.push(node);
                            while (!downStack.isEmpty()) {
                                N current = downStack.pop();
                                NodeSignParameter<Key> childSignParameter = signParametersMap.get(current.id);
                                if (childSignParameter == null) {
                                    signParametersMap.put(current.id, NodeSignParameter.<Key>builder()
                                            .id(current.id)
                                            .sign(0)
                                            .transmissionUp(Boolean.FALSE)
                                            .build());
                                }
                                List<N> children = current.children;
                                if (children != null && !children.isEmpty()) {
                                    for (N child : children) {
                                        downStack.push(child);
                                    }
                                }
                            }
                        }

                        if (sp.getTransmissionUp() != null && sp.getTransmissionUp()) {
                            Stack<N> upStack = new Stack<>();
                            upStack.push(node);
                            while (!upStack.isEmpty()) {
                                N current = upStack.pop();
                                NodeSignParameter<Key> childSignParameter = signParametersMap.get(current.id);
                                if (childSignParameter == null) {
                                    signParametersMap.put(current.id, NodeSignParameter.<Key>builder()
                                            .id(current.id)
                                            .sign(0)
                                            .transmissionDown(Boolean.FALSE)
                                            .build());
                                }
                                N parent = nodeMap.get(current.parentId);
                                if (parent != null) {
                                    upStack.push(parent);
                                }
                            }
                        }
                    }
                }
            }
        });

        if (this.overwrite) {
            if (this.nodes == null || this.nodes.isEmpty()) {
                return;
            }
            for (N node : this.nodes) {
                signParametersMap.putIfAbsent(node.getId(), NodeSignParameter
                        .<Key>builder()
                        .id(node.id)
                        .sign(0)
                        .transmission(Boolean.FALSE)
                        .build());
            }
        }
    }


    /**
     * 从根节点开始递归标记所有节点
     */
    public void cascadeSign() {
        if (nodes == null || nodes.isEmpty()) {
            return;
        }
        if (signParameters == null || signParameters.isEmpty()) {
            if (overwrite) {
                nodes.forEach(node -> node.sign = 0);
            }
            return;
        }
        this.buildTree();
        //region 向下传递sign, 修复已存在sign可能缺漏
        if (transmission) {
            this.transmissionAndCompressDownSign(Boolean.TRUE, Boolean.FALSE);
        }
        //endregion

        //region 构造 signMap
        this.buildSignParametersMap();
        //endregion

        this.signByParameters();

        if (compress) {
            this.transmissionAndCompressDownSign(Boolean.FALSE, Boolean.TRUE);
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
                this.setNodeSign(parentNode);
                if (parentNode.children != null && !parentNode.children.isEmpty()) {
                    Integer allChildrenSign = null;
                    for (N childNode : parentNode.children) {
                        //传入的sign
                        this.setNodeSign(childNode);
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
                    signParentConsumer.accept(parentNode, allChildrenSign, overwrite);
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
                Integer changedSign = changedNode.sign;
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
                            if (changedSign != null) {
                                if (changedSign != (sibling.sign & changedSign)) {
                                    changedSign = 0;
                                }
                            }
                        }
                        if (changedSign != null && changedSign > 0) {
                            Integer originParentSign = changedNodeParent.sign;
                            signParentConsumer.accept(changedNodeParent, changedSign, transmission);
                            if (!Objects.equals(originParentSign, changedSign)) {
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
    private void transmissionAndCompressDownSign(Boolean transmission, Boolean compress) {

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
                        if (transmission && this.transmission) {
                            transmissionChildConsumer.accept(parentNode, node);
                        }
                        if (compress && this.compress) {
                            compressChildConsumer.accept(parentNode, node);
                        }
                    }
                }
            }
        }
    }

    private void setNodeSign(N thisNode) {
        if (thisNode == null) {
            return;
        }
        N parentNode = null;
        if (thisNode.parentId != null) {
            parentNode = nodeMap.get(thisNode.parentId);
        }
        NodeSignParameter<Key> thisNodeSignParameter = signParametersMap.get(thisNode.id);
        Integer thisSign;
        if (thisNodeSignParameter == null) {
            thisSign = thisNode.sign;
            if (parentNode != null) {
                NodeSignParameter<Key> parentNodeSignParameter = signParametersMap.get(parentNode.id);
                if (parentNodeSignParameter != null) {
                    Integer parentSign = parentNodeSignParameter.getSign();
                    if (parentSign == 0 && parentNodeSignParameter.getTransmissionDown() != null && parentNodeSignParameter.getTransmissionDown()) {
                        thisSign = 0;
                    }
                }
            }
        } else {
            thisSign = thisNodeSignParameter.getSign();
        }
        NodeSignParameter<Key> childSignParameter = thisNodeSignParameter != null ? thisNodeSignParameter : NodeSignParameter.<Key>builder()
                .id(thisNode.id)
                .sign(thisSign)
                .build();
        if (thisNode.transmission != null) {
            childSignParameter.setTransmissionDown(thisNode.transmission);
        }
        signChildConsumer.accept(parentNode, thisNode, childSignParameter);
    }
}
