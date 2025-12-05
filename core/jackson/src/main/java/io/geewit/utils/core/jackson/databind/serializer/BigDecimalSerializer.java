package io.geewit.utils.core.jackson.databind.serializer;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.JsonSerializer;
import tools.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * BigDecimal类型反序列化
 * @author geewit
 * @since  2016/12/20
 */
@SuppressWarnings({"unused"})
public class BigDecimalSerializer extends JsonSerializer<BigDecimal> {
    public static final BigDecimalSerializer instance = new BigDecimalSerializer();

    @Override
    public void serialize(BigDecimal value, JsonGenerator generator, SerializerProvider provider) throws IOException {
        // put your desired money style here
        generator.writeString(value.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
    }
}
