package io.geewit.utils.core.tree;

import java.io.Serializable;

/**
 * 根据父节点的sign和传入参数的sign设置子节点的sign
 * @author geewit
 */
@FunctionalInterface
public interface SignChildConsumer<N extends SignedTreeNode<N, Key>, Key extends Serializable> {
    void accept(N parentNode, N childNode, NodeSignParameter<Key> signParameter);
}