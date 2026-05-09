package io.geewit.utils.javafx.base;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.CompletionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(ApplicationExtension.class)
public class FxFuturesTest {

    @Test
    void supplyOnFx_shouldReturnValue() throws Exception {
        var future = FxFutures.supplyOnFx(() -> 42);

        assertThat(future.toCompletableFuture().get()).isEqualTo(42);
    }

    @Test
    void supplyOnFx_shouldPropagateException() {
        var future = FxFutures.supplyOnFx(() -> {
            throw new RuntimeException("test error");
        });

        assertThatThrownBy(() -> future.toCompletableFuture().join())
                .isInstanceOf(CompletionException.class)
                .hasMessageContaining("test error");
    }

    @Test
    void supplyOnFx_shouldHandleNullSupplier() {
        var future = FxFutures.supplyOnFx(null);

        // Should complete without throwing
        assertThat(future.toCompletableFuture()).isNotNull();
    }
}
