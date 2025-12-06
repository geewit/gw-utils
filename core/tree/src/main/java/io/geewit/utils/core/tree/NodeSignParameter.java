package io.geewit.utils.core.tree;

import lombok.*;
import lombok.experimental.FieldNameConstants;

import java.io.Serializable;
import java.util.Objects;

/**
 * 树节点标记参数
 * @param <Key> 主键类型
 * @author geewit
 */
@Builder(toBuilder = true)
@AllArgsConstructor
@FieldNameConstants
@Setter
@Getter
public class NodeSignParameter<Key extends Serializable> {
    private NodeSignParameter() {
    }
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
     * @param <Key> 主键类型
     */
    public static class NodeSignParameterBuilder<Key extends Serializable> {
        private NodeSignParameterBuilder() {
        }

        private boolean transmissionDown$set;
        private Boolean transmissionDown$value;
        private boolean transmissionUp$set;
        private Boolean transmissionUp$value;

        /**
         * 设置传输参数的构建器方法
         *
         * @param transmission 传输参数值，如果为null则不进行设置
         * @return 返回当前NodeSignParameterBuilder实例，支持链式调用
         */
        public NodeSignParameterBuilder<Key> transmission(Boolean transmission) {
            // 当传输参数不为null时，同时设置上行和下行的传输参数值
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
