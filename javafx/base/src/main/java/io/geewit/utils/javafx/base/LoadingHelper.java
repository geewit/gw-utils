package io.geewit.utils.javafx.base;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;

import java.util.concurrent.atomic.AtomicInteger;

public class LoadingHelper {

    public static void commonLoading(String message,
                                     AtomicInteger loadingCounter,
                                     Label loadingLabel,
                                     ProgressIndicator loadingIndicator,
                                     StackPane loadingOverlay) {
        loadingCounter.incrementAndGet();
        Platform.runLater(() -> {
            if (loadingLabel != null && message != null && !message.isBlank()) {
                loadingLabel.setText(message);
            }
            if (loadingIndicator != null) {
                loadingIndicator.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
                loadingIndicator.setVisible(true);
                loadingIndicator.setManaged(true);
            }
            if (loadingOverlay != null) {
                loadingOverlay.setVisible(true);
                loadingOverlay.setManaged(true);
                loadingOverlay.toFront();
            }
        });
    }

    public static void commonLoading(AtomicInteger loadingCounter,
                                     StackPane loadingOverlay,
                                     ProgressIndicator loadingIndicator) {
        int remaining = loadingCounter.decrementAndGet();
        if (remaining <= 0) {
            loadingCounter.set(0);
            Platform.runLater(() -> {
                if (loadingOverlay != null) {
                    loadingOverlay.setVisible(false);
                    loadingOverlay.setManaged(false);
                }
                if (loadingIndicator != null) {
                    loadingIndicator.setVisible(false);
                    loadingIndicator.setManaged(false);
                }
            });
        }
    }
}
