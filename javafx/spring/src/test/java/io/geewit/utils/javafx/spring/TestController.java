package io.geewit.utils.javafx.spring;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class TestController {

    @FXML
    private VBox root;

    @FXML
    private Button testButton;

    public String getButtonText() {
        return testButton != null ? testButton.getText() : null;
    }
}
