package io.geewit.core.utils.tree;

import lombok.*;

import java.io.Serializable;

/**
 * 树节点标记
 * @author geewit
 */
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SimpleNodeSign<Key extends Serializable> implements NodeSign<Key> {
    /**
     * 节点id
     */
    private Key id;
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

        if (!this.id.equals(that.id)) {
            return false;
        }
        return this.sign.equals(that.sign);
    }

    @Override
    public int hashCode() {
        int result = this.id.hashCode();
        result = 31 * result + this.sign.hashCode();
        return result;
    }
}
