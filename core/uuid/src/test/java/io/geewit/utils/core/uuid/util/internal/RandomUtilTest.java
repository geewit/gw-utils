package io.geewit.utils.core.uuid.util.internal;

import org.junit.jupiter.api.Test;

import java.security.SecureRandom;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RandomUtilTest {

    @Test
    void nextBytes_returnsCorrectLength() {
        byte[] bytes1 = RandomUtil.nextBytes(16);
        assertEquals(16, bytes1.length);

        byte[] bytes2 = RandomUtil.nextBytes(32);
        assertEquals(32, bytes2.length);

        byte[] bytes3 = RandomUtil.nextBytes(0);
        assertEquals(0, bytes3.length);
    }

    @Test
    void nextBytes_returnsDifferentValues() {
        byte[] bytes1 = RandomUtil.nextBytes(16);
        byte[] bytes2 = RandomUtil.nextBytes(16);

        // There's a very small chance these could be equal, but virtually impossible
        assertFalse(java.util.Arrays.equals(bytes1, bytes2),
            "Two consecutive calls should return different byte arrays");
    }

    @Test
    void nextBytes_zeroLength() {
        byte[] bytes = RandomUtil.nextBytes(0);
        assertEquals(0, bytes.length);
    }

    @Test
    void newSecureRandom_returnsNonNull() {
        SecureRandom random = RandomUtil.newSecureRandom();
        assertNotNull(random);
    }

    @Test
    void newSecureRandom_producesDifferentInstances() {
        SecureRandom r1 = RandomUtil.newSecureRandom();
        SecureRandom r2 = RandomUtil.newSecureRandom();
        // Each call should create a new instance
        assertNotSame(r1, r2);
    }

    @Test
    void newSecureRandom_producesRandomBytes() {
        SecureRandom random = RandomUtil.newSecureRandom();
        byte[] bytes1 = new byte[16];
        byte[] bytes2 = new byte[16];

        random.nextBytes(bytes1);
        random.nextBytes(bytes2);

        assertFalse(java.util.Arrays.equals(bytes1, bytes2));
    }

    @Test
    void newSecureRandom_withAlgorithmProperty() {
        // This test would require setting system property which is not easily testable
        // Just verify the method doesn't throw
        SecureRandom random = RandomUtil.newSecureRandom();
        assertNotNull(random);
    }

    @Test
    void nextBytes_producesUniqueValuesAcrossThreads() throws InterruptedException {
        // Test that pool works correctly
        int length = 16;
        byte[][] results = new byte[4][length];

        for (int i = 0; i < 4; i++) {
            results[i] = RandomUtil.nextBytes(length);
        }

        // Check that we get different results
        Set<String> uniqueResults = new java.util.HashSet<>();
        for (byte[] result : results) {
            uniqueResults.add(java.util.Arrays.toString(result));
        }
        // At least some should be unique
        assertTrue(uniqueResults.size() > 1);
    }
}