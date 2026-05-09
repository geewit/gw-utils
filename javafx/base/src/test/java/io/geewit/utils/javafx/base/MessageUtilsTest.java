package io.geewit.utils.javafx.base;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("MessageUtils Tests")
class MessageUtilsTest {

    @Nested
    @DisplayName("getMessage()")
    class GetMessageMethod {

        @Test
        @DisplayName("should return fallback when resources is null")
        void shouldReturnFallbackWhenResourcesIsNull() {
            String result = MessageUtils.getMessage(null, "key", "fallback");
            assertThat(result).isEqualTo("fallback");
        }

        @Test
        @DisplayName("should return fallback when key not found in resources")
        void shouldReturnFallbackWhenKeyNotFound() {
            ResourceBundle mockBundle = mock(ResourceBundle.class);
            when(mockBundle.containsKey("missingKey")).thenReturn(false);

            String result = MessageUtils.getMessage(mockBundle, "missingKey", "defaultMessage");
            assertThat(result).isEqualTo("defaultMessage");
        }

        @Test
        @DisplayName("should return key value when key found in resources")
        void shouldReturnResourceValueWhenKeyFound() {
            ResourceBundle mockBundle = mock(ResourceBundle.class);
            when(mockBundle.containsKey("existingKey")).thenReturn(true);
            when(mockBundle.getString("existingKey")).thenReturn("Found message");

            String result = MessageUtils.getMessage(mockBundle, "existingKey", "fallback");
            assertThat(result).isEqualTo("Found message");
        }

        @Test
        @DisplayName("should return empty string when pattern is null")
        void shouldReturnEmptyStringWhenPatternIsNull() {
            ResourceBundle mockBundle = mock(ResourceBundle.class);
            when(mockBundle.containsKey("key")).thenReturn(true);
            when(mockBundle.getString("key")).thenReturn(null);

            String result = MessageUtils.getMessage(mockBundle, "key", "fallback");
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("should return pattern unchanged when args is null")
        void shouldReturnPatternUnchangedWhenArgsIsNull() {
            ResourceBundle mockBundle = mock(ResourceBundle.class);
            when(mockBundle.containsKey("key")).thenReturn(true);
            when(mockBundle.getString("key")).thenReturn("No args pattern");

            String result = MessageUtils.getMessage(mockBundle, "key", "fallback", null);
            assertThat(result).isEqualTo("No args pattern");
        }

        @Test
        @DisplayName("should return pattern unchanged when args is empty")
        void shouldReturnPatternUnchangedWhenArgsIsEmpty() {
            ResourceBundle mockBundle = mock(ResourceBundle.class);
            when(mockBundle.containsKey("key")).thenReturn(true);
            when(mockBundle.getString("key")).thenReturn("No args pattern");

            String result = MessageUtils.getMessage(mockBundle, "key", "fallback");
            assertThat(result).isEqualTo("No args pattern");
        }

        @Test
        @DisplayName("should format message with single argument")
        void shouldFormatMessageWithSingleArgument() {
            ResourceBundle mockBundle = mock(ResourceBundle.class);
            when(mockBundle.containsKey("greeting")).thenReturn(true);
            when(mockBundle.getString("greeting")).thenReturn("Hello, {0}!");

            String result = MessageUtils.getMessage(mockBundle, "greeting", "fallback", "World");
            assertThat(result).isEqualTo("Hello, World!");
        }

        @Test
        @DisplayName("should format message with multiple arguments")
        void shouldFormatMessageWithMultipleArguments() {
            ResourceBundle mockBundle = mock(ResourceBundle.class);
            when(mockBundle.containsKey("format")).thenReturn(true);
            when(mockBundle.getString("format")).thenReturn("User {0} has {1} points");

            String result = MessageUtils.getMessage(mockBundle, "format", "fallback", "Alice", 100);
            assertThat(result).isEqualTo("User Alice has 100 points");
        }

        @Test
        @DisplayName("should use fallback when key not found and format with args")
        void shouldUseFallbackAndFormatWhenKeyNotFound() {
            ResourceBundle mockBundle = mock(ResourceBundle.class);
            when(mockBundle.containsKey("missing")).thenReturn(false);

            String result = MessageUtils.getMessage(mockBundle, "missing", "Hello {0}!", "Test");
            assertThat(result).isEqualTo("Hello Test!");
        }

        @Test
        @DisplayName("should handle resource bundle returning empty string")
        void shouldHandleEmptyResourceString() {
            ResourceBundle mockBundle = mock(ResourceBundle.class);
            when(mockBundle.containsKey("emptyKey")).thenReturn(true);
            when(mockBundle.getString("emptyKey")).thenReturn("");

            String result = MessageUtils.getMessage(mockBundle, "emptyKey", "fallback");
            assertThat(result).isEmpty();
        }
    }
}