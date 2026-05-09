package io.geewit.utils.core.uuid.util.internal;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Additional tests to improve coverage for SettingsUtil.
 */
class SettingsUtilAdditionalTest {

    @Test
    void getSecureRandom_returnsValueOrNull() {
        // Just verify it doesn't throw and returns something reasonable
        String algorithm = SettingsUtil.getSecureRandom();
        // If not null, should be a non-empty string
        if (algorithm != null) {
            assertFalse(algorithm.isEmpty());
        }
    }

    @Test
    void getProperty_emptySystemProperty_returnsEmptyString() throws Exception {
        // Save original
        String originalProperty = System.getProperty("uuidcreator.emptytest");

        try {
            // Set empty string system property
            System.setProperty("uuidcreator.emptytest", "");

            // Empty string is returned (the code checks isEmpty() and returns value if empty)
            String value = SettingsUtil.getProperty("emptytest");
            assertEquals("", value);
        } finally {
            if (originalProperty != null) {
                System.setProperty("uuidcreator.emptytest", originalProperty);
            } else {
                System.clearProperty("uuidcreator.emptytest");
            }
        }
    }

    @Test
    void getPropertyName_formatsCorrectly() throws Exception {
        Method method = SettingsUtil.class.getDeclaredMethod("getPropertyName", String.class);
        method.setAccessible(true);

        assertEquals("uuidcreator.node", method.invoke(null, "node"));
        assertEquals("uuidcreator.securerandom", method.invoke(null, "securerandom"));
        assertEquals("uuidcreator.test", method.invoke(null, "test"));
    }

    @Test
    void getEnvinronmentName_formatsCorrectly() throws Exception {
        Method method = SettingsUtil.class.getDeclaredMethod("getEnvinronmentName", String.class);
        method.setAccessible(true);

        assertEquals("UUIDCREATOR_SECURERANDOM", method.invoke(null, "securerandom"));
        assertEquals("UUIDCREATOR_NODE", method.invoke(null, "node"));
        assertEquals("UUIDCREATOR_TEST", method.invoke(null, "test"));
    }

    @Test
    void getPropertyName_specialCharacters() throws Exception {
        Method method = SettingsUtil.class.getDeclaredMethod("getPropertyName", String.class);
        method.setAccessible(true);

        String result = (String) method.invoke(null, "test.property");
        assertEquals("uuidcreator.test.property", result);
    }

    @Test
    void getEnvinronmentName_specialCharactersConverted() throws Exception {
        Method method = SettingsUtil.class.getDeclaredMethod("getEnvinronmentName", String.class);
        method.setAccessible(true);

        String result = (String) method.invoke(null, "test.property");
        assertEquals("UUIDCREATOR_TEST_PROPERTY", result);
    }
}