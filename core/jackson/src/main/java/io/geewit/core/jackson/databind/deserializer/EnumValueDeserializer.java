package io.geewit.core.jackson.databind.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import io.geewit.core.utils.enums.EnumUtils;
import io.geewit.core.utils.enums.Value;

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
        valueType = (Class <N>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[2];
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
