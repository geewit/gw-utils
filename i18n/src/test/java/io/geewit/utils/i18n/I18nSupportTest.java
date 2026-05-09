package io.geewit.utils.i18n;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class I18nSupportTest {

    @Test
    void message_withExistingKey() {
        String result = I18nSupport.message("test.key", "arg1");
        assertNotNull(result);
    }

    @Test
    void message_withNullArgs() {
        String result = I18nSupport.message("test.key");
        assertNotNull(result);
    }

    @Test
    void message_withResourceBundle() {
        ResourceBundle bundle = ResourceBundle.getBundle("i18n/messages");
        String result = I18nSupport.message(bundle, "test.key", "arg1");
        assertNotNull(result);
    }

    @Test
    void message_withMissingKey() {
        String key = "nonexistent.key.12345";
        String result = I18nSupport.message(key);
        assertEquals(key, result);
    }

    @Test
    void message_withArgs() {
        String result = I18nSupport.message("test.pattern", "Hello", 42);
        assertNotNull(result);
    }

    @Test
    void message_withNullResourceBundle() {
        String result = I18nSupport.message((ResourceBundle) null, "test.key");
        assertNotNull(result);
    }

    @Test
    void defaultMessage_withExistingKey() {
        ResourceBundle bundle = ResourceBundle.getBundle("i18n/messages");
        String result = I18nSupport.defaultMessage(bundle, "test.key", "default");
        assertNotNull(result);
    }

    @Test
    void defaultMessage_withMissingKey() {
        ResourceBundle bundle = ResourceBundle.getBundle("i18n/messages");
        String result = I18nSupport.defaultMessage(bundle, "nonexistent.key.12345", "fallback");
        assertEquals("fallback", result);
    }

    @Test
    void defaultMessage_withNullBundle() {
        String result = I18nSupport.defaultMessage(null, "any.key", "fallback");
        assertEquals("fallback", result);
    }

    @Test
    void message_withEmptyArgs() {
        String result = I18nSupport.message("test.key", new Object[]{});
        assertNotNull(result);
    }

    @Test
    void message_whenLocaleContextHolderThrows() {
        try (MockedStatic<LocaleContextHolder> mocked = mockStatic(LocaleContextHolder.class)) {
            mocked.when(LocaleContextHolder::getLocale).thenThrow(new RuntimeException("test exception"));
            
            // Should fallback to Locale.getDefault() and still work
            String result = I18nSupport.message("test.key");
            assertNotNull(result);
        }
    }

    @Test
    void message_withNullLocaleFromContextHolder() {
        try (MockedStatic<LocaleContextHolder> mocked = mockStatic(LocaleContextHolder.class)) {
            mocked.when(LocaleContextHolder::getLocale).thenReturn(null);
            
            // Should fallback to Locale.getDefault()
            String result = I18nSupport.message("test.key");
            assertNotNull(result);
        }
    }
}