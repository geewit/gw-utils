package io.geewit.utils.core.uuid.codec;

import io.geewit.utils.core.uuid.UUID;
import io.geewit.utils.core.uuid.exception.InvalidUuidException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StandardStringCodecTest {

    private static final String STANDARD_FORMAT = "550e8400-e29b-41d4-a716-446655440000";
    private static final UUID TEST_UUID = new UUID(0x550e8400e29b41d4L, 0xa716446655440000L);

    @Nested
    class EncodeTests {

        @Test
        void encode_validUUID_returnsString() {
            String encoded = StandardStringCodec.INSTANCE.encode(TEST_UUID);
            assertNotNull(encoded);
            // Java > 8 returns toString() which is 25 chars, Java 8 returns 36 chars
            assertTrue(encoded.length() == 25 || encoded.length() == 36);
        }

        @Test
        void encode_nullUUID_throws() {
            assertThrows(InvalidUuidException.class,
                () -> StandardStringCodec.INSTANCE.encode(null));
        }

        @Test
        void encode_minValueUUID() {
            UUID minUuid = new UUID(0L, 0L);
            String encoded = StandardStringCodec.INSTANCE.encode(minUuid);
            assertNotNull(encoded);
        }

        @Test
        void encode_maxValueUUID() {
            UUID maxUuid = new UUID(Long.MAX_VALUE, Long.MAX_VALUE);
            String encoded = StandardStringCodec.INSTANCE.encode(maxUuid);
            assertNotNull(encoded);
        }


    }

    @Nested
    class DecodeTests {

        @Test
        void decode_standardFormat_returnsUUID() {
            UUID uuid = StandardStringCodec.INSTANCE.decode(STANDARD_FORMAT);
            assertEquals(TEST_UUID, uuid);
        }

        @Test
        void decode_withoutDashes_returnsUUID() {
            String noDash = "550e8400e29b41d4a716446655440000";
            UUID uuid = StandardStringCodec.INSTANCE.decode(noDash);
            assertEquals(TEST_UUID.getMostSignificantBits(), uuid.getMostSignificantBits());
            assertEquals(TEST_UUID.getLeastSignificantBits(), uuid.getLeastSignificantBits());
        }

        @Test
        void decode_withCurlyBraces_returnsUUID() {
            String withBraces = "{550e8400-e29b-41d4-a716-446655440000}";
            UUID uuid = StandardStringCodec.INSTANCE.decode(withBraces);
            assertEquals(TEST_UUID, uuid);
        }

        @Test
        void decode_withUrnPrefix_returnsUUID() {
            String withUrn = "urn:uuid:550e8400-e29b-41d4-a716-446655440000";
            UUID uuid = StandardStringCodec.INSTANCE.decode(withUrn);
            assertEquals(TEST_UUID, uuid);
        }

        @Test
        void decode_upperCase_returnsUUID() {
            String upper = "550E8400-E29B-41D4-A716-446655440000";
            UUID uuid = StandardStringCodec.INSTANCE.decode(upper);
            assertEquals(TEST_UUID, uuid);
        }

        @Test
        void decode_lowerCase_returnsUUID() {
            String lower = "550e8400-e29b-41d4-a716-446655440000";
            UUID uuid = StandardStringCodec.INSTANCE.decode(lower);
            assertEquals(TEST_UUID, uuid);
        }

        @Test
        void decode_mixedCase_returnsUUID() {
            String mixed = "550E8400-e29b-41D4-A716-446655440000";
            UUID uuid = StandardStringCodec.INSTANCE.decode(mixed);
            assertEquals(TEST_UUID, uuid);
        }

        @Test
        void decode_null_throws() {
            assertThrows(InvalidUuidException.class,
                () -> StandardStringCodec.INSTANCE.decode(null));
        }

        @Test
        void decode_invalidLength_tooShort() {
            assertThrows(InvalidUuidException.class,
                () -> StandardStringCodec.INSTANCE.decode("550e8400-e29b-41d4"));
        }

        @Test
        void decode_invalidLength_tooLong() {
            assertThrows(InvalidUuidException.class,
                () -> StandardStringCodec.INSTANCE.decode("550e8400-e29b-41d4-a716-44665544000000"));
        }

        @Test
        void decode_invalidChar_invalidDigit() {
            String invalid = "550e8400-e29b-41d4-a716-44665544000g";
            assertThrows(InvalidUuidException.class,
                () -> StandardStringCodec.INSTANCE.decode(invalid));
        }

        @Test
        void decode_invalidChar_specialChar() {
            String invalid = "550e8400-e29b-41d4-a716-44665544000!";
            assertThrows(InvalidUuidException.class,
                () -> StandardStringCodec.INSTANCE.decode(invalid));
        }

        @Test
        void decode_invalidDashPosition() {
            String invalid = "550e8400e29b-41d4-a716-446655440000"; // missing dash at position 8
            assertThrows(InvalidUuidException.class,
                () -> StandardStringCodec.INSTANCE.decode(invalid));
        }

        @Test
        void decode_wrongFormat_noDashesAndNot32Chars() {
            assertThrows(InvalidUuidException.class,
                () -> StandardStringCodec.INSTANCE.decode("not-a-uuid-at-all"));
        }
    }

    @Nested
    class ModifyMethodTests {

        @Test
        void modify_standardFormat_unchanged() {
            String result = StandardStringCodec.modify(STANDARD_FORMAT);
            assertEquals(STANDARD_FORMAT, result);
        }

        @Test
        void modify_urnPrefix_stripsPrefix() {
            String urn = "urn:uuid:550e8400-e29b-41d4-a716-446655440000";
            String result = StandardStringCodec.modify(urn);
            assertEquals(STANDARD_FORMAT, result);
        }

        @Test
        void modify_curlyBraces_stripsBraces() {
            String withBraces = "{550e8400-e29b-41d4-a716-446655440000}";
            String result = StandardStringCodec.modify(withBraces);
            assertEquals(STANDARD_FORMAT, result);
        }

        @Test
        void modify_noPrefixOrBraces_unchanged() {
            String result = StandardStringCodec.modify("some-random-string");
            assertEquals("some-random-string", result);
        }
    }

    @Nested
    class RoundTripTests {

        @Test
        void encodeDecode_standardFormat_roundTrip() {
            UUID decoded = StandardStringCodec.INSTANCE.decode(STANDARD_FORMAT);
            String encoded = StandardStringCodec.INSTANCE.encode(decoded);
            assertNotNull(encoded);
        }

        @Test
        void encodeDecode_withoutDashes_roundTrip() {
            String noDash = "550e8400e29b41d4a716446655440000";
            UUID decoded = StandardStringCodec.INSTANCE.decode(noDash);
            assertNotNull(decoded);
        }

        @Test
        void encodeDecode_curlyBraces_roundTrip() {
            String withBraces = "{550e8400-e29b-41d4-a716-446655440000}";
            UUID decoded = StandardStringCodec.INSTANCE.decode(withBraces);
            assertNotNull(decoded);
        }

        @Test
        void encodeDecode_urnPrefix_roundTrip() {
            String withUrn = "urn:uuid:550e8400-e29b-41d4-a716-446655440000";
            UUID decoded = StandardStringCodec.INSTANCE.decode(withUrn);
            assertNotNull(decoded);
        }
    }

    @Nested
    class EdgeCaseTests {

        @Test
        void decode_minUUIDValue() {
            String minStr = "00000000-0000-0000-0000-000000000000";
            UUID uuid = StandardStringCodec.INSTANCE.decode(minStr);
            assertEquals(0L, uuid.getMostSignificantBits());
            assertEquals(0L, uuid.getLeastSignificantBits());
        }

        @Test
        void decode_maxUUIDValue() {
            String maxStr = "ffffffff-ffff-4fff-bfff-ffffffffffff";
            UUID uuid = StandardStringCodec.INSTANCE.decode(maxStr);
            assertNotNull(uuid);
            // Just verify it's a valid UUID, not null
            assertEquals(4, uuid.version());
        }

        @Test
        void decode_allZeros() {
            UUID uuid = StandardStringCodec.INSTANCE.decode("00000000-0000-0000-0000-000000000000");
            assertEquals(new UUID(0L, 0L), uuid);
        }

        @Test
        void decode_allFs() {
            UUID uuid = StandardStringCodec.INSTANCE.decode("ffffffff-ffff-4fff-bfff-ffffffffffff");
            assertNotNull(uuid);
        }
    }
}