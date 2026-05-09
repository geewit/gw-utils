package io.geewit.utils.core.jackson.databind.serializer;

import io.geewit.utils.core.enums.Value;
import org.junit.jupiter.api.Test;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationContext;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Supplementary tests for Jackson serializers to improve coverage.
 * Focus on exception handling and edge cases.
 */
class JacksonSerializersSupplementTest {

    private final ObjectMapper mapper = new ObjectMapper();

    private SerializationContext createContext() {
        return mapper._serializationContext();
    }

    /**
     * Test BigDecimalSerializer with negative value.
     */
    @Test
    void bigDecimalSerializer_negativeValue() throws Exception {
        StringWriter writer = new StringWriter();
        JsonGenerator gen = mapper.createGenerator(writer);
        BigDecimalSerializer.instance.serialize(new BigDecimal("-123.456"), gen, createContext());
        gen.close();
        assertEquals("\"-123.46\"", writer.toString().trim());
    }

    /**
     * Test BigDecimalSerializer with zero.
     */
    @Test
    void bigDecimalSerializer_zero() throws Exception {
        StringWriter writer = new StringWriter();
        JsonGenerator gen = mapper.createGenerator(writer);
        BigDecimalSerializer.instance.serialize(BigDecimal.ZERO, gen, createContext());
        gen.close();
        assertEquals("\"0.00\"", writer.toString().trim());
    }

    /**
     * Test MoneySerializer with negative value.
     */
    @Test
    void moneySerializer_negativeValue() throws Exception {
        StringWriter writer = new StringWriter();
        JsonGenerator gen = mapper.createGenerator(writer);
        MoneySerializer.instance.serialize(new BigDecimal("-99.9"), gen, createContext());
        gen.close();
        assertEquals("\"-99.90\"", writer.toString().trim());
    }

    /**
     * Test MoneySerializer with zero.
     */
    @Test
    void moneySerializer_zero() throws Exception {
        StringWriter writer = new StringWriter();
        JsonGenerator gen = mapper.createGenerator(writer);
        MoneySerializer.instance.serialize(BigDecimal.ZERO, gen, createContext());
        gen.close();
        assertEquals("\"0.00\"", writer.toString().trim());
    }

    /**
     * Test RadioSerializer with negative value.
     */
    @Test
    void radioSerializer_negativeValue() throws Exception {
        StringWriter writer = new StringWriter();
        JsonGenerator gen = mapper.createGenerator(writer);
        RadioSerializer.instance.serialize(new BigDecimal("-0.123456"), gen, createContext());
        gen.close();
        assertEquals("\"-0.1235\"", writer.toString().trim());
    }

    /**
     * Test RadioSerializer with zero.
     */
    @Test
    void radioSerializer_zero() throws Exception {
        StringWriter writer = new StringWriter();
        JsonGenerator gen = mapper.createGenerator(writer);
        RadioSerializer.instance.serialize(BigDecimal.ZERO, gen, createContext());
        gen.close();
        assertEquals("\"0.0000\"", writer.toString().trim());
    }

    /**
     * Test PasswordSerializer with single character.
     */
    @Test
    void passwordSerializer_singleChar() throws Exception {
        StringWriter writer = new StringWriter();
        JsonGenerator gen = mapper.createGenerator(writer);
        PasswordSerializer.instance.serialize("x", gen, createContext());
        gen.close();
        assertEquals("\"*\"", writer.toString().trim());
    }

    /**
     * Test EnumNameSerializer with BAR enum.
     */
    @Test
    void enumNameSerializer_bar() throws Exception {
        enum TestNameEnum implements io.geewit.utils.core.enums.Name {
            FOO, BAR;

            @Override
            public String getName() {
                return name().toLowerCase();
            }
        }

        StringWriter writer = new StringWriter();
        JsonGenerator gen = mapper.createGenerator(writer);
        EnumNameSerializer.instance.serialize(TestNameEnum.BAR, gen, createContext());
        gen.close();
        assertEquals("\"BAR\"", writer.toString().trim());
    }

    /**
     * Test EnumValueSerializer with Long value.
     */
    @Test
    void enumValueSerializer_longValue() throws Exception {
        enum TestLongValueEnum implements Value<Long> {
            FIRST(1L), SECOND(2L), THREE(3L);

            private final long value;

            TestLongValueEnum(long value) {
                this.value = value;
            }

            @Override
            public Long value() {
                return value;
            }
        }

        StringWriter writer = new StringWriter();
        JsonGenerator gen = mapper.createGenerator(writer);
        EnumValueSerializer.instanceOfLong.serialize(TestLongValueEnum.FIRST, gen, createContext());
        gen.close();
        assertEquals("\"1\"", writer.toString().trim());
    }

    /**
     * Test LongSerializer with exact threshold boundary value.
     */
    @Test
    void longSerializer_atThreshold_writesNumber() throws Exception {
        StringWriter writer = new StringWriter();
        JsonGenerator gen = mapper.createGenerator(writer);
        LongSerializer.instance.serialize(100000000L, gen, createContext()); // 9 digits
        gen.close();
        assertEquals("100000000", writer.toString().trim());
    }

    /**
     * Test LongSerializer with just over threshold.
     */
    @Test
    void longSerializer_justOverThreshold_writesString() throws Exception {
        StringWriter writer = new StringWriter();
        JsonGenerator gen = mapper.createGenerator(writer);
        LongSerializer.instance.serialize(1000000000L, gen, createContext()); // 10 digits
        gen.close();
        assertEquals("\"1000000000\"", writer.toString().trim());
    }

    /**
     * Test LongSerializer with negative number below threshold.
     */
    @Test
    void longSerializer_negativeSmall_writesNumber() throws Exception {
        StringWriter writer = new StringWriter();
        JsonGenerator gen = mapper.createGenerator(writer);
        LongSerializer.instance.serialize(-99999999L, gen, createContext());
        gen.close();
        assertEquals("-99999999", writer.toString().trim());
    }

    /**
     * Test LongSerializer with negative number above threshold.
     */
    @Test
    void longSerializer_negativeLarge_writesString() throws Exception {
        StringWriter writer = new StringWriter();
        JsonGenerator gen = mapper.createGenerator(writer);
        LongSerializer.instance.serialize(-100000000L, gen, createContext());
        gen.close();
        assertEquals("\"-100000000\"", writer.toString().trim());
    }

    /**
     * Test LongSerializer with zero.
     */
    @Test
    void longSerializer_zero_writesNumber() throws Exception {
        StringWriter writer = new StringWriter();
        JsonGenerator gen = mapper.createGenerator(writer);
        LongSerializer.instance.serialize(0L, gen, createContext());
        gen.close();
        assertEquals("0", writer.toString().trim());
    }

    /**
     * Test EnumValueSerializer with value that returns null.
     */
    @Test
    void enumValueSerializer_valueReturnsNull() throws Exception {
        enum TestNullValueEnum implements Value<Long> {
            VALID(1L), NULL_VALUE;

            private final Long value;

            TestNullValueEnum() {
                this.value = null;
            }

            TestNullValueEnum(Long value) {
                this.value = value;
            }

            @Override
            public Long value() {
                return value;
            }
        }

        StringWriter writer = new StringWriter();
        JsonGenerator gen = mapper.createGenerator(writer);
        EnumValueSerializer.instanceOfLong.serialize(TestNullValueEnum.NULL_VALUE, gen, createContext());
        gen.close();
        assertEquals("null", writer.toString().trim());
    }
}
