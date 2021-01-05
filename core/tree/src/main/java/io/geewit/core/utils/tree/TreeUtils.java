package io.geewit.core.utils.tree;

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

    public static <N extends TreeNode<N, Key>, Key extends Serializable> List<N> generateTree(List<N> nodes) {
        List<N> treeRoots = new ArrayList<>();
        if(nodes == null || nodes.isEmpty()) {
            return treeRoots;
        }
        nodes.sort(Comparator.comparing(TreeNode::getParentIds));
        Map<Key, Deque<N>> stackMap = new HashMap<>(nodes.size());
        for (N nodeObj : nodes) {
            if (nodeObj == null) {
                continue;
            }
            Key stackMapKey;
            if(nodeObj.getParentId() == null) {
                stackMapKey = nodeObj.getId();
            } else {
                stackMapKey = nodeObj.getParentId();
            }
            Deque<N> stack = stackMap.get(stackMapKey);
            if(stack == null) {
                stack = new ArrayDeque<>();
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
                logger.debug("stack.push " + nodeObj.getId() + ")");
                if (logger.isDebugEnabled()) {
                    String stringBuilder = stack.stream().map(org -> org.getId() + ",").collect(Collectors.joining());
                    logger.debug("stack: [" + stringBuilder + "]");
                }
                logger.debug("TreeNode(" + parent.getId() + ").addChild " + nodeObj.getId());
                parent.addChild(nodeObj);
            }
            stackMap.put(stackMapKey, stack);
        }
        return treeRoots;
    }

    /**
     * 根据父节点id查找父节点
     *
     * @param stack
     * @param parentId
     * @return
     */
    private static <N extends TreeNode<N, Key>, Key extends Serializable> N findParent(Deque<N> stack, Key parentId) {
        if (parentId == null) {
            logger.debug("parentId == null, return null");
            return null;
        }
        if (logger.isDebugEnabled()) {
            String stackLog = stack.stream().map(org -> org.getId() + ",").collect(Collectors.joining());
            logger.debug("parentId = " + parentId + ", stack: [" + stackLog + "]");
        }
        N node;
        do {
            if (stack.isEmpty()) {
                logger.debug("stack.isEmpty");
                return null;
            }
            node = stack.peek();
            logger.debug("stack.peek = " + node.getId());
            logger.debug("node.id = " + node.getId() + ", parentId = " + parentId);
            if (Objects.equals(node.getId(), parentId)) {
                logger.debug("found parent = " + node.getId());
                return node;
            } else {
                N popNode = stack.pop();
                logger.debug("stack.pop: " + popNode.getId() + ")");
            }
        } while (true);
    }

    public static <N extends TreeNode<N, Key>, Key extends Serializable> Set<Key> cascadeCheckKeys(List<N> nodes, Set<Key> checkingKeys) {
        if(nodes == null || nodes.isEmpty()) {
            return Collections.emptySet();
        }
        if(checkingKeys == null || checkingKeys.isEmpty()) {
            return Collections.emptySet();
        }

        nodes.forEach(node -> node.setChecked(checkingKeys.stream().anyMatch(key -> key.equals(node.getId()))));

        List<N> roots = generateTree(nodes);

        if(roots.isEmpty()) {
            return Collections.emptySet();
        }

        Set<Key> checkedKeys = new HashSet<>();

        for(N root : roots) {
            Deque<N> stack = new ArrayDeque<>();
            stack.push(root);
            while (!stack.isEmpty()) {
                N node = stack.pop();
                if(node.getChecked() != null && node.getChecked()) {
                    checkedKeys.add(node.getId());
                }
                if (node.getChildren() != null) {
                    for (N child : node.getChildren()) {
                        if(node.getChecked() != null && node.getChecked()) {
                            child.setChecked(true);
                        }
                        stack.push(child);
                    }
                }
            }
        }

        return checkedKeys;
    }
}
