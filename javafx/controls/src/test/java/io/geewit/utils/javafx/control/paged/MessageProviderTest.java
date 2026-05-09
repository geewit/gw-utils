package io.geewit.utils.javafx.control.paged;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("MessageProvider Tests")
class MessageProviderTest {

    @Nested
    @DisplayName("identity()")
    class IdentityMethod {

        @Test
        @DisplayName("should return key as-is when no args")
        void shouldReturnKeyAsIsWhenNoArgs() {
            MessageProvider provider = MessageProvider.identity();

            String result = provider.message("testKey");

            assertThat(result).isEqualTo("testKey");
        }

        @Test
        @DisplayName("should return key as-is with args")
        void shouldReturnKeyAsIsWithArgs() {
            MessageProvider provider = MessageProvider.identity();

            String result = provider.message("testKey", "arg1", "arg2");

            assertThat(result).isEqualTo("testKey");
        }

        @Test
        @DisplayName("should return empty string when key is empty")
        void shouldReturnEmptyStringWhenKeyIsEmpty() {
            MessageProvider provider = MessageProvider.identity();

            String result = provider.message("");

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("custom implementation")
    class CustomImplementation {

        @Test
        @DisplayName("should work with custom message provider")
        void shouldWorkWithCustomMessageProvider() {
            MessageProvider customProvider = (key, args) -> "Custom: " + key + " with " + args.length + " args";

            String result = customProvider.message("key", "a", "b", "c");

            assertThat(result).isEqualTo("Custom: key with 3 args");
        }

        @Test
        @DisplayName("should work with custom message provider returning null")
        void shouldWorkWithCustomMessageProviderReturningNull() {
            MessageProvider customProvider = (key, args) -> null;

            String result = customProvider.message("key");

            assertThat(result).isNull();
        }
    }
}