package io.geewit.core.utils.tree;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Pair;

import java.io.Serializable;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Builder
@Setter
@Getter
public class SignContext<N extends SignedTreeNode<N, Key>, Key extends Serializable> {
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
    private Map<Key, List<N>> childrenMap;

    /**
     * 树的根节点
     */
    private List<N> roots;

    /**
     * 递归后最终标记的树节点对象集合
     */
    private Set<SimpleNodeSign<Key>> simpleNodeSigns;

    public void buildTree() {
        if (this.nodes == null || this.nodes.isEmpty()) {
            this.roots = Collections.emptyList();
            return;
        }
        for (N node : this.nodes) {
            if (node.getParentId() == null) {
                if (this.rootId == null) {
                    if (this.roots == null) {
                        this.roots = Stream.of(node).collect(Collectors.toList());
                    } else {
                        this.roots.add(node);
                    }
                }
            } else {
                if (this.childrenMap == null) {
                    this.childrenMap = new HashMap<>();
                }
                this.childrenMap.computeIfAbsent(node.getParentId(), k -> new ArrayList<>()).add(node);
            }
        }
        if (this.rootId != null) {
            if (this.childrenMap == null) {
                this.roots = null;
            } else {
                this.roots = this.childrenMap.get(this.rootId);
            }
        }

        if (this.roots == null) {
            final Set<Key> allIds = this.nodes.stream().map(n -> n.getId()).filter(Objects::nonNull).collect(Collectors.toSet());
            final Set<Key> rootKeys;
            if (this.rootId == null) {
                rootKeys = this.nodes.stream()
                        .filter(n -> n.getParentId() == null || !allIds.contains(n.getParentId()))
                        .map(TreeNode::getId)
                        .collect(Collectors.toSet());
            } else {
                rootKeys = Stream.of(this.rootId).collect(Collectors.toSet());
            }

            this.roots = this.nodes.stream().filter(n -> rootKeys.contains(n.getId())).collect(Collectors.toList());
            if (this.roots.isEmpty()) {
                return;
            }
        }

        if (this.roots.isEmpty()) {
            return;
        }
        this.roots.stream().filter(Objects::nonNull).forEach(this::forEach);
    }

    private void forEach(N node) {
        if (this.childrenMap == null || this.childrenMap.isEmpty()) {
            return;
        }
        Key key = node.getId();
        List<N> children = this.childrenMap.get(key);
        if (children != null) {
            node.setChildren(children);
            if (!children.isEmpty()) {
                children.forEach(this::forEach);
            }
        }
    }

    /**
     * 从根节点开始递归标记所有节点
     *
     * @return 递归后最终标记的树节点对象集合
     */
    public void cascadeSignRoots() {
        for (N root : roots) {
            // 自上而下的缓存栈
            Deque<N> rootStack = new ArrayDeque<>();
            rootStack.push(root);

            // 自下而上的缓存栈
            Map<Key, N> changedNodeMap = new HashMap<>();
            while (!rootStack.isEmpty()) {
                N parentNode = rootStack.pop();
                Integer originNodeSign = parentNode.getSign();
                Integer sign;
                if (originNodeSign == null) {
                    sign = null;
                } else {
                    this.addSimpleNodeSigns(SimpleNodeSign.<Key>builder().id(parentNode.getId()).sign(originNodeSign).build());
                    sign = originNodeSign;
                }
                //region sign = sign | 父节点的sign
                List<N> children = parentNode.getChildren();
                if (children != null && !children.isEmpty()) {
                    Integer allChildrenSign = null;
                    for (N child : children) {
                        sign = child.apply(sign);
                        Integer originChildSign = child.getSign();
                        if (originChildSign == null) {
                            if (sign != null) {
                                child.accept(parentNode.getSign(), sign);
                            }
                        }
                        if (child.getSign() != null) {
                            child.accept(parentNode.getSign(), sign);
                            Integer newSign = child.getSign();
                            child.setSign(newSign);
                            if (allChildrenSign == null || allChildrenSign > 0) {
                                if (newSign == (child.getSign() & newSign)) {
                                    allChildrenSign = newSign;
                                }
                            }
                        } else {
                            allChildrenSign = 0;
                        }

                        if (allChildrenSign == null) {
                            allChildrenSign = 0;
                        }

                        if (child.getSign() != null) {
                            this.addSimpleNodeSigns(SimpleNodeSign.<Key>builder().id(child.getId()).sign(child.getSign()).build());
                        }
                        if (child.getChildren() != null && !child.getChildren().isEmpty()) {
                            rootStack.push(child);
                        }
                        if (!Objects.equals(child.getSign(), originChildSign)) {
                            changedNodeMap.put(child.getId(), child);
                        }
                    }
                    if (allChildrenSign > 0) {
                        parentNode.accept(null, allChildrenSign);
                    }
                    if (!Objects.equals(parentNode.getSign(), originNodeSign)) {
                        changedNodeMap.put(parentNode.getId(), parentNode);
                    }
                }
                //endregion
            }
            while (!changedNodeMap.isEmpty()) {
                Map.Entry<Key, N> changedNodeEntry = changedNodeMap.entrySet().stream().findFirst().get();
                N changeNode = changedNodeEntry.getValue();
                Integer allChildrenSign = changeNode.getSign();
                if (changeNode.getParentId() != null) {
                    List<N> siblings = this.childrenMap.get(changeNode.getParentId());
                    if (siblings != null && !siblings.isEmpty()) {
                        for (N sibling : siblings) {
                            if (Objects.equals(sibling.getId(), changeNode.getId())) {
                                continue;
                            }
                            if (allChildrenSign != null && allChildrenSign > 0) {
                                if (allChildrenSign != (sibling.getSign() & allChildrenSign)) {
                                    allChildrenSign = 0;
                                }
                            }
                        }
                        if (allChildrenSign != null && allChildrenSign > 0) {
                            N parentNode = this.nodes.stream().filter(n -> n.getId().equals(changeNode.getParentId())).findFirst().orElse(null);
                            if (parentNode != null) {
                                Integer originParentNodeSign = parentNode.getSign();
                                parentNode.accept(originParentNodeSign, allChildrenSign);
                                if (!Objects.equals(originParentNodeSign, allChildrenSign)) {
                                    changedNodeMap.put(parentNode.getId(), parentNode);
                                }
                            }
                        }
                    }
                }

                changedNodeMap.remove(changedNodeEntry.getKey());
            }
        }
    }

    private void addSimpleNodeSigns(SimpleNodeSign<Key> simpleNodeSign) {
        if (this.simpleNodeSigns == null) {
            simpleNodeSigns = Stream.of(simpleNodeSign).collect(Collectors.toSet());
        } else {
            this.simpleNodeSigns.add(simpleNodeSign);
        }
    }

    public Pair<List<N>, Set<SimpleNodeSign<Key>>> getPairOfRootsAndNodeSigns(SignKeysMap<Key> signKeysMap, BiFunction<Integer, Integer, Integer> signFunction) {
        KeySignMap<Key> keySignMap = new KeySignMap<>(signKeysMap, signFunction);

        nodes.forEach(node -> {
            Integer existValue = keySignMap.get(node.id);
            if (existValue == null) {
                node.setSign(0);
            } else {
                node.setSign(existValue);
            }
        });
        this.buildTree();
        this.cascadeSignRoots();
        return Pair.of(this.roots, this.simpleNodeSigns);
    }
}
