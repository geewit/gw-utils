package io.geewit.utils.core.uuid.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UuidVersionTest {

    @Test
    void versionUnknown_hasValueZero() {
        assertEquals(0, UuidVersion.VERSION_UNKNOWN.getValue());
    }

    @Test
    void versionTimeOrderedEpoch_hasValue7() {
        assertEquals(7, UuidVersion.VERSION_TIME_ORDERED_EPOCH.getValue());
    }

    @Test
    void enumValues_areCorrect() {
        UuidVersion[] values = UuidVersion.values();
        assertEquals(2, values.length);
    }

    @Test
    void valueOf_versionUnknown() {
        UuidVersion version = UuidVersion.valueOf("VERSION_UNKNOWN");
        assertEquals(UuidVersion.VERSION_UNKNOWN, version);
    }

    @Test
    void valueOf_versionTimeOrderedEpoch() {
        UuidVersion version = UuidVersion.valueOf("VERSION_TIME_ORDERED_EPOCH");
        assertEquals(UuidVersion.VERSION_TIME_ORDERED_EPOCH, version);
    }

    @Test
    void toString_containsVersionName() {
        assertTrue(UuidVersion.VERSION_UNKNOWN.toString().contains("UNKNOWN"));
        assertTrue(UuidVersion.VERSION_TIME_ORDERED_EPOCH.toString().contains("TIME_ORDERED_EPOCH"));
    }

    @Test
    void name_returnsCorrectString() {
        assertEquals("VERSION_UNKNOWN", UuidVersion.VERSION_UNKNOWN.name());
        assertEquals("VERSION_TIME_ORDERED_EPOCH", UuidVersion.VERSION_TIME_ORDERED_EPOCH.name());
    }

    @Test
    void ordinal_valuesAreOrdered() {
        assertEquals(0, UuidVersion.VERSION_UNKNOWN.ordinal());
        assertEquals(1, UuidVersion.VERSION_TIME_ORDERED_EPOCH.ordinal());
    }
}