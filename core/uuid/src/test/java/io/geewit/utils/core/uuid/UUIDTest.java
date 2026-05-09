package io.geewit.utils.core.uuid;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UUIDTest {

    @Test
    void randomUUID_generatesValidUUID() {
        UUID uuid = UUID.randomUUID();
        assertNotNull(uuid);
        assertEquals(4, uuid.version());
        assertEquals(2, uuid.variant());
        assertEquals(25, uuid.toString().length());
    }

    @Test
    void randomUUID_unique() {
        UUID u1 = UUID.randomUUID();
        UUID u2 = UUID.randomUUID();
        assertNotEquals(u1, u2);
    }

    @Test
    void nameUUIDFromBytes_generatesConsistentUUID() {
        byte[] name = "test".getBytes();
        UUID u1 = UUID.nameUUIDFromBytes(name);
        UUID u2 = UUID.nameUUIDFromBytes(name);
        assertEquals(u1, u2);
        assertEquals(3, u1.version());
    }

    @Test
    void fromString_andToString_roundTrip() {
        // Use a small fixed UUID value to ensure round-trip accuracy
        UUID original = UUID.fromString("1");
        String str = original.toString();
        assertEquals(25, str.length());
        UUID parsed = UUID.fromString(str);
        assertEquals(original, parsed);
    }

    @Test
    void fromString_shortString_padded() {
        UUID uuid = UUID.fromString("1");
        assertNotNull(uuid);
    }

    @Test
    void fromString_invalidCharacterThrows() {
        assertThrows(IllegalArgumentException.class, () -> UUID.fromString("!"));
    }

    @Test
    void fromString_tooLongThrows() {
        assertThrows(IllegalArgumentException.class, () -> UUID.fromString("12345678901234567890123456"));
    }

    @Test
    void fromString_caseInsensitive() {
        UUID lower = UUID.fromString("abc");
        UUID upper = UUID.fromString("ABC");
        assertEquals(lower, upper);
    }

    @Test
    void getBits() {
        UUID uuid = new UUID(0x123456789ABCDEF0L, 0x0FEDCBA987654321L);
        assertEquals(0x123456789ABCDEF0L, uuid.getMostSignificantBits());
        assertEquals(0x0FEDCBA987654321L, uuid.getLeastSignificantBits());
    }

    @Test
    void version_randomUUID() {
        UUID uuid = UUID.randomUUID();
        assertEquals(4, uuid.version());
    }

    @Test
    void variant_standard() {
        UUID uuid = UUID.randomUUID();
        assertEquals(2, uuid.variant());
    }

    @Test
    void variant_variants() {
        UUID v0 = new UUID(0L, 0L);
        assertEquals(0, v0.variant());

        UUID v2 = new UUID(0L, 0x8000000000000000L);
        assertEquals(2, v2.variant());

        UUID v6 = new UUID(0L, 0xC000000000000000L);
        assertEquals(6, v6.variant());

        UUID v7 = new UUID(0L, 0xE000000000000000L);
        assertEquals(7, v7.variant());
    }

    @Test
    void timestamp_notTimeBasedThrows() {
        UUID uuid = UUID.randomUUID();
        assertThrows(UnsupportedOperationException.class, uuid::timestamp);
    }

    @Test
    void timestamp_timeBased() {
        // Version 1 UUID
        UUID uuid = new UUID(0x0000000000001000L, 0x8000000000000000L);
        assertDoesNotThrow(uuid::timestamp);
    }

    @Test
    void clockSequence_notTimeBasedThrows() {
        UUID uuid = UUID.randomUUID();
        assertThrows(UnsupportedOperationException.class, uuid::clockSequence);
    }

    @Test
    void node_notTimeBasedThrows() {
        UUID uuid = UUID.randomUUID();
        assertThrows(UnsupportedOperationException.class, uuid::node);
    }

    @Test
    void equals_sameObject() {
        UUID uuid = UUID.randomUUID();
        assertEquals(uuid, uuid);
    }

    @Test
    void equals_differentType() {
        UUID uuid = UUID.randomUUID();
        assertNotEquals(uuid, "string");
    }

    @Test
    void equals_sameBits() {
        UUID u1 = new UUID(1L, 2L);
        UUID u2 = new UUID(1L, 2L);
        assertEquals(u1, u2);
    }

    @Test
    void hashCode_consistent() {
        UUID u1 = new UUID(1L, 2L);
        UUID u2 = new UUID(1L, 2L);
        assertEquals(u1.hashCode(), u2.hashCode());
    }

    @Test
    void compareTo_lessThan() {
        UUID u1 = new UUID(1L, 2L);
        UUID u2 = new UUID(2L, 2L);
        assertTrue(u1.compareTo(u2) < 0);
    }

    @Test
    void compareTo_greaterThan() {
        UUID u1 = new UUID(2L, 2L);
        UUID u2 = new UUID(1L, 2L);
        assertTrue(u1.compareTo(u2) > 0);
    }

    @Test
    void compareTo_equal() {
        UUID u1 = new UUID(1L, 2L);
        UUID u2 = new UUID(1L, 2L);
        assertEquals(0, u1.compareTo(u2));
    }

    @Test
    void compareTo_sameMsbDifferentLsb() {
        UUID u1 = new UUID(1L, 1L);
        UUID u2 = new UUID(1L, 2L);
        assertTrue(u1.compareTo(u2) < 0);
    }

    @Test
    void nilUUID() {
        UUID nil = UuidCreator.getNil();
        assertEquals(0L, nil.getMostSignificantBits());
        assertEquals(0L, nil.getLeastSignificantBits());
    }

    @Test
    void maxUUID() {
        UUID max = UuidCreator.getMax();
        assertEquals(0xFFFFFFFFFFFFFFFFL, max.getMostSignificantBits());
        assertEquals(0xFFFFFFFFFFFFFFFFL, max.getLeastSignificantBits());
    }

    @Test
    void uuidCreatorToString() {
        UUID uuid = UUID.randomUUID();
        String str = UuidCreator.toString(uuid);
        assertNotNull(str);
    }

    @Test
    void uuidCreatorFromString() {
        String standard = "550e8400-e29b-41d4-a716-446655440000";
        UUID uuid = UuidCreator.fromString(standard);
        assertNotNull(uuid);
    }

    @Test
    void uuidCreatorFromString_withBraces() {
        String withBraces = "{550e8400-e29b-41d4-a716-446655440000}";
        UUID uuid = UuidCreator.fromString(withBraces);
        assertNotNull(uuid);
    }

    @Test
    void uuidCreatorFromString_withUrn() {
        String urn = "urn:uuid:550e8400-e29b-41d4-a716-446655440000";
        UUID uuid = UuidCreator.fromString(urn);
        assertNotNull(uuid);
    }

    @Test
    void getTimeOrderedEpoch_generatesUUID() {
        UUID uuid = UuidCreator.getTimeOrderedEpoch();
        assertNotNull(uuid);
        assertEquals(7, uuid.version());
    }

    @Test
    void getTimeOrderedEpochFast_generatesUUID() {
        UUID uuid = UuidCreator.getTimeOrderedEpochFast();
        assertNotNull(uuid);
        assertEquals(7, uuid.version());
    }

    @Test
    void getTimeOrderedEpochPlus1_generatesUUID() {
        UUID uuid = UuidCreator.getTimeOrderedEpochPlus1();
        assertNotNull(uuid);
        assertEquals(7, uuid.version());
    }

    @Test
    void getTimeOrderedEpochPlusN_generatesUUID() {
        UUID uuid = UuidCreator.getTimeOrderedEpochPlusN();
        assertNotNull(uuid);
        assertEquals(7, uuid.version());
    }

    @Test
    void getTimeOrderedEpoch_withInstant() {
        java.time.Instant instant = java.time.Instant.now();
        UUID uuid = UuidCreator.getTimeOrderedEpoch(instant);
        assertNotNull(uuid);
        assertEquals(7, uuid.version());
    }

    @Test
    void getTimeOrderedEpochMin_withInstant() {
        java.time.Instant instant = java.time.Instant.now();
        UUID min = UuidCreator.getTimeOrderedEpochMin(instant);
        assertNotNull(min);
    }

    @Test
    void getTimeOrderedEpochMax_withInstant() {
        java.time.Instant instant = java.time.Instant.now();
        UUID max = UuidCreator.getTimeOrderedEpochMax(instant);
        assertNotNull(max);
    }
}
