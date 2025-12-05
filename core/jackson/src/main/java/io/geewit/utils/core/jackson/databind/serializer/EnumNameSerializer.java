package io.geewit.utils.core.jackson.databind.serializer;

import io.geewit.utils.core.enums.Name;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

/**
 * 枚举类型序列化 / Enum serializer
 * @author geewit
 */
@SuppressWarnings({"unused"})
public class EnumNameSerializer extends ValueSerializer<Name> {
    private EnumNameSerializer() {
    }

    /**
     * 枚举类型序列化 / Enum serializer
     */
    public static final EnumNameSerializer instance = new EnumNameSerializer();

    /**
     * 序列化Name对象为JSON格式
     *
     * @param value 需要序列化的Name对象，可能为null
     * @param generator JSON生成器，用于输出序列化结果
     * @param context 序列化上下文，提供序列化相关的配置和工具方法
     * @throws JacksonException 当序列化过程中发生错误时抛出
     */
    @Override
    public void serialize(Name value,
                          JsonGenerator generator,
                          SerializationContext context) throws JacksonException {

        if (value == null) {
            // 处理null值情况，直接写入null
            generator.writeNull();
            return;
        }

        // 将Name对象转换为字符串并写入JSON
        generator.writeString(value.toString());
    }

}
