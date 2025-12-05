package io.geewit.utils.core.jackson.databind.deserializer;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import io.geewit.utils.core.enums.EnumUtils;
import io.geewit.utils.core.enums.Value;

import java.lang.reflect.ParameterizedType;

/**
 * 枚举类型反序列化
 * @param <E> 枚举类型
 * @param <N> 数字类型
 * @author geewit
 */
@SuppressWarnings({"unused"})
public abstract class EnumValueDeserializer<E extends Enum<E> & Value<N>, N extends Number> extends ValueDeserializer<E> {
    /**
     * 构造函数
     */
    @SuppressWarnings({"unchecked"})
    public EnumValueDeserializer() {
        clazz = (Class <E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        valueType = (Class <N>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
    }

    private final Class<E> clazz;
    private final Class<N> valueType;


    /**
     * 反序列化JSON数据为枚举对象
     *
     * @param parser JSON解析器，用于读取JSON数据
     * @param context 反序列化上下文，提供反序列化过程中的辅助信息
     * @return 返回反序列化后的枚举对象，如果反序列化失败则返回null
     */
    @Override
    public E deserialize(JsonParser parser, DeserializationContext context) {
        String token = parser.getValueAsString();
        try {
            // 根据值类型的不同，将字符串转换为相应的数值类型，并通过EnumUtils获取对应的枚举实例
            if (valueType.isAssignableFrom(Long.class)) {
                long value = Long.parseLong(token);
                return EnumUtils.forValue(clazz, valueType.cast(value));
            } else if (valueType.isAssignableFrom(Integer.class)) {
                int value = Integer.parseInt(token);
                return EnumUtils.forValue(clazz, valueType.cast(value));
            } else {
                return null;
            }

        } catch (NumberFormatException e) {
            return null;
        }
    }
}
