package io.geewit.utils.javafx.base;

import javafx.application.Platform;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Supplier;

public class FxFutures {
    private FxFutures() {}

    public static <T> CompletionStage<T> supplyOnFx(Supplier<T> supplier) {
        CompletableFuture<T> f = new CompletableFuture<>();
        if (Platform.isFxApplicationThread()) {
            try {
                f.complete(supplier.get());
            } catch (Throwable t) {
                f.completeExceptionally(t);
            }
            return f;
        }
        Platform.runLater(() -> {
            try {
                f.complete(supplier.get());
            } catch (Throwable t) {
                f.completeExceptionally(t);
            }
        });
        return f;
    }
}
