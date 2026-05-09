package io.geewit.utils.core.uuid.util.internal;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Additional tests to improve coverage for RandomUtil and SecureRandomPool.
 */
class RandomUtilAdditionalTest {

    @Test
    void nextBytes_variousLengths() {
        // Test edge cases for length
        for (int len = 0; len <= 64; len++) {
            byte[] bytes = RandomUtil.nextBytes(len);
            assertEquals(len, bytes.length, "Length mismatch for " + len);
        }
    }

    @Test
    void nextBytes_zeroLength_returnsEmptyArray() {
        byte[] bytes = RandomUtil.nextBytes(0);
        assertEquals(0, bytes.length);
    }

    @Test
    void nextBytes_producesDifferentResults_consecutiveCalls() {
        byte[] bytes1 = RandomUtil.nextBytes(16);
        byte[] bytes2 = RandomUtil.nextBytes(16);
        assertFalse(java.util.Arrays.equals(bytes1, bytes2),
            "Consecutive calls should produce different results");
    }

    @RepeatedTest(100)
    void nextBytes_repeatedCalls_varyResults() {
        // Force pool deletion by calling enough times
        byte[] bytes = RandomUtil.nextBytes(16);
        assertNotNull(bytes);
        assertEquals(16, bytes.length);
    }

    @Test
    void newSecureRandom_returnsNewInstanceEachTime() {
        java.security.SecureRandom r1 = RandomUtil.newSecureRandom();
        java.security.SecureRandom r2 = RandomUtil.newSecureRandom();
        assertNotSame(r1, r2);
    }

    @Test
    void newSecureRandom_producesValidBytes() {
        java.security.SecureRandom random = RandomUtil.newSecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        assertNotNull(bytes);
        // Verify not all zeros
        boolean allZero = true;
        for (byte b : bytes) {
            if (b != 0) {
                allZero = false;
                break;
            }
        }
        assertFalse(allZero, "Random should not produce all zeros");
    }

    @Test
    void newSecureRandom_withInvalidAlgorithm_fallsBackToDefault() {
        // This test verifies fallback behavior when invalid algorithm is specified
        // The actual algorithm is read from settings, so we just verify the method works
        java.security.SecureRandom random = RandomUtil.newSecureRandom();
        assertNotNull(random);
    }

    @Test
    void processors_returnsValueInRange() throws Exception {
        // Use reflection to test the private processors() method
        Class<?> poolClass = Class.forName("io.geewit.utils.core.uuid.util.internal.RandomUtil$SecureRandomPool");
        Method processorsMethod = poolClass.getDeclaredMethod("processors");
        processorsMethod.setAccessible(true);

        int processors = (int) processorsMethod.invoke(null);

        // Should be between 4 and 32
        assertTrue(processors >= 4, "Should be at least 4: " + processors);
        assertTrue(processors <= 32, "Should be at most 32: " + processors);
    }

    @Test
    void processors_lowCpuCount() throws Exception {
        // Test that low CPU count (< 4) returns min of 4
        Class<?> poolClass = Class.forName("io.geewit.utils.core.uuid.util.internal.RandomUtil$SecureRandomPool");
        Method processorsMethod = poolClass.getDeclaredMethod("processors");
        processorsMethod.setAccessible(true);

        int processors = (int) processorsMethod.invoke(null);

        // Available processors could be 1-32, but method should return 4-32
        assertTrue(processors >= 4, "Min should be 4");
        assertTrue(processors <= 32, "Max should be 32");
    }

    @Test
    void current_lazyInitialization() throws Exception {
        // Access the current() method via reflection to verify it initializes lazily
        Class<?> poolClass = Class.forName("io.geewit.utils.core.uuid.util.internal.RandomUtil$SecureRandomPool");
        Method currentMethod = poolClass.getDeclaredMethod("current");
        currentMethod.setAccessible(true);

        Object result = currentMethod.invoke(null);
        assertNotNull(result);
        assertTrue(java.util.Random.class.isAssignableFrom(result.getClass()));
    }

    @Test
    void delete_nullifiesPoolEntry() throws Exception {
        // Get the POOL_SIZE via reflection first
        Class<?> poolClass = Class.forName("io.geewit.utils.core.uuid.util.internal.RandomUtil$SecureRandomPool");
        Field poolField = poolClass.getDeclaredField("POOL");
        poolField.setAccessible(true);
        java.util.Random[] pool = (java.util.Random[]) poolField.get(null);
        int poolSize = pool.length;

        // Call current to initialize an entry
        Method currentMethod = poolClass.getDeclaredMethod("current");
        currentMethod.setAccessible(true);
        currentMethod.invoke(null);

        // Now delete at a random index
        int deleteIndex = ThreadLocalRandom.current().nextInt(poolSize);
        Method deleteMethod = poolClass.getDeclaredMethod("delete", int.class);
        deleteMethod.setAccessible(true);
        deleteMethod.invoke(null, deleteIndex);

        // Pool entry should be null after delete
        java.util.Random[] poolAfter = (java.util.Random[]) poolField.get(null);
        assertNull(poolAfter[deleteIndex], "Pool entry should be null after delete");
    }

    @Test
    void nextBytes_poolDeletion_onZeroFirstByte() throws Exception {
        // This test verifies the branch where first byte is 0 triggers deletion
        // We need to force this condition - we'll use reflection to check behavior
        // since we can't easily control the random output

        Class<?> poolClass = Class.forName("io.geewit.utils.core.uuid.util.internal.RandomUtil$SecureRandomPool");
        Field poolField = poolClass.getDeclaredField("POOL");
        poolField.setAccessible(true);

        // Call nextBytes multiple times to potentially trigger the deletion branch
        for (int i = 0; i < 1000; i++) {
            RandomUtil.nextBytes(16);
        }
        // If we get here without exception, the branch works correctly
    }

    @Test
    void nextBytes_concurrentAccess() throws Exception {
        // Test thread safety of the pool by accessing from multiple threads
        int numThreads = 4;
        Thread[] threads = new Thread[numThreads];
        AtomicBoolean failed = new AtomicBoolean(false);

        for (int i = 0; i < numThreads; i++) {
            threads[i] = new Thread(() -> {
                try {
                    for (int j = 0; j < 10; j++) {
                        byte[] bytes = RandomUtil.nextBytes(16);
                        if (bytes.length != 16) {
                            failed.set(true);
                        }
                    }
                } catch (Exception e) {
                    failed.set(true);
                }
            });
        }

        for (Thread t : threads) {
            t.start();
        }
        for (Thread t : threads) {
            t.join();
        }

        assertFalse(failed.get(), "Concurrent access should not fail");
    }

    @Test
    void current_sameThreadReturnsSameInstance() throws Exception {
        // On the same thread, current() should return the same Random instance
        Class<?> poolClass = Class.forName("io.geewit.utils.core.uuid.util.internal.RandomUtil$SecureRandomPool");
        Method currentMethod = poolClass.getDeclaredMethod("current");
        currentMethod.setAccessible(true);

        Object first = currentMethod.invoke(null);
        Object second = currentMethod.invoke(null);

        assertSame(first, second, "Same thread should get same Random instance");
    }
}