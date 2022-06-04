package io.geewit.core.utils.tree;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

/**
 * 树节点
 * @author geewit
 */
@Setter
@Getter
public abstract class SignedTreeNode<N extends SignedTreeNode<N, Key>, Key extends Serializable> extends TreeNode<N, Key> {
    /**
     * 标记
     */
    protected Integer sign;

    public void sign(int sign) {
        if (this.sign == null) {
            this.sign = sign;
        } else {
            this.sign = this.sign | sign;
        }
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
        return super.id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.id);
    }
}
