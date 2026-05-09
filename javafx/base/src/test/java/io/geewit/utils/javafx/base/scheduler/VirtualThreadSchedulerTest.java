package io.geewit.utils.javafx.base.scheduler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("VirtualThreadScheduler Tests")
@ExtendWith(ApplicationExtension.class)
class VirtualThreadSchedulerTest {

    @Nested
    @DisplayName("constructor")
    class Constructor {

        @Test
        @DisplayName("should create scheduler successfully")
        void shouldCreateSchedulerSuccessfully() {
            VirtualThreadScheduler scheduler = new VirtualThreadScheduler();
            assertThat(scheduler.scheduler()).isNotNull();
        }
    }

    @Nested
    @DisplayName("scheduler()")
    class SchedulerMethod {

        @Test
        @DisplayName("should return the same scheduler on multiple calls")
        void shouldReturnSameSchedulerOnMultipleCalls() {
            VirtualThreadScheduler scheduler = new VirtualThreadScheduler();

            var scheduler1 = scheduler.scheduler();
            var scheduler2 = scheduler.scheduler();

            assertThat(scheduler1).isSameAs(scheduler2);
        }
    }

    @Nested
    @DisplayName("destroy()")
    class DestroyMethod {

        @Test
        @DisplayName("should not throw when called")
        void shouldNotThrowWhenCalled() {
            VirtualThreadScheduler scheduler = new VirtualThreadScheduler();

            // Should not throw
            scheduler.destroy();
        }

        @Test
        @DisplayName("should be idempotent")
        void shouldBeIdempotent() {
            VirtualThreadScheduler scheduler = new VirtualThreadScheduler();

            // Should not throw on multiple calls
            scheduler.destroy();
            scheduler.destroy();
        }
    }
}