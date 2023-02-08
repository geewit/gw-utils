package io.geewit.core.utils.tree;

import java.io.Serializable;

@FunctionalInterface
public interface SignNodeConsumer<N extends SignedTreeNode<N, Key>, Key extends Serializable> {
    void accept(N node, SimpleNodeSign<Key> simpleNodeSign, boolean compress);
}