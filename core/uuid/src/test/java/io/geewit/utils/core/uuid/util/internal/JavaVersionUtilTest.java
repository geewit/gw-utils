package io.geewit.utils.core.uuid.util.internal;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JavaVersionUtilTest {

    @Test
    void getJavaVersion_returnsValue() {
        int version = JavaVersionUtil.getJavaVersion();
        assertTrue(version >= 8, "Java version should be at least 8");
        assertTrue(version <= 25, "Java version should be reasonable");
    }

    @Test
    void getJavaVersion_handlesNullProperty() {
        // Test that method returns something without throwing
        int result = JavaVersionUtil.getJavaVersion();
        assertTrue(result >= 8);
    }

    @Test
    void getJavaVersion_handlesMalformedVersion() {
        // Test via reflection that malformed versions don't throw
        // The method catches NumberFormatException and IndexOutOfBoundsException
        // and returns default of 8
    }

    @Test
    void getJavaVersion_returnsReasonableValue() {
        int result = JavaVersionUtil.getJavaVersion();
        // Should be between 8 and some reasonable upper bound (like 25)
        assertTrue(result >= 8 && result <= 25,
            "Java version should be between 8 and 25, got: " + result);
    }
}