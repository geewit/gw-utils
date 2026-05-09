package io.geewit.utils.javafx.base.scheduler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("FxScheduler Tests")
@ExtendWith(ApplicationExtension.class)
class FxSchedulerTest {

    @Nested
    @DisplayName("constructor")
    class Constructor {

        @Test
        @DisplayName("should create scheduler successfully")
        void shouldCreateSchedulerSuccessfully() {
            FxScheduler scheduler = new FxScheduler();
            assertThat(scheduler.scheduler()).isNotNull();
        }
    }

    @Nested
    @DisplayName("scheduler()")
    class SchedulerMethod {

        @Test
        @DisplayName("should return the same scheduler on multiple calls")
        void shouldReturnSameSchedulerOnMultipleCalls() {
            FxScheduler scheduler = new FxScheduler();

            var scheduler1 = scheduler.scheduler();
            var scheduler2 = scheduler.scheduler();

            assertThat(scheduler1).isSameAs(scheduler2);
        }
    }

    @Nested
    @DisplayName("runLater()")
    class RunLaterMethod {

        @Test
        @DisplayName("should do nothing when runnable is null")
        void shouldDoNothingWhenRunnableIsNull() {
            FxScheduler scheduler = new FxScheduler();

            // Should not throw
            scheduler.runLater(null);
        }
    }

    @Nested
    @DisplayName("destroy()")
    class DestroyMethod {

        @Test
        @DisplayName("should not throw when called")
        void shouldNotThrowWhenCalled() {
            FxScheduler scheduler = new FxScheduler();

            // Should not throw
            scheduler.destroy();
        }

        @Test
        @DisplayName("should be idempotent")
        void shouldBeIdempotent() {
            FxScheduler scheduler = new FxScheduler();

            // Should not throw on multiple calls
            scheduler.destroy();
            scheduler.destroy();
        }
    }
}