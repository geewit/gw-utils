package io.geewit.utils.core.tree;

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
    private TreeUtils() {
    }

    /**
     * 构建树形结构数据
     *
     * @param <N> 树节点类型，必须继承TreeNode
     * @param <Key> 节点ID类型，必须实现Serializable接口
     * @param nodes 所有需要构建树形结构的节点列表
     * @param rootPredicate 判断根节点的谓词条件，可为空
     * @param rootId 指定的根节点ID，可为空
     * @return 构建好的树形结构根节点列表
     */
    public static <N extends TreeNode<N, Key>, Key extends Serializable> List<N> buildTree(List<N> nodes,
                                                                                           Predicate<N> rootPredicate,
                                                                                           Key rootId) {
        if(nodes == null || nodes.isEmpty()) {
            return Collections.emptyList();
        }
        List<N> roots = new ArrayList<>();
        // 创建节点映射表，用于快速查找父节点
        Map<Key, N> nodeMap = nodes.stream()
                .collect(Collectors.toMap(TreeNode::getId, node -> node, (oldValue, newValue) -> oldValue));
        try {
            // 遍历所有节点，构建父子关系
            nodes.forEach(node -> {
                if ((rootId != null && Objects.equals(node.id, rootId)) || (rootId == null && (node.parentId == null || (rootPredicate != null && rootPredicate.test(node))))) {
                    roots.add(node);
                } else {
                    N parent = nodeMap.get(node.parentId);
                    if (parent != null) {
                        parent.children.add(node);
                    }
                }
            });
            return roots;
        } finally {
            nodeMap.clear();
        }
    }

    /**
     * 构建树形结构数据。
     * Build tree structure from flat node list.
     *
     * @param <N>   节点类型，必须继承 {@code TreeNode<N, Key>}。
     *              Node type, must extend {@code TreeNode<N, Key>}.
     * @param <Key> 节点键值类型，必须实现 {@link Serializable}。
     *              Node key type, must implement {@link Serializable}.
     * @param nodes         待构建树形结构的节点列表
     *                      nodes to be built into a tree
     * @param rootPredicate 判断节点是否为根节点的谓词条件
     *                      predicate to determine whether a node is root
     * @return 构建完成的树形结构节点列表
     *         list of root nodes of the built tree
     */
    public static <N extends TreeNode<N, Key>, Key extends Serializable> List<N> buildTree(List<N> nodes,
                                                                                           Predicate<N> rootPredicate) {
        return buildTree(nodes, rootPredicate, null);
    }
}
