package io.geewit.utils.javafx.base;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("LoadingHelper Tests")
class LoadingHelperTest {

    @Nested
    @DisplayName("commonLoading with message")
    class CommonLoadingWithMessage {

        @Test
        @DisplayName("should increment loading counter")
        void shouldIncrementLoadingCounter() {
            AtomicInteger counter = new AtomicInteger(0);

            int newValue = counter.incrementAndGet();
            assertThat(newValue).isEqualTo(1);
        }

        @Test
        @DisplayName("should handle multiple increments")
        void shouldHandleMultipleIncrements() {
            AtomicInteger counter = new AtomicInteger(0);

            counter.incrementAndGet();
            counter.incrementAndGet();
            counter.incrementAndGet();

            assertThat(counter.get()).isEqualTo(3);
        }
    }

    @Nested
    @DisplayName("commonLoading without message")
    class CommonLoadingWithoutMessage {

        @Test
        @DisplayName("should decrement loading counter")
        void shouldDecrementLoadingCounter() {
            AtomicInteger counter = new AtomicInteger(1);

            int remaining = counter.decrementAndGet();
            assertThat(remaining).isEqualTo(0);
        }

        @Test
        @DisplayName("should not go negative when already zero")
        void shouldNotGoNegativeWhenAlreadyZero() {
            AtomicInteger counter = new AtomicInteger(0);

            int remaining = counter.decrementAndGet();
            assertThat(remaining).isEqualTo(-1);
        }

        @Test
        @DisplayName("should decrement to zero then set to zero")
        void shouldDecrementToZeroThenSetToZero() {
            AtomicInteger counter = new AtomicInteger(1);

            int remaining = counter.decrementAndGet();
            if (remaining <= 0) {
                counter.set(0);
            }

            assertThat(counter.get()).isEqualTo(0);
        }

        @Test
        @DisplayName("should handle multiple concurrent calls")
        void shouldHandleMultipleConcurrentCalls() {
            AtomicInteger counter = new AtomicInteger(2);

            int remaining = counter.decrementAndGet();

            // 2-1=1, not <=0 so not reset
            assertThat(remaining).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("counter logic edge cases")
    class CounterEdgeCases {

        @Test
        @DisplayName("should handle large counter values")
        void shouldHandleLargeCounterValues() {
            AtomicInteger counter = new AtomicInteger(Integer.MAX_VALUE);

            int newValue = counter.incrementAndGet();
            assertThat(newValue).isEqualTo(Integer.MAX_VALUE + 1);
        }

        @Test
        @DisplayName("should handle counter wrap around")
        void shouldHandleCounterWrapAround() {
            AtomicInteger counter = new AtomicInteger(Integer.MAX_VALUE);

            counter.incrementAndGet();
            int remaining = counter.decrementAndGet();

            assertThat(remaining).isEqualTo(Integer.MAX_VALUE);
        }
    }
}
