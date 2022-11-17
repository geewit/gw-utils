package io.geewit.core.utils.tree;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.apache.commons.lang3.tuple.Pair;

import java.io.Serializable;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Builder
@FieldNameConstants
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
     * @param signFunction 节点sign传递逻辑 first param: 当前节点已存在的sign, secend param: 传入的sign
     * @param signDependParentFunction 根据父节点sign和传入的sign以及当前sign计算当前节点sign逻辑 first param: 当前节点已存在的sign, secend param: SignParams.parentSign 传入的父节点sign, SignParams.sign 传入的sign
     */
    public void cascadeSignRoots(BiFunction<Integer, Integer, Integer> signFunction,
                                 BiFunction<Integer, SignParams, Integer> signDependParentFunction) {
        for (N root : roots) {
            // 自上而下的缓存栈
            Deque<N> rootStack = new ArrayDeque<>();
            rootStack.push(root);

            // 自下而上的缓存栈
            Map<Key, N> changedNodeMap = new HashMap<>();
            while (!rootStack.isEmpty()) {
                N parentNode = rootStack.pop();
                if (parentNode.getSign() == null) {
                    parentNode.setSign(0);
                }
                Integer originParentSign = parentNode.getSign();
                Integer parentSign;
                if (originParentSign > 0) {
                    parentSign = originParentSign;
                } else {
                    parentSign = 0;
                }
                //region sign = sign | 父节点的sign
                List<N> children = parentNode.getChildren();
                if (children != null && !children.isEmpty()) {
                    Integer allChildrenSign = null;
                    for (N child : children) {
                        /*
                         * 父节点sign传递逻辑
                         */
                        if (child.getSign() == null) {
                            child.setSign(0);
                        }
                        /*
                         * 根据父节点sign和传入的sign设置当前节点sign
                         */
                        Integer sign = signDependParentFunction.apply(child.getSign(), SignParams.builder().sign(child.getSign()).parentSign(parentSign).build());
                        child.setSign(sign);
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

                        if (child.getSign() != null) {
                            this.addSimpleNodeSigns(SimpleNodeSign.<Key>builder().id(child.getId()).sign(child.getSign()).build(), signFunction);
                        }
                        if (child.getChildren() != null && !child.getChildren().isEmpty()) {
                            rootStack.push(child);
                        }
                    }
                    if (allChildrenSign > 0) {
                        parentSign = signFunction.apply(parentNode.getSign(), allChildrenSign);
                    }
                }
                if (parentSign != null && parentSign > 0) {
                    this.addSimpleNodeSigns(SimpleNodeSign.<Key>builder().id(parentNode.getId()).sign(parentSign).build(), signFunction);
                }
                if (!Objects.equals(parentSign, originParentSign)) {
                    parentNode.setSign(parentSign);
                    changedNodeMap.put(parentNode.getId(), parentNode);
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
                                allChildrenSign = changeNode.getSign();
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
                                Integer originParentSign = parentNode.getSign();
                                parentNode.setSign(signFunction.apply(originParentSign, allChildrenSign));
                                if (!Objects.equals(originParentSign, allChildrenSign)) {
                                    changedNodeMap.put(parentNode.getId(), parentNode);
                                }
                                this.addSimpleNodeSigns(SimpleNodeSign.<Key>builder().id(parentNode.getId()).sign(parentNode.getSign()).build(), signFunction);
                            }
                        }
                    }
                }

                changedNodeMap.remove(changedNodeEntry.getKey());
            }
        }
    }

    private void addSimpleNodeSigns(SimpleNodeSign<Key> simpleNodeSign, BiFunction<Integer, Integer, Integer> signFunction) {
        if (this.simpleNodeSigns == null) {
            simpleNodeSigns = Stream.of(simpleNodeSign).collect(Collectors.toSet());
        } else {
            Optional<SimpleNodeSign<Key>> simpleNodeSignOptional = this.simpleNodeSigns.stream()
                    .filter(item -> Objects.equals(item.getId(), simpleNodeSign.getId()))
                    .findFirst();
            if (simpleNodeSignOptional.isPresent()) {
                SimpleNodeSign<Key> exitSimpleNodeSign = simpleNodeSignOptional.get();
                Integer sign = signFunction.apply(exitSimpleNodeSign.getSign(), simpleNodeSign.getSign());
                exitSimpleNodeSign.setSign(sign);
            } else {
                this.simpleNodeSigns.add(simpleNodeSign);
            }
        }
    }

    public Pair<List<N>, Set<SimpleNodeSign<Key>>> getPairOfRootsAndNodeSigns(SignKeysMap<Key> signKeysMap,
                                                                              BiFunction<Integer, Integer, Integer> signFunction,
                                                                              BiFunction<Integer, SignParams, Integer> signDependParentFunction) {
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
        this.cascadeSignRoots(signFunction, signDependParentFunction);
        return Pair.of(this.roots, this.simpleNodeSigns);
    }
}
