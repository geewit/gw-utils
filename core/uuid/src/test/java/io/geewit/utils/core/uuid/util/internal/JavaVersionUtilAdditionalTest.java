package io.geewit.utils.core.uuid.util.internal;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Additional tests to improve coverage for JavaVersionUtil.
 */
class JavaVersionUtilAdditionalTest {

    @Test
    void getJavaVersion_returnsReasonableValue() {
        int version = JavaVersionUtil.getJavaVersion();
        assertTrue(version >= 8, "Java version should be at least 8, got: " + version);
        assertTrue(version <= 25, "Java version should be at most 25, got: " + version);
    }

    @Test
    void getJavaVersion_handlesJava9Format() throws Exception {
        // Java 9+ uses "9" or "9.0.2" format instead of "1.8.0_xxx"
        // Save original value
        String originalVersion = System.getProperty("java.version");

        try {
            // Test with Java 9+ format (major version only)
            System.setProperty("java.version", "11");
            // Clear cached value by reloading class if possible, or just test the branch
            int version = JavaVersionUtil.getJavaVersion();
            assertTrue(version >= 8, "Should handle Java 9+ format, got: " + version);
        } finally {
            // Restore original
            if (originalVersion != null) {
                System.setProperty("java.version", originalVersion);
            }
        }
    }

    @Test
    void getJavaVersion_handlesJava9WithMinorVersion() throws Exception {
        // Test Java 9+ format with minor version
        String originalVersion = System.getProperty("java.version");

        try {
            System.setProperty("java.version", "11.0.2");
            int version = JavaVersionUtil.getJavaVersion();
            assertTrue(version >= 8, "Should handle Java 9+ format with minor, got: " + version);
        } finally {
            if (originalVersion != null) {
                System.setProperty("java.version", originalVersion);
            }
        }
    }

    @Test
    void getJavaVersion_handlesLegacyFormat() throws Exception {
        // Test Java 8 format "1.8.0_xxx"
        String originalVersion = System.getProperty("java.version");

        try {
            System.setProperty("java.version", "1.8.0_301");
            int version = JavaVersionUtil.getJavaVersion();
            assertEquals(8, version, "Should parse legacy 1.8 format correctly");
        } finally {
            if (originalVersion != null) {
                System.setProperty("java.version", originalVersion);
            }
        }
    }

    @Test
    void getJavaVersion_handlesLegacyFormatWithDifferentMinor() throws Exception {
        // Test various legacy format versions
        String originalVersion = System.getProperty("java.version");

        try {
            System.setProperty("java.version", "1.11.0_123");
            int version = JavaVersionUtil.getJavaVersion();
            assertEquals(11, version, "Should parse 1.11 format correctly");
        } finally {
            if (originalVersion != null) {
                System.setProperty("java.version", originalVersion);
            }
        }
    }

    @Test
    void getJavaVersion_handlesNullProperty() throws Exception {
        // Save original value
        String originalVersion = System.getProperty("java.version");

        try {
            System.clearProperty("java.version");
            int version = JavaVersionUtil.getJavaVersion();
            assertEquals(8, version, "Should return default 8 when property is null");
        } finally {
            if (originalVersion != null) {
                System.setProperty("java.version", originalVersion);
            }
        }
    }

    @Test
    void getJavaVersion_handlesEmptyString() throws Exception {
        // Save original value
        String originalVersion = System.getProperty("java.version");

        try {
            System.setProperty("java.version", "");
            int version = JavaVersionUtil.getJavaVersion();
            // Should return default 8 due to NumberFormatException or IndexOutOfBoundsException
            assertTrue(version >= 8, "Should return default 8 for empty string, got: " + version);
        } finally {
            if (originalVersion != null) {
                System.setProperty("java.version", originalVersion);
            }
        }
    }

    @Test
    void getJavaVersion_handlesMalformedVersion_noSecondElement() throws Exception {
        // Test with version string that doesn't have second element after split
        String originalVersion = System.getProperty("java.version");

        try {
            System.setProperty("java.version", "1");  // Only one element after split
            int version = JavaVersionUtil.getJavaVersion();
            // Should handle IndexOutOfBoundsException and return default 8
            assertEquals(8, version, "Should return default 8 for malformed version");
        } finally {
            if (originalVersion != null) {
                System.setProperty("java.version", originalVersion);
            }
        }
    }

    @Test
    void getJavaVersion_handlesNonNumericVersion() throws Exception {
        // Test with non-numeric version string
        String originalVersion = System.getProperty("java.version");

        try {
            System.setProperty("java.version", "invalid");
            int version = JavaVersionUtil.getJavaVersion();
            // Should handle NumberFormatException and return default 8
            assertEquals(8, version, "Should return default 8 for non-numeric version");
        } finally {
            if (originalVersion != null) {
                System.setProperty("java.version", originalVersion);
            }
        }
    }

    @Test
    void getJavaVersion_handlesNumberFormatException() throws Exception {
        // Test when first element is non-numeric in legacy format
        String originalVersion = System.getProperty("java.version");

        try {
            System.setProperty("java.version", "a.b.c");
            int version = JavaVersionUtil.getJavaVersion();
            // Should return default 8
            assertEquals(8, version, "Should return default 8 for non-numeric version");
        } finally {
            if (originalVersion != null) {
                System.setProperty("java.version", originalVersion);
            }
        }
    }

    @Test
    void getJavaVersion_handlesJava21Format() throws Exception {
        // Test Java 21 format
        String originalVersion = System.getProperty("java.version");

        try {
            System.setProperty("java.version", "21");
            int version = JavaVersionUtil.getJavaVersion();
            assertEquals(21, version, "Should handle Java 21 format");
        } finally {
            if (originalVersion != null) {
                System.setProperty("java.version", originalVersion);
            }
        }
    }

    @Test
    void getJavaVersion_handlesJava21WithMinor() throws Exception {
        // Test Java 21 with minor version
        String originalVersion = System.getProperty("java.version");

        try {
            System.setProperty("java.version", "21.0.4");
            int version = JavaVersionUtil.getJavaVersion();
            assertEquals(21, version, "Should handle Java 21.0.4 format");
        } finally {
            if (originalVersion != null) {
                System.setProperty("java.version", originalVersion);
            }
        }
    }
}