package io.geewit.utils.core.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExceptionUtilsTest {

    @Test
    void getSimpleStackTrace_withNestedException_returnsRootCause() {
        RuntimeException root = new RuntimeException("root cause");
        IllegalStateException middle = new IllegalStateException("middle", root);
        Exception top = new Exception("top", middle);

        String trace = ExceptionUtils.getSimpleStackTrace(top, 5);
        assertNotNull(trace);
        assertTrue(trace.contains("java.lang.RuntimeException"));
        assertTrue(trace.contains("root cause"));
    }

    @Test
    void getSimpleStackTrace_noCause_returnsSelf() {
        Exception ex = new Exception("single");
        String trace = ExceptionUtils.getSimpleStackTrace(ex, 5);
        assertTrue(trace.contains("java.lang.Exception"));
        assertTrue(trace.contains("single"));
    }

    @Test
    void getSimpleStackTrace_defaultMaxFrames() {
        Exception ex = new Exception("test");
        String trace = ExceptionUtils.getSimpleStackTrace(ex);
        assertNotNull(trace);
        assertTrue(trace.contains("test"));
    }

    @Test
    void getSimpleStackTrace_moreFramesThanAvailable() {
        Exception ex = new Exception("test");
        String trace = ExceptionUtils.getSimpleStackTrace(ex, 1000);
        assertNotNull(trace);
        // When requested frames >= available frames, no "more" suffix
        assertFalse(trace.contains(" more"));
    }

    @Test
    void getSimpleStackTrace_fewerFramesThanAvailable() {
        Exception ex = new Exception("test");
        String trace = ExceptionUtils.getSimpleStackTrace(ex, 1);
        assertNotNull(trace);
        assertTrue(trace.contains("more"));
    }

    @Test
    void getSimpleStackTrace_nullMessage() {
        Exception ex = new RuntimeException();
        String trace = ExceptionUtils.getSimpleStackTrace(ex, 5);
        assertTrue(trace.contains("java.lang.RuntimeException"));
    }
}
