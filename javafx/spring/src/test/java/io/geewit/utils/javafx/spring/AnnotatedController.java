package io.geewit.utils.javafx.spring;

import io.geewit.utils.javafx.spring.weaver.FxmlView;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

@FxmlView("custom/AnnotatedController.fxml")
public class AnnotatedController {

    @FXML
    private HBox root;

    @FXML
    private Label testLabel;
}
