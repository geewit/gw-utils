package io.geewit.utils.core.jackson.databind.deserializer;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonDeserializer;
import io.geewit.utils.core.enums.EnumUtils;
import io.geewit.utils.core.enums.Name;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;

/**
 * 枚举类型反序列化
 * @author geewit
 */
@SuppressWarnings({"unused"})
public abstract class EnumNameDeserializer<E extends Enum<E> & Name> extends JsonDeserializer<E> {
    @SuppressWarnings({"unchecked"})
    public EnumNameDeserializer() {
        clazz = (Class <E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    private final Class<E> clazz;

    @Override
    public E deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        String name = parser.getText();
        return EnumUtils.forToken(clazz, name);
    }
}
