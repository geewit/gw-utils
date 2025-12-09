package io.geewit.utils.javafx.base;

import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Ensures dialogs are uniquely identified so that each key can only be opened once at a time.
 */
public final class UniqueDialogManager {
    private static final Map<String, Stage> OPEN_DIALOGS = new ConcurrentHashMap<>();
    private static final Map<Stage, String> STAGE_KEYS = new ConcurrentHashMap<>();
    private static final String HANDLER_KEY = UniqueDialogManager.class.getName() + ".hiddenHandler";

    private UniqueDialogManager() {
    }

    public static String identifier(Class<?> dialogClass, Object identifier) {
        return identifier == null
                ? dialogClass.getName()
                : dialogClass.getName() + "#" + identifier;
    }

    public static <T> T show(Stage stage,
                             Class<?> dialogClass,
                             Object identifier,
                             Supplier<T> showAction,
                             Supplier<T> whenAlreadyOpen) {
        Objects.requireNonNull(stage, "stage");
        Objects.requireNonNull(dialogClass, "dialogClass");
        Objects.requireNonNull(showAction, "showAction");
        Objects.requireNonNull(whenAlreadyOpen, "whenAlreadyOpen");
        String key = identifier(dialogClass, identifier);
        Stage existing = OPEN_DIALOGS.putIfAbsent(key, stage);
        if (existing != null) {
            bringToFront(existing);
            return whenAlreadyOpen.get();
        }
        registerHiddenHandler(stage);
        STAGE_KEYS.put(stage, key);
        try {
            return showAction.get();
        } finally {
            if (!stage.isShowing()) {
                OPEN_DIALOGS.remove(key, stage);
                STAGE_KEYS.remove(stage, key);
            }
        }
    }

    public static boolean show(Stage stage,
                               Class<?> dialogClass,
                               Object identifier,
                               Runnable showAction) {
        return show(stage, dialogClass, identifier, () -> {
            showAction.run();
            return Boolean.TRUE;
        }, () -> Boolean.FALSE);
    }

    private static void bringToFront(Stage stage) {
        Platform.runLater(() -> {
            if (!stage.isShowing()) {
                stage.show();
            }
            if (stage.isIconified()) {
                stage.setIconified(false);
            }
            stage.toFront();
            stage.requestFocus();
        });
    }

    private static void registerHiddenHandler(Stage stage) {
        if (stage.getProperties().putIfAbsent(HANDLER_KEY, Boolean.TRUE) == null) {
            stage.addEventHandler(WindowEvent.WINDOW_HIDDEN, _ -> {
                String key = STAGE_KEYS.remove(stage);
                if (key != null) {
                    OPEN_DIALOGS.remove(key, stage);
                }
            });
        }
    }
}
