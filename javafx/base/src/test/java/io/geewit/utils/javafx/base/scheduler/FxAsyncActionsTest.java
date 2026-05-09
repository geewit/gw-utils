package io.geewit.utils.javafx.base.scheduler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("FxAsyncActions Tests")
@ExtendWith(ApplicationExtension.class)
class FxAsyncActionsTest {

    @Nested
    @DisplayName("record constructor")
    class RecordConstructor {

        @Test
        @DisplayName("should throw NullPointerException when fxScheduler is null")
        void shouldThrowWhenFxSchedulerIsNull() {
            assertThatThrownBy(() -> new FxAsyncActions(null, new VirtualThreadScheduler()))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("fxScheduler");
        }

        @Test
        @DisplayName("should throw NullPointerException when virtualScheduler is null")
        void shouldThrowWhenVirtualSchedulerIsNull() {
            assertThatThrownBy(() -> new FxAsyncActions(new FxScheduler(), null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("virtualScheduler");
        }

        @Test
        @DisplayName("should create successfully with valid arguments")
        void shouldCreateSuccessfullyWithValidArguments() {
            FxScheduler fxScheduler = new FxScheduler();
            VirtualThreadScheduler virtualScheduler = new VirtualThreadScheduler();
            FxAsyncActions actions = new FxAsyncActions(fxScheduler, virtualScheduler);
            assertThat(actions).isNotNull();
            assertThat(actions.fxScheduler()).isSameAs(fxScheduler);
            assertThat(actions.virtualScheduler()).isSameAs(virtualScheduler);

            // Cleanup
            fxScheduler.destroy();
            virtualScheduler.destroy();
        }
    }

    @Test
    @DisplayName("should handle runMono with null supplier")
    void shouldHandleRunMonoWithNullSupplier() {
        FxScheduler fxScheduler = new FxScheduler();
        VirtualThreadScheduler virtualScheduler = new VirtualThreadScheduler();
        FxAsyncActions actions = new FxAsyncActions(fxScheduler, virtualScheduler);

        // Should not throw - null supplier defaults to empty Flux
        var disposable = actions.runMono(null, null, null, null, null, null, null);

        assertThat(disposable).isNotNull();
        disposable.dispose();

        fxScheduler.destroy();
        virtualScheduler.destroy();
    }

    @Test
    @DisplayName("should handle runFlux with null supplier")
    void shouldHandleRunFluxWithNullSupplier() {
        FxScheduler fxScheduler = new FxScheduler();
        VirtualThreadScheduler virtualScheduler = new VirtualThreadScheduler();
        FxAsyncActions actions = new FxAsyncActions(fxScheduler, virtualScheduler);

        // Should not throw - null supplier defaults to empty Flux
        actions.runFlux(null, null, null, null, null, null, null);

        fxScheduler.destroy();
        virtualScheduler.destroy();
    }

    @Test
    @DisplayName("should handle runMono with supplier returningNull")
    void shouldHandleRunMonoWithSupplierReturningNull() {
        FxScheduler fxScheduler = new FxScheduler();
        VirtualThreadScheduler virtualScheduler = new VirtualThreadScheduler();
        FxAsyncActions actions = new FxAsyncActions(fxScheduler, virtualScheduler);

        // Should not throw - null from supplier defaults to empty Flux
        var disposable = actions.runMono(() -> null, null, null, null, null, null, null);

        assertThat(disposable).isNotNull();
        disposable.dispose();

        fxScheduler.destroy();
        virtualScheduler.destroy();
    }
}