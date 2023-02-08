package io.geewit.core.utils.tree;

import java.io.Serializable;

@FunctionalInterface
public interface SignParentConsumer<N extends SignedTreeNode<N, Key>, Key extends Serializable> {
    void accept(N parentNode, int allChildrenSign, boolean overwrite);
}