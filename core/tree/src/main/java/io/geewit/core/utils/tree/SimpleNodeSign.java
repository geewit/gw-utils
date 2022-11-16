package io.geewit.core.utils.tree;

import lombok.*;
import lombok.experimental.FieldNameConstants;

import java.io.Serializable;
import java.util.Objects;

/**
 * 树节点标记
 * @author geewit
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@Setter
@Getter
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

        return Objects.equals(this.id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
