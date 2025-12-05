package io.geewit.utils.core.jackson.databind.serializer;

import io.geewit.utils.core.enums.Value;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

/**
 * 枚举类型序列化 / Enum value serializer
 * @param <V> 枚举值类型
 * @author geewit
 */
@SuppressWarnings({"unused"})
public class EnumValueSerializer<V extends Number> extends ValueSerializer<Value<V>> {
    private EnumValueSerializer() {
    }

    /**
     * 默认实例
     */
    public static final EnumValueSerializer<Integer> instanceOfInteger = new EnumValueSerializer<>();

    /**
     * 默认实例
     */
    public static final EnumValueSerializer<Long>    instanceOfLong    = new EnumValueSerializer<>();


    /**
     * 序列化Value对象到JSON格式
     *
     * @param value 待序列化的Value对象，包含泛型类型V的值
     * @param generator JSON生成器，用于输出序列化结果
     * @param context 序列化上下文，提供序列化过程中的环境信息
     * @throws JacksonException 当序列化过程中发生错误时抛出
     */
    @Override
    public void serialize(Value<V> value,
                          JsonGenerator generator,
                          SerializationContext context) throws JacksonException {

        // 处理null值情况
        if (value == null || value.value() == null) {
            // 简单处理 null：直接写 null，也可以改成 context.defaultSerializeNull(generator)
            generator.writeNull();
            return;
        }

        // 将值统一转换为long类型后以字符串形式输出，保持与原有实现的一致性
        generator.writeString(String.valueOf(value.value().longValue()));
    }
}
