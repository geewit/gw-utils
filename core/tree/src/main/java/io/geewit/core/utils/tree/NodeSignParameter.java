package io.geewit.core.utils.tree;

import lombok.*;
import lombok.experimental.FieldNameConstants;

import java.io.Serializable;
import java.util.Objects;

/**
 * 树节点标记参数
 * @author geewit
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@Setter
@Getter
public class NodeSignParameter<Key extends Serializable> {
    /**
     * 节点id
     */
    private Key id;
    /**
     * 标记
     */
    private Integer sign;

    /**
     * 是否向下传递标记
     */
    private Boolean transmission;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NodeSignParameter)) {
            return false;
        }

        NodeSignParameter<?> that = (NodeSignParameter<?>) o;

        return Objects.equals(this.id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
