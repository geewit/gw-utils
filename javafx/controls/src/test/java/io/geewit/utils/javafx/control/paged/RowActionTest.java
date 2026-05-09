package io.geewit.utils.javafx.control.paged;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class RowActionTest {

    @Test
    void constructor_withNullTextKey_throwsNullPointerException() {
        assertThatThrownBy(() -> new RowAction<>(null, "icon", null, row -> {}))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("textKey");
    }

    @Test
    void constructor_withNullHandler_throwsNullPointerException() {
        assertThatThrownBy(() -> new RowAction<>("key", "icon", null, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("handler");
    }

    @Test
    void record_accessors() {
        RowAction<String> action = new RowAction<>("key", "icon", null, row -> {});
        assertThat(action.textKey()).isEqualTo("key");
        assertThat(action.iconLiteral()).isEqualTo("icon");
        assertThat(action.enabledWhen()).isNull();
        assertThat(action.handler()).isNotNull();
    }

    @Test
    void isDisabled_withNullEnabledWhen_returnsFalse() {
        RowAction<String> action = new RowAction<>("key", "icon", null, row -> {});
        assertThat(action.isDisabled("data")).isFalse();
    }

    @Test
    void isDisabled_withEnabledPredicateReturnsTrue_returnsFalse() {
        RowAction<String> action = new RowAction<>("key", "icon", row -> true, row -> {});
        assertThat(action.isDisabled("data")).isFalse();
    }

    @Test
    void isDisabled_withEnabledPredicateReturnsFalse_returnsTrue() {
        RowAction<String> action = new RowAction<>("key", "icon", row -> false, row -> {});
        assertThat(action.isDisabled("data")).isTrue();
    }

    @Test
    void isDisabled_withNullRow() {
        RowAction<String> action = new RowAction<>("key", "icon", row -> false, row -> {});
        assertThat(action.isDisabled(null)).isTrue();
    }
}