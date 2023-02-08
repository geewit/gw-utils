package io.geewit.core.utils.tree;

import java.io.Serializable;

@FunctionalInterface
public interface SignChildConsumer<N extends SignedTreeNode<N, Key>, Key extends Serializable> {
    void accept(N parentNode, N childNode, SimpleNodeSign<Key> simpleNodeSign);
}