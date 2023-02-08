package io.geewit.core.utils.tree;

import java.io.Serializable;

@FunctionalInterface
public interface CompressChildConsumer<N extends SignedTreeNode<N, Key>, Key extends Serializable> {
    void accept(N parentNode, N childNode);
}