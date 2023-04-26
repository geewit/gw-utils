package io.geewit.core.utils.tree;

import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 树的工具类
 * @author geewit
 */
@Slf4j
public class TreeUtils {

    public static <N extends TreeNode<N, Key>, Key extends Serializable> List<N> buildTree(List<N> nodes, Predicate<Key> rootPredicate, Key rootId) {
        if(nodes == null || nodes.isEmpty()) {
            return Collections.emptyList();
        }
        List<N> roots = new ArrayList<>();
        Map<Key, N> nodeMap = nodes.stream().collect(Collectors.toMap(TreeNode::getId, node -> node));
        nodes.forEach(node -> {

            if ((rootId != null && Objects.equals(node.id, rootId)) || (rootId == null && (node.parentId == null || (rootPredicate != null && rootPredicate.test(node.parentId))))) {
                roots.add(node);
            } else {
                N parent = nodeMap.get(node.parentId);
                if (parent != null) {
                    parent.children.add(node);
                }
            }
        });
        return roots;
    }

    public static <N extends TreeNode<N, Key>, Key extends Serializable> List<N> buildTree(List<N> nodes, Predicate<Key> rootPredicate) {
        return buildTree(nodes, rootPredicate, null);
    }

}
