package io.geewit.utils.javafx.base;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Window;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Utility class for showing confirmation dialogs and executing actions on virtual threads. */
public final class DialogHelper {
    private static final ExecutorService EXECUTOR = Executors.newVirtualThreadPerTaskExecutor();

    private DialogHelper() {
    }

    public static void confirm(Window owner, String message, Runnable action) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.OK, ButtonType.CANCEL);
        if (owner != null) {
            alert.initOwner(owner);
        }
        alert.setHeaderText(null);
        alert.showAndWait()
                .filter(btn -> btn == ButtonType.OK)
                .ifPresent(_ -> EXECUTOR.execute(action));
    }
}
