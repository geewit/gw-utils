package io.geewit.utils.core.jackson.databind.deserializer;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonDeserializer;
import io.geewit.utils.core.enums.EnumUtils;
import io.geewit.utils.core.enums.Value;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;

/**
 * 枚举类型反序列化
 * @author geewit
 */
@SuppressWarnings({"unused"})
public abstract class EnumValueDeserializer<E extends Enum<E> & Value<N>, N extends Number> extends JsonDeserializer<E> {
    @SuppressWarnings({"unchecked"})
    public EnumValueDeserializer() {
        clazz = (Class <E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        valueType = (Class <N>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
    }

    private final Class<E> clazz;
    private final Class<N> valueType;

    @Override
    public E deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        String token = parser.getText();
        try {
            if (valueType.isAssignableFrom(Long.class)) {
                long value = Long.parseLong(token);
                return EnumUtils.forValue(clazz, (N)Long.valueOf(value));
            } else if (valueType.isAssignableFrom(Integer.class)) {
                int value = Integer.parseInt(token);
                return EnumUtils.forValue(clazz, (N)Integer.valueOf(value));
            } else {
                return null;
            }

        } catch (NumberFormatException e) {
            return null;
        }
    }
}
