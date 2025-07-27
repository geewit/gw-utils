package io.geewit.utils.core.tree;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.io.Serializable;
import java.util.Objects;

/**
 * 带sign的树节点
 * @author geewit
 */
@FieldNameConstants
@Setter
@Getter
public abstract class SignedTreeNode<N extends SignedTreeNode<N, Key>, Key extends Serializable>
        extends TreeNode<N, Key> {
    /**
     * 标记
     */
    protected Integer sign;

    /**
     * 是否向下传递标记
     */
    protected Boolean transmission;

    @Override
    public void clear() {
        super.clear();
        this.sign = null;
        this.transmission = null;
    }

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
