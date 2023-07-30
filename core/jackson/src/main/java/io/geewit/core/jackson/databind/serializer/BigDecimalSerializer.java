package io.geewit.core.jackson.databind.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * BigDecimal类型反序列化
 * @author geewit
 * @since  2016/12/20
 */
@SuppressWarnings({"unused"})
@JsonComponent
public class BigDecimalSerializer extends JsonSerializer<BigDecimal> {
    public static final BigDecimalSerializer instance = new BigDecimalSerializer();

    @Override
    public void serialize(BigDecimal value, JsonGenerator generator, SerializerProvider provider) throws IOException {
        // put your desired money style here
        generator.writeString(value.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
    }
}
