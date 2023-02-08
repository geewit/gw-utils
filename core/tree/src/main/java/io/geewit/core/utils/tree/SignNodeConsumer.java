package io.geewit.core.utils.tree;

import java.io.Serializable;

/**
 * 设置节点的sign
 * @author geewit
 */
@FunctionalInterface
public interface SignNodeConsumer<N extends SignedTreeNode<N, Key>, Key extends Serializable> {
    void accept(N node, NodeSignParameter<Key> simpleNodeSign, boolean compress);
}