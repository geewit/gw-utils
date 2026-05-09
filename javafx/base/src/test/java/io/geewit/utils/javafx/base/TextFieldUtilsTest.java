package io.geewit.utils.javafx.base;

import javafx.scene.control.TextField;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.verify;

@DisplayName("TextFieldUtils Tests")
class TextFieldUtilsTest {

    private TextField mockTextField = Mockito.mock(TextField.class);

    @Test
    @DisplayName("setEditable should set editable and disable state")
    void setEditable_shouldSetEditableAndDisableState() {
        TextFieldUtils.setEditable(mockTextField, true);

        verify(mockTextField).setEditable(true);
        verify(mockTextField).setDisable(false);
    }

    @Test
    @DisplayName("setEditable with false should make field non-editable and disabled")
    void setEditable_withFalse_shouldMakeFieldNonEditableAndDisabled() {
        TextFieldUtils.setEditable(mockTextField, false);

        verify(mockTextField).setEditable(false);
        verify(mockTextField).setDisable(true);
    }

    @Test
    @DisplayName("setEditable should do nothing when field is null")
    void setEditable_shouldDoNothingWhenFieldIsNull() {
        // Should not throw
        TextFieldUtils.setEditable(null, true);
    }
}