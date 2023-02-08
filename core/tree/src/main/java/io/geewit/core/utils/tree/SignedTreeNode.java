package io.geewit.core.utils.tree;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.Objects;

/**
 * 树节点
 * @author geewit
 */
@SuperBuilder
@FieldNameConstants
@Setter
@Getter
public abstract class SignedTreeNode<N extends SignedTreeNode<N, Key>, Key extends Serializable>
        extends TreeNode<N, Key>
        implements NodeSign<Key> {
    /**
     * 标记
     */
    protected Integer sign;

    /**
     * 是否向下传递标记
     */
    protected Boolean transmission;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SignedTreeNode)) {
            return false;
        }
        SignedTreeNode<N, Key> that = (SignedTreeNode<N, Key>) o;
        return Objects.equals(super.id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.id);
    }
}
