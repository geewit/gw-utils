package io.geewit.utils.core.jackson.databind.serializer;

import org.junit.jupiter.api.Test;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for Jackson serializers exception handling to improve coverage.
 * Uses Mockito to mock JsonGenerator and simulate exception paths.
 */
class JacksonSerializersExceptionTest {

    private final ObjectMapper mapper = new ObjectMapper();

    private SerializationContext createContext() {
        return mapper._serializationContext();
    }

    /**
     * Test LongSerializer exception handling - when JsonGenerator.writeNumber throws.
     * This covers the catch block at LongSerializer.java:69-71.
     * Note: 1234567890 is 10 digits, so it triggers the writeString path.
     */
    @Test
    void longSerializer_writeNumberException_catchesAndLogs() throws Exception {
        JsonGenerator mockGenerator = mock(JsonGenerator.class);
        
        // 100000000 is 9 digits, triggers writeNumber path
        doThrow(new RuntimeException("Simulated write error"))
                .when(mockGenerator).writeNumber(anyLong());
        doNothing().when(mockGenerator).close();

        // Should catch exception and log warning, not throw
        LongSerializer.instance.serialize(100000000L, mockGenerator, createContext());
        
        // Verify the mock was called and exception was caught (no exception propagated)
        verify(mockGenerator).writeNumber(100000000L);
    }

    /**
     * Test LongSerializer exception handling when writeString throws (large number path).
     */
    @Test
    void longSerializer_writeStringException_catchesAndLogs() throws Exception {
        JsonGenerator mockGenerator = mock(JsonGenerator.class);
        
        // Make writeString throw an exception - this is called for large numbers (10+ digits)
        doThrow(new RuntimeException("Simulated write error"))
                .when(mockGenerator).writeString(anyString());
        doNothing().when(mockGenerator).close();

        // 12345678901 is 11 digits, triggers writeString path
        LongSerializer.instance.serialize(12345678901L, mockGenerator, createContext());
        
        verify(mockGenerator).writeString("12345678901");
    }
}
