package io.geewit.utils.core.uuid;

import io.geewit.utils.core.uuid.exception.InvalidUuidException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UuidCreatorTest {

    @Test
    void getNil_returnsCorrectUUID() {
        UUID nil = UuidCreator.getNil();
        assertEquals(0L, nil.getMostSignificantBits());
        assertEquals(0L, nil.getLeastSignificantBits());
        assertEquals(0, nil.version());
    }

    @Test
    void getMax_returnsCorrectUUID() {
        UUID max = UuidCreator.getMax();
        assertEquals(0xFFFFFFFFFFFFFFFFL, max.getMostSignificantBits());
        assertEquals(0xFFFFFFFFFFFFFFFFL, max.getLeastSignificantBits());
    }

    @Test
    void toString_validUUID() {
        UUID uuid = new UUID(0x550e8400e29b41d4L, 0xa716446655440000L);
        String str = UuidCreator.toString(uuid);
        assertNotNull(str);
    }

    @Test
    void toString_nullThrows() {
        assertThrows(InvalidUuidException.class, () -> UuidCreator.toString(null));
    }

    @Test
    void fromString_standardFormat() {
        UUID uuid = UuidCreator.fromString("550e8400-e29b-41d4-a716-446655440000");
        assertNotNull(uuid);
        assertEquals(0x550e8400e29b41d4L, uuid.getMostSignificantBits());
        assertEquals(0xa716446655440000L, uuid.getLeastSignificantBits());
    }

    @Test
    void fromString_upperCase() {
        UUID uuid = UuidCreator.fromString("550E8400-E29B-41D4-A716-446655440000");
        assertNotNull(uuid);
    }

    @Test
    void fromString_withBraces() {
        UUID uuid = UuidCreator.fromString("{550e8400-e29b-41d4-a716-446655440000}");
        assertNotNull(uuid);
    }

    @Test
    void fromString_withUrnPrefix() {
        UUID uuid = UuidCreator.fromString("urn:uuid:550e8400-e29b-41d4-a716-446655440000");
        assertNotNull(uuid);
    }

    @Test
    void fromString_withoutHyphens() {
        UUID uuid = UuidCreator.fromString("550e8400e29b41d4a716446655440000");
        assertNotNull(uuid);
    }

    @Test
    void fromString_invalidCharThrows() {
        assertThrows(InvalidUuidException.class,
                () -> UuidCreator.fromString("550e8400-e29b-41d4-a716-44665544000g"));
    }

    @Test
    void fromString_invalidLengthThrows() {
        assertThrows(InvalidUuidException.class, () -> UuidCreator.fromString("invalid"));
    }

    @Test
    void fromString_nullThrows() {
        assertThrows(InvalidUuidException.class, () -> UuidCreator.fromString(null));
    }

    @Test
    void fromString_emptyThrows() {
        assertThrows(InvalidUuidException.class, () -> UuidCreator.fromString(""));
    }

    @Test
    void getTimeOrderedEpoch_generatesV7UUID() {
        UUID uuid = UuidCreator.getTimeOrderedEpoch();
        assertNotNull(uuid);
        assertEquals(7, uuid.version());
        assertEquals(2, uuid.variant());
    }

    @Test
    void getTimeOrderedEpoch_uniqueness() {
        Set<String> uuids = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
            UUID uuid = UuidCreator.getTimeOrderedEpoch();
            assertTrue(uuids.add(uuid.toString()), "Duplicate UUID generated");
        }
    }

    @Test
    void getTimeOrderedEpochFast_generatesV7UUID() {
        UUID uuid = UuidCreator.getTimeOrderedEpochFast();
        assertNotNull(uuid);
        assertEquals(7, uuid.version());
    }

    @Test
    void getTimeOrderedEpochPlus1_generatesV7UUID() {
        UUID uuid = UuidCreator.getTimeOrderedEpochPlus1();
        assertNotNull(uuid);
        assertEquals(7, uuid.version());
    }

    @Test
    void getTimeOrderedEpochPlusN_generatesV7UUID() {
        UUID uuid = UuidCreator.getTimeOrderedEpochPlusN();
        assertNotNull(uuid);
        assertEquals(7, uuid.version());
    }

    @Test
    void getTimeOrderedEpoch_withInstant() {
        Instant instant = Instant.parse("2024-01-15T10:30:00Z");
        UUID uuid = UuidCreator.getTimeOrderedEpoch(instant);
        assertNotNull(uuid);
        assertEquals(7, uuid.version());
    }

    @Test
    void getTimeOrderedEpoch_withInstantNullThrows() {
        assertThrows(NullPointerException.class,
                () -> UuidCreator.getTimeOrderedEpoch((Instant) null));
    }

    @Test
    void getTimeOrderedEpochMin_withInstant() {
        Instant instant = Instant.parse("2024-01-15T10:30:00Z");
        UUID min = UuidCreator.getTimeOrderedEpochMin(instant);
        assertNotNull(min);
        assertEquals(7, min.version());
    }

    @Test
    void getTimeOrderedEpochMin_nullThrows() {
        assertThrows(NullPointerException.class,
                () -> UuidCreator.getTimeOrderedEpochMin(null));
    }

    @Test
    void getTimeOrderedEpochMax_withInstant() {
        Instant instant = Instant.parse("2024-01-15T10:30:00Z");
        UUID max = UuidCreator.getTimeOrderedEpochMax(instant);
        assertNotNull(max);
        assertEquals(7, max.version());
    }

    @Test
    void getTimeOrderedEpochMax_nullThrows() {
        assertThrows(NullPointerException.class,
                () -> UuidCreator.getTimeOrderedEpochMax(null));
    }

    @Test
    void minAndMaxForSameInstant_areOrdered() {
        Instant instant = Instant.now();
        UUID min = UuidCreator.getTimeOrderedEpochMin(instant);
        UUID max = UuidCreator.getTimeOrderedEpochMax(instant);
        assertTrue(min.compareTo(max) < 0);
    }

    @Test
    void timeOrderedEpoch_monotonicallyIncreasingWithinSameMillisecond() {
        // Generate many UUIDs in quick succession
        UUID first = UuidCreator.getTimeOrderedEpoch();
        boolean foundLater = false;
        for (int i = 0; i < 100; i++) {
            UUID uuid = UuidCreator.getTimeOrderedEpoch();
            if (uuid.compareTo(first) > 0) {
                foundLater = true;
                break;
            }
        }
        assertTrue(foundLater, "Should find UUID generated after first");
    }
}