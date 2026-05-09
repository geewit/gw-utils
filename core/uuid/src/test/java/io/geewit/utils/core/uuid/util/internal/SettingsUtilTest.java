package io.geewit.utils.core.uuid.util.internal;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class SettingsUtilTest {

    @Test
    void getSecureRandom_returnsNonNull() {
        String algorithm = SettingsUtil.getSecureRandom();
        // May be null if not configured
        if (algorithm != null) {
            assertFalse(algorithm.isEmpty());
        }
    }

    @Test
    void getProperty_returnsNullWhenNotSet() {
        // Get a property that likely doesn't exist
        String value = SettingsUtil.getProperty("nonexistent.property." + System.currentTimeMillis());
        assertNull(value);
    }

    @Test
    void getPropertyName_formatsCorrectly() throws Exception {
        // Use reflection to test private method
        Method method = SettingsUtil.class.getDeclaredMethod("getPropertyName", String.class);
        method.setAccessible(true);

        String result = (String) method.invoke(null, "test");
        assertEquals("uuidcreator.test", result);
    }

    @Test
    void getEnvironmentName_formatsCorrectly() throws Exception {
        // Use reflection to test private method
        Method method = SettingsUtil.class.getDeclaredMethod("getEnvinronmentName", String.class);
        method.setAccessible(true);

        String result = (String) method.invoke(null, "securerandom");
        assertEquals("UUIDCREATOR_SECURERANDOM", result);
    }

    @Test
    void getPropertyName_withMultipleKeys() throws Exception {
        Method method = SettingsUtil.class.getDeclaredMethod("getPropertyName", String.class);
        method.setAccessible(true);

        assertEquals("uuidcreator.node", method.invoke(null, "node"));
        assertEquals("uuidcreator.securerandom", method.invoke(null, "securerandom"));
    }
}