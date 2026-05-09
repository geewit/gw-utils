package io.geewit.utils.core.uuid.factory.function.impl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DefaultRandomFunctionTest {

    @Test
    void apply_returnsNonNull() {
        DefaultRandomFunction function = new DefaultRandomFunction();
        byte[] result = function.apply(16);
        assertNotNull(result);
    }

    @Test
    void apply_returnsCorrectLength() {
        DefaultRandomFunction function = new DefaultRandomFunction();

        assertEquals(0, function.apply(0).length);
        assertEquals(1, function.apply(1).length);
        assertEquals(16, function.apply(16).length);
        assertEquals(32, function.apply(32).length);
    }

    @Test
    void apply_producesDifferentResults() {
        DefaultRandomFunction function = new DefaultRandomFunction();
        byte[] first = function.apply(16);
        byte[] second = function.apply(16);

        assertFalse(java.util.Arrays.equals(first, second),
                "Subsequent calls should produce different results");
    }

    @Test
    void apply_producesNonZeroContent() {
        DefaultRandomFunction function = new DefaultRandomFunction();

        boolean foundNonZero = false;
        for (int i = 0; i < 10; i++) {
            byte[] bytes = function.apply(16);
            for (byte b : bytes) {
                if (b != 0) {
                    foundNonZero = true;
                    break;
                }
            }
            if (foundNonZero) break;
        }
        assertTrue(foundNonZero, "Should produce non-zero bytes at least occasionally");
    }

    @Test
    void apply_isFunctionalInterfaceCompliant() {
        // Verify it implements IntFunction<byte[]>
        DefaultRandomFunction function = new DefaultRandomFunction();

        // Can be used as IntFunction<byte[]>
        java.util.function.IntFunction<byte[]> intFunction = function;
        assertNotNull(intFunction.apply(8));
    }
}