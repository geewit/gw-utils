package io.geewit.utils.core.jackson.databind.deserializer;

import io.geewit.utils.core.enums.Name;
import io.geewit.utils.core.enums.Value;
import org.junit.jupiter.api.Test;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ObjectMapper;

import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

class JacksonDeserializersTest {

    private final ObjectMapper mapper = new ObjectMapper();

    private JsonParser createParser(String json) throws Exception {
        return mapper.createParser(new StringReader(json));
    }

    private DeserializationContext createContext() {
        return mapper._deserializationContext();
    }

    enum TestValueEnum implements Value<Integer> {
        ONE(1), TWO(2), THREE(3);

        private final int value;

        TestValueEnum(int value) {
            this.value = value;
        }

        @Override
        public Integer value() {
            return value;
        }
    }

    static class TestValueDeserializer extends EnumValueDeserializer<TestValueEnum, Integer> {
    }

    @Test
    void enumValueDeserializer_integerValue() throws Exception {
        TestValueDeserializer deserializer = new TestValueDeserializer();
        JsonParser parser = createParser("\"2\"");
        parser.nextToken();
        TestValueEnum result = deserializer.deserialize(parser, createContext());
        assertEquals(TestValueEnum.TWO, result);
    }

    @Test
    void enumValueDeserializer_invalidNumberFormat() throws Exception {
        TestValueDeserializer deserializer = new TestValueDeserializer();
        JsonParser parser = createParser("\"not-a-number\"");
        parser.nextToken();
        assertNull(deserializer.deserialize(parser, createContext()));
    }

    enum TestLongValueEnum implements Value<Long> {
        FIRST(1L), SECOND(2L);

        private final long value;

        TestLongValueEnum(long value) {
            this.value = value;
        }

        @Override
        public Long value() {
            return value;
        }
    }

    static class TestLongValueDeserializer extends EnumValueDeserializer<TestLongValueEnum, Long> {
    }

    @Test
    void enumValueDeserializer_longValue() throws Exception {
        TestLongValueDeserializer deserializer = new TestLongValueDeserializer();
        JsonParser parser = createParser("\"1\"");
        parser.nextToken();
        TestLongValueEnum result = deserializer.deserialize(parser, createContext());
        assertEquals(TestLongValueEnum.FIRST, result);
    }

    enum TestNameEnum implements Name {
        FOO, BAR, BAZ;

        @Override
        public String getName() {
            return name().toLowerCase();
        }
    }

    static class TestNameDeserializer extends EnumNameDeserializer<TestNameEnum> {
    }

    @Test
    void enumNameDeserializer_byName() throws Exception {
        TestNameDeserializer deserializer = new TestNameDeserializer();
        JsonParser parser = createParser("\"foo\"");
        parser.nextToken();
        TestNameEnum result = deserializer.deserialize(parser, createContext());
        assertEquals(TestNameEnum.FOO, result);
    }

    @Test
    void enumNameDeserializer_byEnumName() throws Exception {
        TestNameDeserializer deserializer = new TestNameDeserializer();
        JsonParser parser = createParser("\"BAR\"");
        parser.nextToken();
        TestNameEnum result = deserializer.deserialize(parser, createContext());
        assertEquals(TestNameEnum.BAR, result);
    }
}
