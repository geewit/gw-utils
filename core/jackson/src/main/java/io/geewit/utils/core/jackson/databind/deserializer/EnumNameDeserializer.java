package io.geewit.utils.core.jackson.databind.deserializer;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import io.geewit.utils.core.enums.EnumUtils;
import io.geewit.utils.core.enums.Name;

import java.lang.reflect.ParameterizedType;

/**
 * 枚举类型反序列化
 * @param <E> 枚举类型
 * @author geewit
 */
@SuppressWarnings({"unused"})
public abstract class EnumNameDeserializer<E extends Enum<E> & Name> extends ValueDeserializer<E> {
    /**
     * 构造函数
     */
    @SuppressWarnings({"unchecked"})
    public EnumNameDeserializer() {
        clazz = (Class <E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    private final Class<E> clazz;

    /**
     * 反序列化JSON数据为枚举对象
     *
     * @param parser JSON解析器，用于读取JSON数据
     * @param context 反序列化上下文，提供反序列化过程中的上下文信息
     * @return 返回根据JSON字符串值转换得到的枚举对象
     */
    @Override
    public E deserialize(JsonParser parser, DeserializationContext context) {
        // 获取JSON中的字符串值
        String name = parser.getValueAsString();
        // 根据枚举类型和名称查找对应的枚举常量
        return EnumUtils.forToken(clazz, name);
    }
}
