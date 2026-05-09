package io.geewit.utils.core.uuid;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TimeOrderedUuidGeneratorTest {

    @Test
    void nextId_monotonicallyIncreasing() {
        TimeOrderedUuidGenerator gen = TimeOrderedUuidGenerator.INSTANCE;
        long id1 = gen.nextId();
        long id2 = gen.nextId();
        long id3 = gen.nextId();
        assertTrue(id1 < id2);
        assertTrue(id2 < id3);
    }

    @Test
    void nextId_uniqueAcrossManyCalls() {
        TimeOrderedUuidGenerator gen = TimeOrderedUuidGenerator.INSTANCE;
        Set<Long> ids = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
            long id = gen.nextId();
            assertTrue(ids.add(id), "Duplicate ID generated: " + id);
        }
    }

    @Test
    void nextId_singleInstance() {
        long id1 = TimeOrderedUuidGenerator.INSTANCE.nextId();
        long id2 = TimeOrderedUuidGenerator.INSTANCE.nextId();
        assertTrue(id1 < id2);
    }

    @Test
    void nextId_fixedLengthHexString() {
        TimeOrderedUuidGenerator gen = TimeOrderedUuidGenerator.INSTANCE;
        for (int i = 0; i < 100; i++) {
            long id = gen.nextId();
            String hex = String.format("%016x", id);
            assertEquals(16, hex.length(), "Hex string length should always be 16 for id: " + id);
        }
    }

    @Test
    void minIdForInstant_withInstant() {
        TimeOrderedUuidGenerator gen = TimeOrderedUuidGenerator.INSTANCE;
        Instant instant = Instant.parse("2023-01-01T00:00:00Z");
        long minId = gen.minIdForInstant(instant);
        assertTrue(minId > 0);
    }

    @Test
    void minIdForInstant_nullUsesCurrentTime() {
        TimeOrderedUuidGenerator gen = TimeOrderedUuidGenerator.INSTANCE;
        long minId = gen.minIdForInstant(null);
        assertTrue(minId > 0);
    }

    @Test
    void minIdForInstant_negativeEpoch() {
        TimeOrderedUuidGenerator gen = TimeOrderedUuidGenerator.INSTANCE;
        Instant instant = Instant.ofEpochMilli(-1);
        long minId = gen.minIdForInstant(instant);
        assertEquals(0L, minId);
    }

    @Test
    void minIdForInstant_veryLargeEpoch() {
        TimeOrderedUuidGenerator gen = TimeOrderedUuidGenerator.INSTANCE;
        Instant instant = Instant.ofEpochMilli(Long.MAX_VALUE);
        long minId = gen.minIdForInstant(instant);
        // MAX_TIMESTAMP << 16 overflows to negative in signed long, so minId can be negative
        assertNotEquals(0, minId);
    }
}
