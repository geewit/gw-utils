package io.geewit.utils.javafx.base;

import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.util.function.Consumer;

public final class FxClicks {
    private FxClicks() {}

    public static void onDoubleClick(Node node, Runnable action) {
        if (action == null) {
            return;
        }
        onDoubleClick(node, event -> {
            action.run();
            event.consume();
        });
    }

    public static void onDoubleClick(Node node, Consumer<MouseEvent> action) {
        if (node == null || action == null) {
            return;
        }
        node.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2 && event.isStillSincePress()) {
                action.accept(event);
            }
        });
    }
}
