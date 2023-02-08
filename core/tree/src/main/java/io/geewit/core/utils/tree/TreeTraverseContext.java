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
     * 初始化的节点列表
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
    private boolean transmission;

    /**
     * 是否覆盖 是: 如果没有传 id则设置sign == 0
     */
    private boolean overwrite;
    /**
     * 是否需要压缩标记(sign)
     */
    private boolean needCompress;

    /**
     * 递归后最终标记的树节点对象集合
     */
    private Collection<SimpleNodeSign<Key>> signs;

    private Map<Key, SimpleNodeSign<Key>> signMap;

    private SignNodeConsumer<N, Key> signNodeConsumer;
    /**
     * 节点sign传递逻辑 first param: 当前节点已存在的sign, secend param: 传入的sign
     */
    private SignChildConsumer<N, Key> signChildConsumer;
    /**
     * 根据父节点sign和传入的sign以及当前sign计算当前节点sign逻辑 first param: 当前节点已存在的sign, secend param: SignParams.parentSign 传入的父节点sign, SignParams.sign 传入的sign
     */
    private SignParentConsumer<N, Key> signParentConsumer;
    private CompressChildConsumer<N, Key> compressChildConsumer;
    private TransmissionChildConsumer<N, Key> transmissionChildConsumer;

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

    private void buildSignMap() {
        if (signs == null || signs.isEmpty()) {
            return;
        }
        signMap = signs.stream()
                .filter(s -> s.getId() != null && s.getSign() != null)
                .collect(Collectors.toMap(SimpleNodeSign::getId,
                        s -> SimpleNodeSign.<Key>builder()
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
        if (signs == null || signs.isEmpty()) {
            return;
        }
        this.buildTree();
        if (roots == null || roots.isEmpty()) {
            return;
        }
        this.buildSignMap();
        for (N root : roots) {
            // 自上而下的缓存栈
            Deque<N> rootStack = new ArrayDeque<>();
            rootStack.push(root);

            // 自下而上的缓存栈
            Map<Key, N> changedNodeMap = new HashMap<>();
            while (!rootStack.isEmpty()) {
                N parentNode = rootStack.pop();
                Integer originParentSign = parentNode.sign;
                signNodeConsumer.accept(parentNode, this.nodeSign(parentNode), needCompress);
                if (parentNode.children != null && !parentNode.children.isEmpty()) {
                    Integer allChildrenSign = null;
                    for (N childNode : parentNode.children) {
                        //传入的sign
                        SimpleNodeSign<Key> simpleNodeSign = this.nodeSign(childNode);
                        //父节点sign传递逻辑, 根据父节点sign和传入的sign设置当前节点sign
                        signChildConsumer.accept(parentNode, childNode, simpleNodeSign);
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
                            rootStack.push(childNode);
                        }
                    }
                    if (allChildrenSign > 0) {
                        signParentConsumer.accept(parentNode, allChildrenSign, transmission);
                    }
                }
                if (!Objects.equals(parentNode.sign, originParentSign)) {
                    changedNodeMap.put(parentNode.id, parentNode);
                }
            }
            while (!changedNodeMap.isEmpty()) {
                Map.Entry<Key, N> changedNodeEntry = changedNodeMap.entrySet().stream().findFirst().get();
                N changeNode = changedNodeEntry.getValue();
                Integer allChildrenSign = changeNode.sign;
                if (changeNode.parentId != null) {
                    List<N> siblings = changeNode.children;
                    if (siblings != null && !siblings.isEmpty()) {
                        for (N sibling : siblings) {
                            if (Objects.equals(sibling.id, changeNode.id)) {
                                allChildrenSign = changeNode.sign;
                                continue;
                            }
                            if (allChildrenSign != null && allChildrenSign > 0) {
                                if (allChildrenSign != (sibling.sign & allChildrenSign)) {
                                    allChildrenSign = 0;
                                }
                            }
                        }
                        if (allChildrenSign != null && allChildrenSign > 0) {
                            N parentNode = this.nodes.stream().filter(n -> Objects.equals(n.id, changeNode.parentId)).findFirst().orElse(null);
                            if (parentNode != null) {
                                Integer originParentSign = parentNode.sign;
                                signParentConsumer.accept(parentNode, allChildrenSign, transmission);
                                if (!Objects.equals(originParentSign, allChildrenSign)) {
                                    changedNodeMap.put(parentNode.id, parentNode);
                                }
                            }
                        }
                    }
                }

                changedNodeMap.remove(changedNodeEntry.getKey());
            }
        }
        if (transmission) {
            this.transmissionSign();
        }
    }

    private void transmissionSign() {

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
                        if (needCompress) {
                            compressChildConsumer.accept(parentNode, node);
                        }
                    }
                }
            }
        }
    }

    private SimpleNodeSign<Key> nodeSign(N node) {
        SimpleNodeSign<Key> variableSimpleNodeSign = signMap.get(node.id);
        SimpleNodeSign<Key> simpleNodeSign;
        if (variableSimpleNodeSign == null) {
            if (overwrite) {
                simpleNodeSign = SimpleNodeSign.<Key>builder()
                        .id(node.id)
                        .sign(0)
                        .transmission(node.transmission)
                        .build();
            } else {
                simpleNodeSign = SimpleNodeSign.<Key>builder()
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
