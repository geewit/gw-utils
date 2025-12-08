package io.geewit.utils.javafx.base.scheduler;

import javafx.application.Platform;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.Objects;

/**
 * Reactor scheduler that ensures work is executed on the JavaFX application thread.
 */
public class FxScheduler {

    private final Scheduler scheduler;

    public FxScheduler() {
        this.scheduler = Schedulers.fromExecutor(command -> {
            Objects.requireNonNull(command, "command");
            if (Platform.isFxApplicationThread()) {
                command.run();
            } else {
                Platform.runLater(command);
            }
        });
    }

    public Scheduler scheduler() {
        return scheduler;
    }

    public void runLater(Runnable runnable) {
        if (runnable == null) {
            return;
        }
        if (Platform.isFxApplicationThread()) {
            runnable.run();
        } else {
            Platform.runLater(runnable);
        }
    }

    public void destroy() {
        scheduler.dispose();
    }
}
