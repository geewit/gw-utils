package io.geewit.utils.core.jackson.databind.serializer;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.JsonSerializer;
import tools.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;

/**
 *
 * @author geewit
 * @since  2016/12/20
 */
@SuppressWarnings({"unused"})
public class MoneySerializer extends JsonSerializer<BigDecimal> {
    public static final MoneySerializer instance = new MoneySerializer();

    @Override
    public void serialize(BigDecimal value, JsonGenerator generator, SerializerProvider provider) throws IOException {
        // put your desired money style here
        generator.writeString(value.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
    }
}
