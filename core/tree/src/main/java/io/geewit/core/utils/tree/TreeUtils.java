package io.geewit.core.utils.tree;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author geewit
 */
@Slf4j
public class TreeUtils {

    public static <N extends TreeNode<N, Key>, Key extends Serializable> List<N> buildTree(List<N> nodes, Key rootId) {
        if(nodes == null || nodes.isEmpty()) {
            return Collections.emptyList();
        }
        List<N> roots = null;
        Map<Key, List<N>> childrenMap = new HashMap<>();
        for (N node : nodes) {
            if (node.getParentId() == null) {
                if (rootId == null) {
                    if (roots == null) {
                        roots = Stream.of(node).collect(Collectors.toList());
                    } else {
                        roots.add(node);
                    }
                }
            } else {
                childrenMap.computeIfAbsent(node.getParentId(), k -> new ArrayList<>()).add(node);
            }
        }
        if (rootId != null) {
            roots = childrenMap.get(rootId);
        }

        if (roots == null) {
            final Set<Key> allIds = nodes.stream().map(N::getId).filter(Objects::nonNull).collect(Collectors.toSet());
            final Set<Key> rootKeys;
            if (rootId == null) {
                rootKeys = nodes.stream()
                        .filter(n -> n.getParentId() == null || !allIds.contains(n.getParentId()))
                        .map(TreeNode::getId)
                        .collect(Collectors.toSet());
            } else {
                rootKeys = Stream.of(rootId).collect(Collectors.toSet());
            }

            roots = nodes.stream().filter(n -> rootKeys.contains(n.getId())).collect(Collectors.toList());
            if (roots.isEmpty()) {
                return Collections.emptyList();
            }
        }

        if (roots.isEmpty()) {
            return Collections.emptyList();
        }
        roots.stream().filter(Objects::nonNull).forEach(root -> forEach(childrenMap, root));
        return roots;
    }

    public static <N extends TreeNode<N, Key>, Key extends Serializable> List<N> buildTree(List<N> nodes) {
        return buildTree(nodes, null);
    }

    private static <N extends TreeNode<N, Key>, Key extends Serializable> void forEach(Map<Key, List<N>> childrenMap, N node) {
        if (childrenMap == null || childrenMap.isEmpty()) {
            return;
        }
        Key key = node.getId();
        List<N> children = childrenMap.get(key);
        if (children != null) {
            node.setChildren(children);
            if (!children.isEmpty()) {
                children.forEach(child -> forEach(childrenMap, child));
            }
        }
    }

    /**
     * 根据parentIds 构造 tree
     *
     * @param nodes 节点列表
     * @return 多颗 tree
     */
    public static <N extends TreeNode<N, Key>, Key extends Serializable> List<N> buildTreeByParentIds(List<N> nodes) {
        if (nodes == null || nodes.isEmpty()) {
            return Collections.emptyList();
        }
        //region 除非parentIds全是正确的, 否则无法正确构造出树
        nodes.sort(Comparator.comparing(TreeNode::getParentIds));
        //endregion

        return buildTreeWithSortedList(nodes);
    }

    private static <N extends TreeNode<N, Key>, Key extends Serializable> List<N> buildTreeWithSortedList(List<N> nodes) {
        List<N> treeRoots = new ArrayList<>();
        Map<Key, Deque<N>> stackMap = new HashMap<>(nodes.size());
        for (N nodeObj : nodes) {
            if (nodeObj == null) {
                continue;
            }
            Key stackMapKey;
            if (nodeObj.getParentId() == null) {
                stackMapKey = nodeObj.getId();
            } else {
                stackMapKey = nodeObj.getParentId();
            }
            Deque<N> stack = stackMap.get(stackMapKey);
            if (stack == null) {
                stack = new ArrayDeque<>();
                stackMap.put(stackMapKey, stack);
            }
            if (stack.isEmpty()) {
                treeRoots.add(nodeObj);
                stack.push(nodeObj);
                log.debug("set tree root = " + nodeObj.getId() + ")");
            } else {
                N parent = findParent(stack, nodeObj.getParentId());
                if (parent == null) {
                    log.debug("TreeNode(" + nodeObj.getId() + ").parent(" + nodeObj.getParentId() + ") == null, continue");
                    continue;
                }
                log.debug("TreeNode(" + nodeObj.getId() + ").parent = " + parent.getId() + ")");
                stack.push(nodeObj);
                if (log.isDebugEnabled()) {
                    log.debug("stack.push " + nodeObj.getId() + ")");
                    String stackLog = stack.stream().map(org -> org.getId().toString()).collect(Collectors.joining(","));
                    log.debug("stack.push " + nodeObj.getId() + "), stack: [" + stackLog + "]");
                }
                log.debug("TreeNode(" + parent.getId() + ").addChild " + nodeObj.getId());
                parent.addChild(nodeObj);
            }
        }
        return treeRoots;
    }

    /**
     * 根据父节点id查找父节点
     *
     * @param stack    缓存栈
     * @param parentId 父节点id
     * @return 父节点
     */
    private static <N extends TreeNode<N, Key>, Key extends Serializable> N findParent(Deque<N> stack, Key parentId) {
        if (parentId == null) {
            log.debug("parentId == null, return null");
            return null;
        }
        if (log.isDebugEnabled()) {
            String stackLog = stack.stream().map(org -> org.getId().toString()).collect(Collectors.joining(","));
            log.debug("parentId = " + parentId + ", stack: [" + stackLog + "]");
        }
        while (!stack.isEmpty()) {
            N node = stack.peek();
            log.debug("stack.peek = " + node.getId());
            log.debug("node.id = " + node.getId() + ", parentId = " + parentId);
            if (Objects.equals(node.getId(), parentId)) {
                log.debug("found parent = " + node.getId());
                return node;
            } else {
                N popNode = stack.pop();
                log.debug("not a parent, stack.pop: " + popNode.getId() + ")");
            }
        }
        return null;
    }

    /**
     * 递归选中节点
     *
     * @param nodes        多个树
     * @param checkingKeys 选中的id集合
     * @return 递归后最终选中的id集合
     */
    public static <N extends TreeNode<N, Key>, Key extends Serializable> Set<Key> cascadeCheckKeys(List<N> nodes, Collection<Key> checkingKeys) {
        if (nodes == null || nodes.isEmpty()) {
            return Collections.emptySet();
        }
        if (checkingKeys == null || checkingKeys.isEmpty()) {
            return Collections.emptySet();
        }

        nodes.forEach(node -> node.setChecked(checkingKeys.stream().anyMatch(key -> key.equals(node.getId()))));

        List<N> roots = buildTree(nodes);

        if (roots.isEmpty()) {
            return Collections.emptySet();
        }

        return cascadeCheckNodes(roots);
    }

    /**
     * 递归选中树节点(可能多颗树)
     *
     * @param nodes        树节点集合
     * @param checkingKeys 选中的树节点id集合
     * @return 递归后最终选中的树节点id集合
     */
    public static <N extends TreeNode<N, Key>, Key extends Serializable> Pair<List<N>, Set<Key>> buildTreeAndCascadeCheckKeys(List<N> nodes, Collection<Key> checkingKeys) {
        if (nodes == null || nodes.isEmpty()) {
            return Pair.of(Collections.emptyList(), Collections.emptySet());
        }
        if (checkingKeys == null || checkingKeys.isEmpty()) {
            return Pair.of(Collections.emptyList(), Collections.emptySet());
        }

        nodes.forEach(node -> node.setChecked(checkingKeys.stream().anyMatch(key -> key.equals(node.getId()))));

        List<N> roots = buildTree(nodes);

        if (roots.isEmpty()) {
            return Pair.of(Collections.emptyList(), Collections.emptySet());
        }
        Set<Key> checkedKeys = cascadeCheckNodes(roots);

        return Pair.of(roots, checkedKeys);
    }

    /**
     * 递归选中树节点
     * @param roots   多个树的根节点
     * @return        选中的根节点id集合
     * @param <N>     树节点
     * @param <Key>   树节点id
     */
    private static <N extends TreeNode<N, Key>, Key extends Serializable> Set<Key> cascadeCheckNodes(List<N> roots) {
        Set<Key> checkedKeys = new HashSet<>();

        for (N root : roots) {
            Deque<N> stack = new ArrayDeque<>();
            stack.push(root);
            while (!stack.isEmpty()) {
                N node = stack.pop();
                boolean parentChecked = false;
                if (node.getChecked() != null && node.getChecked()) {
                    checkedKeys.add(node.getId());
                    parentChecked = true;
                }
                if (node.getChildren() != null && !node.getChildren().isEmpty()) {
                    Boolean allChildrenChecked = parentChecked ? true : null;
                    for (N child : node.getChildren()) {
                        if (allChildrenChecked != null && allChildrenChecked) {
                            child.setChecked(true);
                        } else if (allChildrenChecked == null) {
                            allChildrenChecked = child.getChecked() != null && child.getChecked();
                        }
                        stack.push(child);
                    }
                    if (allChildrenChecked != null && allChildrenChecked) {
                        node.setChecked(true);
                        checkedKeys.add(node.getId());
                    }
                }
            }
        }
        return checkedKeys;
    }
}
