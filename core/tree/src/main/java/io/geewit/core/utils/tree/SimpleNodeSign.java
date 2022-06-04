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
public class SimpleNodeSign<Key extends Serializable> implements NodeSign<Key> {
    /**
     * 节点id
     */
    private Key key;
    /**
     * 标记
     */
    private Integer sign;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SimpleNodeSign)) {
            return false;
        }

        SimpleNodeSign<?> that = (SimpleNodeSign<?>) o;

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
