package io.geewit.utils.core.jackson.databind.deserializer;

import io.geewit.utils.core.enums.Name;
import io.geewit.utils.core.enums.Value;
import org.junit.jupiter.api.Test;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ObjectMapper;

import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Supplementary tests for Jackson deserializers to improve coverage.
 */
class JacksonDeserializersSupplementTest {

    private final ObjectMapper mapper = new ObjectMapper();

    private JsonParser createParser(String json) throws Exception {
        return mapper.createParser(new StringReader(json));
    }

    private DeserializationContext createContext() {
        return mapper._deserializationContext();
    }

    /**
     * Test EnumNameDeserializer throws exception for invalid token
     */
    @Test
    void enumNameDeserializer_invalidToken_throwsException() throws Exception {
        enum TestNameEnum implements Name {
            FOO, BAR, BAZ;

            @Override
            public String getName() {
                return name().toLowerCase();
            }
        }

        JacksonDeserializersTest.TestNameDeserializer deserializer = new JacksonDeserializersTest.TestNameDeserializer();
        JsonParser parser = createParser("\"invalid-token\"");
        parser.nextToken();

        assertThrows(IllegalArgumentException.class, () -> {
            deserializer.deserialize(parser, createContext());
        });
    }

    /**
     * Test EnumNameDeserializer with null token
     */
    @Test
    void enumNameDeserializer_nullToken_returnsNull() throws Exception {
        enum TestNameEnum implements Name {
            FOO, BAR, BAZ;

            @Override
            public String getName() {
                return name().toLowerCase();
            }
        }

        JacksonDeserializersTest.TestNameDeserializer deserializer = new JacksonDeserializersTest.TestNameDeserializer();
        JsonParser parser = createParser("null");
        parser.nextToken();

        // forToken with null returns null
        assertNull(deserializer.deserialize(parser, createContext()));
    }

    /**
     * Test EnumValueDeserializer with null value
     */
    @Test
    void enumValueDeserializer_nullValue_returnsNull() throws Exception {
        JacksonDeserializersTest.TestValueDeserializer deserializer = new JacksonDeserializersTest.TestValueDeserializer();
        JsonParser parser = createParser("null");
        parser.nextToken();

        // null token leads to NumberFormatException which is caught, returning null
        assertNull(deserializer.deserialize(parser, createContext()));
    }

    /**
     * Test EnumValueDeserializer with empty string
     */
    @Test
    void enumValueDeserializer_emptyString_returnsNull() throws Exception {
        JacksonDeserializersTest.TestValueDeserializer deserializer = new JacksonDeserializersTest.TestValueDeserializer();
        JsonParser parser = createParser("\"\"");
        parser.nextToken();

        // empty string leads to NumberFormatException which is caught, returning null
        assertNull(deserializer.deserialize(parser, createContext()));
    }

    /**
     * Test EnumValueDeserializer with unsupported valueType (Double) returns null.
     * This requires creating an enum that implements Value<Double>.
     */
    @Test
    void enumValueDeserializer_unsupportedValueType_returnsNull() throws Exception {
        enum TestDoubleValueEnum implements Value<Double> {
            SMALL(0.1), MEDIUM(0.5), LARGE(1.0);

            private final Double value;

            TestDoubleValueEnum(double value) {
                this.value = value;
            }

            @Override
            public Double value() {
                return value;
            }
        }

        // Create a deserializer subclass with Double valueType
        @SuppressWarnings("unchecked")
        EnumValueDeserializer<TestDoubleValueEnum, Double> deserializer =
                new EnumValueDeserializer<TestDoubleValueEnum, Double>() {};

        JsonParser parser = createParser("\"0.5\"");
        parser.nextToken();

        // Double is not assignable from Long or Integer, so it should return null
        assertNull(deserializer.deserialize(parser, createContext()));
    }
}
