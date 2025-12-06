package io.geewit.utils.core.tree;

import java.io.Serializable;

/**
 * 根据父节点的sign和传入参数的sign设置子节点的sign
 * @param <N> 树节点类型
 * @param <Key> 主键类型
 * @author geewit
 */
@FunctionalInterface
public interface SignChildConsumer<N extends SignedTreeNode<N, Key>, Key extends Serializable> {
    /**
     * 根据父节点的sign和传入参数的sign设置子节点的sign
     * @param parentNode 父节点
     * @param childNode 子节点
     * @param signParameter 传入参数的sign
     */
    void accept(N parentNode, N childNode, NodeSignParameter<Key> signParameter);
}