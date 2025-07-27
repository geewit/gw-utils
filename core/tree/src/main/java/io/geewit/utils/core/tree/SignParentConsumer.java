package io.geewit.utils.core.tree;

import java.io.Serializable;

/**
 * 根据所有下级子节点的sign设置上级父节点的sign
 * @author geewit
 */
@FunctionalInterface
public interface SignParentConsumer<N extends SignedTreeNode<N, Key>, Key extends Serializable> {
    void accept(N parentNode, int allChildrenSign, boolean overwrite);
}