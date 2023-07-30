package io.geewit.core.jackson.databind.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

/**
 * 密码加密
 * @author geewit
 * @since  2016/12/20
 */
@SuppressWarnings({"unused"})
@JsonComponent
public class PasswordSerializer extends JsonSerializer<String> {
    public static final PasswordSerializer instance = new PasswordSerializer();

    @Override
    public void serialize(String value, JsonGenerator generator, SerializerProvider provider) throws IOException {
        // put your desired money style here
        if(value != null) {
            String decodePassword = repeat('*', value.length());
            generator.writeString(decodePassword);
        } else {
            generator.writeString("");
        }
    }

    private static String repeat(char value, int repeat) {
        if (repeat <= 0) {
            return "";
        } else {
            char[] buf = new char[repeat];
            for(int i = repeat - 1; i >= 0; --i) {
                buf[i] = value;
            }

            return new String(buf);
        }
    }
}
