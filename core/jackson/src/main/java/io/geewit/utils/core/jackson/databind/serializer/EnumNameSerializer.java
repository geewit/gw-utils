package io.geewit.utils.core.jackson.databind.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
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
