package io.geewit.utils.core.tree;

import java.io.Serializable;

/**
 * 根据所有下级子节点的sign设置上级父节点的sign
 * @param <N> 树节点类型
 * @param <Key> 主键类型
 * @author geewit
 */
@FunctionalInterface
public interface SignParentConsumer<N extends SignedTreeNode<N, Key>, Key extends Serializable> {
    /**
     * 根据所有下级子节点的sign设置上级父节点的sign
     * @param parentNode 父节点
     * @param allChildrenSign 所有子节点的sign
     * @param overwrite 是否覆盖
     */
    void accept(N parentNode, int allChildrenSign, boolean overwrite);
}