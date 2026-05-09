package io.geewit.utils.javafx.spring.weaver;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FxLoadExceptionTest {

    @Test
    void constructor_noArgs() {
        FxLoadException exception = new FxLoadException();
        assertNotNull(exception);
    }

    @Test
    void constructor_withMessage() {
        FxLoadException exception = new FxLoadException("test message");
        assertEquals("test message", exception.getMessage());
    }

    @Test
    void constructor_withMessageAndCause() {
        Throwable cause = new RuntimeException("cause");
        FxLoadException exception = new FxLoadException("test message", cause);
        assertEquals("test message", exception.getMessage());
        assertSame(cause, exception.getCause());
    }

    @Test
    void constructor_withCause() {
        Throwable cause = new RuntimeException("cause");
        FxLoadException exception = new FxLoadException(cause);
        assertSame(cause, exception.getCause());
    }

    @Test
    void constructor_withMessageCauseSuppressionStackTrace() {
        Throwable cause = new RuntimeException("cause");
        FxLoadException exception = new FxLoadException("test message", cause, false, false);
        assertEquals("test message", exception.getMessage());
        assertSame(cause, exception.getCause());
    }
}
