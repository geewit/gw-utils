package io.geewit.utils.core.jackson.databind.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import io.geewit.utils.core.enums.Value;

import java.io.IOException;

/**
 * 枚举类型序列化
 * @author geewit
 */
@SuppressWarnings({"unused"})
public class EnumValueSerializer<V extends Number> extends JsonSerializer<Value<V>> {
    public static final EnumValueSerializer<Integer> instanceOfInteger = new EnumValueSerializer<>();
    public static final EnumValueSerializer<Long> instanceOfLong = new EnumValueSerializer<>();

    @Override
    public void serialize(Value value, JsonGenerator generator, SerializerProvider provider) throws IOException {
        // put your desired money style here
        generator.writeString(String.valueOf(value.value().longValue()));
    }
}
