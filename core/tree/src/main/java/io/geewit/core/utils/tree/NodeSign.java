package io.geewit.core.utils.tree;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 树节点标记
 * @author geewit
 */
@Setter
@Getter
@AllArgsConstructor
public class NodeSign<Key extends Serializable> {
    /**
     * 节点id
     */
    protected Key key;
    /**
     * 标记
     */
    protected Integer sign;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NodeSign)) {
            return false;
        }

        NodeSign<?> that = (NodeSign<?>) o;

        if (!this.key.equals(that.key)) {
            return false;
        }
        return this.sign.equals(that.sign);
    }

    @Override
    public int hashCode() {
        int result = this.key.hashCode();
        result = 31 * result + this.sign.hashCode();
        return result;
    }
}
