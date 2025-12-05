package io.geewit.utils.core.jackson.databind.serializer;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.JsonSerializer;
import tools.jackson.databind.SerializerProvider;
import io.geewit.utils.core.enums.Name;

import java.io.IOException;

/**
 * 枚举类型序列化
 * @author geewit
 */
@SuppressWarnings({"unused"})
public class EnumNameSerializer extends JsonSerializer<Name> {
    public static final EnumNameSerializer instance = new EnumNameSerializer();

    @Override
    public void serialize(Name name, JsonGenerator generator, SerializerProvider provider) throws IOException {
        // put your desired money style here
        generator.writeString(name.toString());
    }
}
