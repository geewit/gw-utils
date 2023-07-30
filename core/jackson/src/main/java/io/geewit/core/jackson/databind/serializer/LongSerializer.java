package io.geewit.core.jackson.databind.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

/**
 *  Long 序列化为 String
 *  @author geewit
 */
@SuppressWarnings({"unused"})
@JsonComponent
public class LongSerializer extends StdSerializer<Long> {
    private final static Logger logger = LoggerFactory.getLogger(LongSerializer.class);

    public static final LongSerializer instance = new LongSerializer();

    private final int maxLength;

    public LongSerializer() {
        super(Long.class);
        this.maxLength = (int)Math.log10(Integer.MAX_VALUE);
    }

    @Override
    public void serialize(Long value, JsonGenerator generator, SerializerProvider provider) {
        if (value.toString().length() > this.maxLength) {
            try {
                generator.writeString(value.toString());
            } catch (IOException e) {
                logger.warn(e.getMessage());
            }
        } else {
            try {
                generator.writeNumber(value);
            } catch (IOException e) {
                logger.warn(e.getMessage());
            }
        }
    }
}
