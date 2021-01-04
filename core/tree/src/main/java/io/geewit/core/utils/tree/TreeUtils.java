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
        nodes.sort(Comparator.comparing(TreeNode::getParentIds));
        List<N> treeRoots = new ArrayList<>();
        Map<Key, Stack<N>> stackMap = new HashMap<>();
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
            Stack<N> stack = stackMap.get(stackMapKey);
            if(stack == null) {
                stack = new Stack<>();
            }
            if (stack.empty()) {
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
    private static <N extends TreeNode<N, Key>, Key extends Serializable> N findParent(Stack<N> stack, Key parentId) {
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
}
