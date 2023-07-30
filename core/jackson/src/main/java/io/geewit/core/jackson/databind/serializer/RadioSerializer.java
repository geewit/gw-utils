package io.geewit.core.jackson.databind.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * @author hf
 * @since  2016/12/20
 */
@SuppressWarnings({"unused"})
@JsonComponent
public class RadioSerializer extends JsonSerializer<BigDecimal> {
    public static final RadioSerializer instance = new RadioSerializer();

    @Override
    public void serialize(BigDecimal value, JsonGenerator generator, SerializerProvider provider) throws IOException {
        // put your desired money style here
        generator.writeString(value.setScale(4, BigDecimal.ROUND_HALF_UP).toString());
    }
}
