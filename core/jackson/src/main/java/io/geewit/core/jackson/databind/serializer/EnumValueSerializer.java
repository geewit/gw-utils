package io.geewit.core.jackson.databind.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import io.geewit.core.utils.enums.Value;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

/**
 * 枚举类型序列化
 * @author geewit
 */
@SuppressWarnings({"unused"})
@JsonComponent
public class EnumValueSerializer extends JsonSerializer<Value<Number>> {
    public static final EnumValueSerializer instance = new EnumValueSerializer();

    @Override
    public void serialize(Value value, JsonGenerator generator, SerializerProvider provider) throws IOException {
        // put your desired money style here
        generator.writeString(String.valueOf(value.value()));
    }
}
