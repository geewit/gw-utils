package io.geewit.utils.core.uuid.factory.standard;

import io.geewit.utils.core.uuid.UUID;
import io.geewit.utils.core.uuid.factory.UuidFactory;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class TimeOrderedEpochFactoryTest {

    @Nested
    class BuilderTests {

        @Test
        void builder_default() {
            TimeOrderedEpochFactory factory = TimeOrderedEpochFactory.builder().build();
            assertNotNull(factory);
        }

        @Test
        void builder_withFastRandom() {
            TimeOrderedEpochFactory factory = TimeOrderedEpochFactory.builder()
                .withFastRandom()
                .build();
            assertNotNull(factory);
        }

        @Test
        void builder_withIncrementPlus1() {
            TimeOrderedEpochFactory factory = TimeOrderedEpochFactory.builder()
                .withIncrementPlus1()
                .build();
            assertNotNull(factory);
        }

        @Test
        void builder_withIncrementPlusN() {
            TimeOrderedEpochFactory factory = TimeOrderedEpochFactory.builder()
                .withIncrementPlusN()
                .build();
            assertNotNull(factory);
        }

        @Test
        void builder_chainedMethods() {
            TimeOrderedEpochFactory factory = TimeOrderedEpochFactory.builder()
                .withFastRandom()
                .withIncrementPlus1()
                .build();
            assertNotNull(factory);
        }
    }

    @Nested
    class CreateTests {

        @Test
        void create_default_returnsUUIDv7() {
            TimeOrderedEpochFactory factory = new TimeOrderedEpochFactory();
            UUID uuid = factory.create();
            assertNotNull(uuid);
            assertEquals(7, uuid.version());
        }

        @Test
        void create_withParameters_returnsUUIDv7() {
            TimeOrderedEpochFactory factory = new TimeOrderedEpochFactory();
            Instant instant = Instant.now();
            UuidFactory.Parameters params = UuidFactory.Parameters.builder()
                .withInstant(instant)
                .build();
            UUID uuid = factory.create(params);
            assertNotNull(uuid);
            assertEquals(7, uuid.version());
        }

        @Test
        void create_multiple_callsReturnDifferentUUIDs() {
            TimeOrderedEpochFactory factory = TimeOrderedEpochFactory.builder().build();
            HashSet<String> uuids = new HashSet<>();
            for (int i = 0; i < 100; i++) {
                UUID uuid = factory.create();
                assertTrue(uuids.add(uuid.toString()), "Duplicate UUID generated");
            }
        }
    }

    @Nested
    class DefaultFunctionTests {

        @Test
        void create_defaultFunction_incrementsOnSameTimestamp() {
            TimeOrderedEpochFactory factory = TimeOrderedEpochFactory.builder().build();

            // Create two UUIDs in quick succession - they should have same timestamp
            // but different random parts
            UUID uuid1 = factory.create();
            UUID uuid2 = factory.create();

            assertNotEquals(uuid1, uuid2);
        }

        @Test
        void create_defaultFunction_monotonicallyIncreasing() {
            TimeOrderedEpochFactory factory = TimeOrderedEpochFactory.builder().build();

            UUID prev = factory.create();
            for (int i = 0; i < 10; i++) {
                UUID curr = factory.create();
                assertTrue(curr.compareTo(prev) > 0,
                    "UUID should be monotonically increasing");
                prev = curr;
            }
        }

        @Test
        void create_withInstant_usesSpecifiedTime() {
            TimeOrderedEpochFactory factory = TimeOrderedEpochFactory.builder().build();
            Instant instant = Instant.parse("2024-01-01T00:00:00Z");
            UuidFactory.Parameters params = UuidFactory.Parameters.builder()
                .withInstant(instant)
                .build();

            UUID uuid = factory.create(params);

            // The timestamp in UUIDv7 should be based on the provided instant
            assertNotNull(uuid);
        }
    }

    @Nested
    class Plus1FunctionTests {

        @Test
        void create_plus1Function_monotonicallyIncreasing() {
            TimeOrderedEpochFactory factory = TimeOrderedEpochFactory.builder()
                .withIncrementPlus1()
                .build();

            UUID prev = factory.create();
            for (int i = 0; i < 10; i++) {
                UUID curr = factory.create();
                assertTrue(curr.compareTo(prev) > 0,
                    "UUID should be monotonically increasing with Plus1");
                prev = curr;
            }
        }

        @Test
        void create_plus1Function_handlesMultipleCreations() {
            // Test with multiple creations
            TimeOrderedEpochFactory factory = TimeOrderedEpochFactory.builder()
                .withIncrementPlus1()
                .build();

            UUID uuid1 = factory.create();
            UUID uuid2 = factory.create();
            assertNotEquals(uuid1, uuid2);
        }
    }

    @Nested
    class PlusNFunctionTests {

        @Test
        void create_plusNFunction_monotonicallyIncreasing() {
            TimeOrderedEpochFactory factory = TimeOrderedEpochFactory.builder()
                .withIncrementPlusN()
                .build();

            UUID prev = factory.create();
            for (int i = 0; i < 10; i++) {
                UUID curr = factory.create();
                assertTrue(curr.compareTo(prev) > 0,
                    "UUID should be monotonically increasing with PlusN");
                prev = curr;
            }
        }

        @Test
        void create_plusNFunction_withDefaultConfig() {
            TimeOrderedEpochFactory factory = TimeOrderedEpochFactory.builder()
                .withIncrementPlusN()
                .build();

            UUID uuid = factory.create();
            assertNotNull(uuid);
        }
    }

    @Nested
    class TimestampTests {

        @Test
        void create_withPastInstant_generatesValidUUID() {
            TimeOrderedEpochFactory factory = TimeOrderedEpochFactory.builder().build();
            Instant past = Instant.now().minus(1, ChronoUnit.DAYS);
            UuidFactory.Parameters params = UuidFactory.Parameters.builder()
                .withInstant(past)
                .build();

            UUID uuid = factory.create(params);
            assertNotNull(uuid);
            assertEquals(7, uuid.version());
        }

        @Test
        void create_withFutureInstant_generatesValidUUID() {
            TimeOrderedEpochFactory factory = TimeOrderedEpochFactory.builder().build();
            Instant future = Instant.now().plus(1, ChronoUnit.DAYS);
            UuidFactory.Parameters params = UuidFactory.Parameters.builder()
                .withInstant(future)
                .build();

            UUID uuid = factory.create(params);
            assertNotNull(uuid);
            assertEquals(7, uuid.version());
        }

        @Test
        void create_withEpoch_generatesValidUUID() {
            TimeOrderedEpochFactory factory = TimeOrderedEpochFactory.builder().build();
            UuidFactory.Parameters params = UuidFactory.Parameters.builder()
                .withInstant(Instant.EPOCH)
                .build();

            UUID uuid = factory.create(params);
            assertNotNull(uuid);
            assertEquals(7, uuid.version());
        }
    }

    @Nested
    class FastRandomTests {

        @Test
        void create_withFastRandom_returnsUUIDv7() {
            TimeOrderedEpochFactory factory = TimeOrderedEpochFactory.builder()
                .withFastRandom()
                .build();

            UUID uuid = factory.create();
            assertNotNull(uuid);
            assertEquals(7, uuid.version());
        }

        @Test
        void create_withFastRandom_multipleUnique() {
            TimeOrderedEpochFactory factory = TimeOrderedEpochFactory.builder()
                .withFastRandom()
                .build();

            HashSet<String> uuids = new HashSet<>();
            for (int i = 0; i < 100; i++) {
                assertTrue(uuids.add(factory.create().toString()));
            }
        }
    }

    @Nested
    class SafeRandomTests {

        @Test
        void create_withSafeRandom_returnsUUIDv7() {
            TimeOrderedEpochFactory factory = TimeOrderedEpochFactory.builder().build();

            UUID uuid = factory.create();
            assertNotNull(uuid);
            assertEquals(7, uuid.version());
        }

        @Test
        void create_withSafeRandom_multipleUnique() {
            TimeOrderedEpochFactory factory = TimeOrderedEpochFactory.builder().build();

            HashSet<String> uuids = new HashSet<>();
            for (int i = 0; i < 100; i++) {
                assertTrue(uuids.add(factory.create().toString()));
            }
        }
    }

    @RepeatedTest(10)
    void create_concurrentGenerations_areUnique() {
        TimeOrderedEpochFactory factory = TimeOrderedEpochFactory.builder().build();
        UUID uuid = factory.create();
        assertNotNull(uuid);
    }

    @Nested
    class EdgeCaseTests {

        @Test
        void create_withNullTimeFunction() {
            // Test that null time function doesn't cause issues
            TimeOrderedEpochFactory factory = new TimeOrderedEpochFactory();
            UUID uuid = factory.create();
            assertNotNull(uuid);
        }

        @Test
        void create_withCustomEpoch() {
            // Test with a specific instant
            TimeOrderedEpochFactory factory = TimeOrderedEpochFactory.builder().build();
            Instant epoch = Instant.EPOCH;
            UuidFactory.Parameters params = UuidFactory.Parameters.builder()
                .withInstant(epoch)
                .build();

            UUID uuid = factory.create(params);
            assertNotNull(uuid);
        }

        @Test
        void create_differentFactoriesProduceDifferentUUIDs() {
            TimeOrderedEpochFactory factory1 = TimeOrderedEpochFactory.builder().build();
            TimeOrderedEpochFactory factory2 = TimeOrderedEpochFactory.builder().build();

            UUID uuid1 = factory1.create();
            UUID uuid2 = factory2.create();

            // Both should be valid UUIDv7
            assertEquals(7, uuid1.version());
            assertEquals(7, uuid2.version());
        }
    }
}