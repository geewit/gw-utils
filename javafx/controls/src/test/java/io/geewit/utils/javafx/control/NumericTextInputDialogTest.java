package io.geewit.utils.javafx.control;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for NumericTextInputDialog.
 * Note: Dialog subclass must be created on FX thread, so we only test
 * static/reifiable behavior and avoid creating dialog instances directly.
 */
@DisplayName("NumericTextInputDialog Tests")
class NumericTextInputDialogTest {

    @Nested
    @DisplayName("static behavior")
    class StaticBehavior {

        @Test
        @DisplayName("NumericTextInputDialog should be a Dialog subclass")
        void shouldBeDialogSubclass() {
            // Verify the class extends Dialog
            assertThat(javafx.scene.control.Dialog.class).isAssignableFrom(NumericTextInputDialog.class);
        }
    }

    @Nested
    @DisplayName("getEditor()")
    class GetEditor {

        @Test
        @DisplayName("getEditor should be declared")
        void getEditorShouldBeDeclared() throws NoSuchMethodException {
            NumericTextInputDialog.class.getMethod("getEditor");
        }
    }
}