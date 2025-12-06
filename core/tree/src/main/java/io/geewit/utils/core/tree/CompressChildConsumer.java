package io.geewit.utils.core.tree;

import java.io.Serializable;

/**
 * 压缩子节点的sign, 根据父节点处理子节点的处理逻辑
 * @param <N> 树节点类型
 * @param <Key> 树节点标识类型
 * @author geewit
 */
@FunctionalInterface
public interface CompressChildConsumer<N extends SignedTreeNode<N, Key>, Key extends Serializable> {
    /**
     * 压缩子节点的sign, 根据父节点处理子节点的处理逻辑
     * @param parentNode 父节点
     * @param childNode 子节点
     */
    void accept(N parentNode, N childNode);
}