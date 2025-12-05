package io.geewit.utils.core.jackson.databind.serializer;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.JsonSerializer;
import tools.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * @author hf
 * @since  2016/12/20
 */
@SuppressWarnings({"unused"})
public class RadioSerializer extends JsonSerializer<BigDecimal> {
    public static final RadioSerializer instance = new RadioSerializer();

    @Override
    public void serialize(BigDecimal value, JsonGenerator generator, SerializerProvider provider) throws IOException {
        // put your desired money style here
        generator.writeString(value.setScale(4, BigDecimal.ROUND_HALF_UP).toString());
    }
}
