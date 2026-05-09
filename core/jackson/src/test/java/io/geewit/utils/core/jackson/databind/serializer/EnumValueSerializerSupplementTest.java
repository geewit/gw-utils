package io.geewit.utils.core.jackson.databind.serializer;

import io.geewit.utils.core.enums.Value;
import org.junit.jupiter.api.Test;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationContext;

import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Supplementary tests for EnumValueSerializer to improve coverage.
 * Focus on the value.value() == null branch.
 */
class EnumValueSerializerSupplementTest {

    private final ObjectMapper mapper = new ObjectMapper();

    private SerializationContext createContext() {
        return mapper._serializationContext();
    }

    /**
     * Test EnumValueSerializer with enum whose value() returns null.
     * This covers the branch at EnumValueSerializer.java:44 where value.value() == null.
     */
    @Test
    void enumValueSerializer_valueReturnsNull_writesNull() throws Exception {
        // Create an enum where value() returns null
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

    /**
     * Test EnumValueSerializer.instanceOfInteger with Integer value.
     */
    @Test
    void enumValueSerializer_integerValue() throws Exception {
        enum TestIntValueEnum implements Value<Integer> {
            A(100), B(200);

            private final int value;

            TestIntValueEnum(int value) {
                this.value = value;
            }

            @Override
            public Integer value() {
                return value;
            }
        }

        StringWriter writer = new StringWriter();
        JsonGenerator gen = mapper.createGenerator(writer);
        EnumValueSerializer.instanceOfInteger.serialize(TestIntValueEnum.A, gen, createContext());
        gen.close();
        
        assertEquals("\"100\"", writer.toString().trim());
    }

    /**
     * Test EnumValueSerializer with null enum.
     */
    @Test
    void enumValueSerializer_nullEnum_writesNull() throws Exception {
        StringWriter writer = new StringWriter();
        JsonGenerator gen = mapper.createGenerator(writer);
        EnumValueSerializer.instanceOfLong.serialize(null, gen, createContext());
        gen.close();
        
        assertEquals("null", writer.toString().trim());
    }
}
