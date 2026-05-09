package io.geewit.utils.core.uuid.factory;

import io.geewit.utils.core.uuid.UUID;
import io.geewit.utils.core.uuid.enums.UuidVersion;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class UuidFactoryTest {

    @Test
    void defaultConstructor_setsUnknownVersion() {
        TestableUuidFactory factory = new TestableUuidFactory();
        assertEquals(UuidVersion.VERSION_UNKNOWN, factory.version);
        assertEquals(0L, factory.versionMask);
    }

    @Test
    void constructor_withVersion_setsCorrectVersion() {
        TestableUuidFactory factory = new TestableUuidFactory(UuidVersion.VERSION_TIME_ORDERED_EPOCH);
        assertEquals(UuidVersion.VERSION_TIME_ORDERED_EPOCH, factory.version);
        assertEquals(0x7000L, factory.versionMask); // version 7 << 12
    }

    @Test
    void toUuid_appliesVersionAndVariant() {
        TestableUuidFactory factory = new TestableUuidFactory(UuidVersion.VERSION_TIME_ORDERED_EPOCH);
        UUID uuid = factory.toUuid(0x123456789ABC0000L, 0x0FEDCBA987654321L);

        // Check version bits (bits 12-15 of MSB should be 7)
        assertEquals(7, uuid.version());

        // Check variant bits (bits 62-63 of LSB should be 10)
        assertEquals(2, uuid.variant());
    }

    @Test
    void toUuid_version7Mask() {
        // Test that version mask correctly sets version 7
        TestableUuidFactory factory = new TestableUuidFactory(UuidVersion.VERSION_TIME_ORDERED_EPOCH);

        // Input MSB without version: 0x123456789ABC0000L
        // After applying mask: should have 0x7000 in bits 12-15
        UUID uuid = factory.toUuid(0x123456789ABC0000L, 0x8000000000000000L);

        long msb = uuid.getMostSignificantBits();
        // Version should be in bits 12-15
        long versionFromMsb = (msb >> 12) & 0xF;
        assertEquals(7, versionFromMsb);
    }

    @Test
    void parameters_builder() {
        Instant instant = Instant.now();
        UuidFactory.Parameters params = UuidFactory.Parameters.builder()
                .withInstant(instant)
                .build();

        assertEquals(instant, params.instant());
    }

    @Test
    void parameters_builder_rejectsNullInstant() {
        assertThrows(NullPointerException.class, () ->
                UuidFactory.Parameters.builder()
                        .withInstant(null)
                        .build());
    }

    @Test
    void parameters_instantRoundTrip() {
        Instant instant = Instant.parse("2024-06-15T12:30:00Z");
        UuidFactory.Parameters params = UuidFactory.Parameters.builder()
                .withInstant(instant)
                .build();

        assertEquals(instant, params.instant());
    }

    // Testable subclass to access protected members
    private static class TestableUuidFactory extends UuidFactory {
        TestableUuidFactory() {
            super();
        }

        TestableUuidFactory(UuidVersion version) {
            super(version);
        }

        @Override
        public UUID create() {
            return new UUID(0, 0);
        }

        @Override
        public UUID create(Parameters parameters) {
            return create();
        }
    }
}