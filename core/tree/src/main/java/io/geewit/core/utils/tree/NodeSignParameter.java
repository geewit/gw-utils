package io.geewit.core.utils.tree;

import lombok.*;
import lombok.experimental.FieldNameConstants;

import java.io.Serializable;
import java.util.Objects;

/**
 * 树节点标记参数
 * @author geewit
 */
@Builder(toBuilder = true)
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
    @Builder.Default
    private Boolean transmissionDown = Boolean.TRUE;

    /**
     * 是否向上传递标记
     */
    @Builder.Default
    private Boolean transmissionUp = Boolean.TRUE;

    /**
     * 在lombok生成的builder方法里增加 transmission() 方法
     * @param <Key>
     */
    public static class NodeSignParameterBuilder<Key extends Serializable> {
        private boolean transmissionDown$set;
        private Boolean transmissionDown$value;
        private boolean transmissionUp$set;
        private Boolean transmissionUp$value;

        public NodeSignParameterBuilder<Key> transmission(Boolean transmission) {
            if (transmission != null) {
                this.transmissionDown$value = transmission;
                this.transmissionDown$set = true;
                this.transmissionUp$value = transmission;
                this.transmissionUp$set = true;
            }
            return this;
        }
    }

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
