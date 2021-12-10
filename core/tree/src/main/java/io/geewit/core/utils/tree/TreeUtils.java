package io.geewit.core.utils.tree;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author geewit
 */
public class TreeUtils {
    private final static Logger logger = LoggerFactory.getLogger(TreeUtils.class);

    public static <N extends TreeNode<N, Key>, Key extends Serializable> List<N> buildTree(List<N> nodes) {
        if(nodes == null || nodes.isEmpty()) {
            return Collections.emptyList();
        }
        Map<String, List<N>> cildrenMap = nodes.stream().collect(Collectors.groupingBy(n -> n.getParentId() == null ? StringUtils.EMPTY : n.getParentId().toString()));
        List<N> roots = cildrenMap.get(StringUtils.EMPTY);
        if (roots == null || roots.isEmpty()) {
            return Collections.emptyList();
        }
        for (N root : roots) {
            if (root == null) {
                continue;
            }
            forEach(cildrenMap, root);
        }
        return roots;
    }

    private static <N extends TreeNode<N, Key>, Key extends Serializable> void forEach(Map<String, List<N>> childrenMap, N node) {
        if (childrenMap == null || childrenMap.isEmpty()) {
            return;
        }
        String key = node.getId().toString();
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
                logger.debug("set tree root = " + nodeObj.getId() + ")");
            } else {
                N parent = findParent(stack, nodeObj.getParentId());
                if (parent == null) {
                    logger.debug("TreeNode(" + nodeObj.getId() + ").parent(" + nodeObj.getParentId() + ") == null, continue");
                    continue;
                }
                logger.debug("TreeNode(" + nodeObj.getId() + ").parent = " + parent.getId() + ")");
                stack.push(nodeObj);
                if (logger.isDebugEnabled()) {
                    logger.debug("stack.push " + nodeObj.getId() + ")");
                    String stackLog = stack.stream().map(org -> org.getId().toString()).collect(Collectors.joining(","));
                    logger.debug("stack.push " + nodeObj.getId() + "), stack: [" + stackLog + "]");
                }
                logger.debug("TreeNode(" + parent.getId() + ").addChild " + nodeObj.getId());
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
            logger.debug("parentId == null, return null");
            return null;
        }
        if (logger.isDebugEnabled()) {
            String stackLog = stack.stream().map(org -> org.getId().toString()).collect(Collectors.joining(","));
            logger.debug("parentId = " + parentId + ", stack: [" + stackLog + "]");
        }
        while (!stack.isEmpty()) {
            N node = stack.peek();
            logger.debug("stack.peek = " + node.getId());
            logger.debug("node.id = " + node.getId() + ", parentId = " + parentId);
            if (Objects.equals(node.getId(), parentId)) {
                logger.debug("found parent = " + node.getId());
                return node;
            } else {
                N popNode = stack.pop();
                logger.debug("not a parent, stack.pop: " + popNode.getId() + ")");
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

        Set<Key> checkedKeys = cascadeCheckNodes(roots);

        return checkedKeys;
    }

    /**
     * 递归选中节点
     *
     * @param nodes        多个树
     * @param checkingKeys 选中的id集合
     * @return 递归后最终选中的id集合
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
                        if (parentChecked) {
                            child.setChecked(true);
                        } else {
                            if (child.getChecked() != null && child.getChecked()) {
                                if (allChildrenChecked == null) {
                                    allChildrenChecked = true;
                                }
                            } else {
                                allChildrenChecked = false;
                            }
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
