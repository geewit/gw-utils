package io.geewit.utils.javafx.base;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import org.kordamp.bootstrapfx.BootstrapFX;

public final class BootstrapFXHelper {

    private BootstrapFXHelper() {
    }

    public static void applyBootstrapFX(Node root) {
        if (root == null) {
            return;
        }
        root.sceneProperty().addListener((_, _, newScene) -> {
            if (newScene == null) {
                return;
            }
            ObservableList<String> sceneStylesheets = newScene.getStylesheets();
            moveStylesheet(sceneStylesheets, BootstrapFX.bootstrapFXStylesheet(), 0);
            if (root instanceof Parent parent) {
                ObservableList<String> parentStylesheets = parent.getStylesheets();
                if (parentStylesheets != null && !parentStylesheets.isEmpty()) {
                    parentStylesheets.forEach(stylesheet -> moveStylesheet(sceneStylesheets, stylesheet, sceneStylesheets.size()));
                }
            }
        });
    }

    private static void moveStylesheet(ObservableList<String> stylesheets, String stylesheet, int index) {
        if (stylesheet == null) {
            return;
        }
        stylesheets.remove(stylesheet);
        int targetIndex = Math.min(Math.max(index, 0), stylesheets.size());
        stylesheets.add(targetIndex, stylesheet);
    }
}
