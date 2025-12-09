package io.geewit.utils.javafx.base;

import javafx.scene.control.TextField;

public class TextFieldUtils {
    public static void setEditable(TextField field, boolean editable) {
        if (field == null) {
            return;
        }
        field.setEditable(editable);
        field.setDisable(!editable);
    }
}
