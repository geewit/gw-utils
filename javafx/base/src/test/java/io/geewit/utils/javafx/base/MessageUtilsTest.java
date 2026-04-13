package io.geewit.utils.javafx.base;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.ResourceBundle;

import static org.assertj.core.api.Assertions.assertThat;

public class MessageUtilsTest {

    @Test
    void getMessage_withNullResources_returnsFallback() {
        String result = MessageUtils.getMessage(null, "key", "Fallback message");
        assertThat(result).isEqualTo("Fallback message");
    }

    @Test
    void getMessage_withNullFallback_returnsEmptyString() {
        String result = MessageUtils.getMessage(null, "nonexistent.key", null);
        assertThat(result).isEmpty();
    }

    @Test
    void getMessage_withNullArgs_returnsPatternWithoutFormatting() {
        String result = MessageUtils.getMessage(null, "simple", "No args here", (Object[]) null);
        assertThat(result).isEqualTo("No args here");
    }

    @Test
    void getMessage_withEmptyArgs_returnsPatternWithoutFormatting() {
        String result = MessageUtils.getMessage(null, "key", "Pattern {0}", new Object[]{});
        assertThat(result).isEqualTo("Pattern {0}");
    }

    @Test
    void getMessage_withMultipleArgs_returnsFormattedMessage() {
        String result = MessageUtils.getMessage(null, "key", "{0} {1} {2}", "A", "B", "C");
        assertThat(result).isEqualTo("A B C");
    }

    @Test
    void getMessage_withNoArgs_returnsPattern() {
        String result = MessageUtils.getMessage(null, "key", "Just a pattern");
        assertThat(result).isEqualTo("Just a pattern");
    }

    @Test
    void getMessage_withSingleArg_returnsFormattedMessage() {
        String result = MessageUtils.getMessage(null, "key", "Hello {0}", "World");
        assertThat(result).isEqualTo("Hello World");
    }
}