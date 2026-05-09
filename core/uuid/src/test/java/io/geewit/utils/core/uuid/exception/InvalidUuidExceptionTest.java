package io.geewit.utils.core.uuid.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InvalidUuidExceptionTest {

    @Test
    void constructor_withMessage() {
        InvalidUuidException ex = new InvalidUuidException("test message");
        assertEquals("test message", ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test
    void constructor_withMessageAndCause() {
        Throwable cause = new RuntimeException("cause");
        InvalidUuidException ex = new InvalidUuidException("test message", cause);
        assertEquals("test message", ex.getMessage());
        assertEquals(cause, ex.getCause());
    }

    @Test
    void newInstance_withString() {
        InvalidUuidException ex = InvalidUuidException.newInstance("invalid");
        assertTrue(ex.getMessage().contains("Invalid UUID"));
        assertTrue(ex.getMessage().contains("\"invalid\""));
    }

    @Test
    void newInstance_withCharArray() {
        InvalidUuidException ex = InvalidUuidException.newInstance(new char[]{'t', 'e', 's', 't'});
        assertTrue(ex.getMessage().contains("Invalid UUID"));
        assertTrue(ex.getMessage().contains("test"));
    }

    @Test
    void newInstance_withByteArray() {
        byte[] bytes = {(byte) 0x01, (byte) 0x02};
        InvalidUuidException ex = InvalidUuidException.newInstance(bytes);
        assertTrue(ex.getMessage().contains("Invalid UUID"));
        assertTrue(ex.getMessage().contains("[1, 2]"));
    }

    @Test
    void newInstance_withNull() {
        InvalidUuidException ex = InvalidUuidException.newInstance(null);
        assertTrue(ex.getMessage().contains("Invalid UUID"));
        assertTrue(ex.getMessage().contains("null"));
    }

    @Test
    void newInstance_withLong() {
        InvalidUuidException ex = InvalidUuidException.newInstance(12345L);
        assertTrue(ex.getMessage().contains("Invalid UUID"));
        assertTrue(ex.getMessage().contains("12345"));
    }

    @Test
    void newInstance_withInteger() {
        InvalidUuidException ex = InvalidUuidException.newInstance(42);
        assertTrue(ex.getMessage().contains("Invalid UUID"));
        assertTrue(ex.getMessage().contains("42"));
    }

    @Test
    void extendsRuntimeException() {
        InvalidUuidException ex = new InvalidUuidException("test");
        assertTrue(ex instanceof RuntimeException);
    }
}