package io.geewit.core.utils.tree;

import java.io.Serializable;

/**
 * 压缩子节点的sign, 根据父节点处理子节点的处理逻辑
 * @author geewit
 */
@FunctionalInterface
public interface CompressChildConsumer<N extends SignedTreeNode<N, Key>, Key extends Serializable> {
    void accept(N parentNode, N childNode);
}