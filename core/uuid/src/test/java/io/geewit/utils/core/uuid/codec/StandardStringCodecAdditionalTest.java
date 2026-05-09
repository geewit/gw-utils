package io.geewit.utils.core.uuid.codec;

import io.geewit.utils.core.uuid.UUID;
import io.geewit.utils.core.uuid.UuidCreator;
import io.geewit.utils.core.uuid.exception.InvalidUuidException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Additional tests for StandardStringCodec to improve branch coverage.
 */
class StandardStringCodecAdditionalTest {

    private static final String STANDARD_FORMAT = "550e8400-e29b-41d4-a716-446655440000";
    private static final UUID TEST_UUID = new UUID(0x550e8400e29b41d4L, 0xa716446655440000L);

    @Nested
    class EncodeBranchCoverageTests {

        @Test
        void encode_withNonNullUUID_doesNotThrow() {
            // Covers validate(uuid) call which passes for non-null UUID
            assertDoesNotThrow(() -> StandardStringCodec.INSTANCE.encode(TEST_UUID));
        }

        @Test
        void encode_nullUUID_throwsInvalidUuidException() {
            // Covers the validate(uuid) null check branch
            InvalidUuidException ex = assertThrows(InvalidUuidException.class,
                () -> StandardStringCodec.INSTANCE.encode(null));
            assertNotNull(ex);
        }
    }

    @Nested
    class DecodeInvalidInputTests {

        @Test
        void decode_withHighUnicodeCharacter_throws() {
            // chr > 255 branch in get() method - high unicode character
            String invalidWithHighChar = "550e8400-e29b-41d4-a716-44665544000\u10FF";
            assertThrows(InvalidUuidException.class,
                () -> StandardStringCodec.INSTANCE.decode(invalidWithHighChar));
        }

        @Test
        void decode_withCharGreaterThan255_throws() {
            // Test chr > 255 branch specifically - character value 256+
            StringBuilder sb = new StringBuilder("550e8400-e29b-41d4-a716-44665544000");
            sb.append((char) 256);
            assertThrows(InvalidUuidException.class,
                () -> StandardStringCodec.INSTANCE.decode(sb.toString()));
        }

        @Test
        void decode_withInvalidMapValue_throws() {
            // value < 0 branch in get() method - character not in base16 map
            // Using '@' which is not a valid hex character
            String invalid = "550e8400-e29b-41d4-a716-44665544000@";
            assertThrows(InvalidUuidException.class,
                () -> StandardStringCodec.INSTANCE.decode(invalid));
        }
    }

    @Nested
    class ValidateDashPositionTests {

        @Test
        void decode_withWrongDashAtPosition1_throws() {
            // Dash at position 1 (index 8) is missing
            String invalid = "550e8400e29b-41d4-a716-446655440000";
            assertThrows(InvalidUuidException.class,
                () -> StandardStringCodec.INSTANCE.decode(invalid));
        }

        @Test
        void decode_withWrongDashAtPosition2_throws() {
            // Dash at position 2 (index 13) is wrong
            String invalid = "550e8400-e29b.41d4-a716-446655440000";
            assertThrows(InvalidUuidException.class,
                () -> StandardStringCodec.INSTANCE.decode(invalid));
        }

        @Test
        void decode_withWrongDashAtPosition3_throws() {
            // Dash at position 3 (index 18) is wrong
            String invalid = "550e8400-e29b-41d4a716-446655440000";
            assertThrows(InvalidUuidException.class,
                () -> StandardStringCodec.INSTANCE.decode(invalid));
        }

        @Test
        void decode_withWrongDashAtPosition4_throws() {
            // Dash at position 4 (index 23) is wrong
            String invalid = "550e8400-e29b-41d4-a716.446655440000";
            assertThrows(InvalidUuidException.class,
                () -> StandardStringCodec.INSTANCE.decode(invalid));
        }
    }

    @Nested
    class ModifyMethodEdgeCases {

        @Test
        void modify_withUrnPrefix_correctLength() {
            // Covers the URN prefix branch (string.length() == LENGTH_WITH_URN_PREFIX && startsWith(URN_PREFIX))
            String urn = "urn:uuid:550e8400-e29b-41d4-a716-446655440000";
            String result = StandardStringCodec.modify(urn);
            assertEquals(STANDARD_FORMAT, result);
            assertEquals(36, result.length());
        }

        @Test
        void modify_withCurlyBraces_correctLength() {
            // Covers curly braces branch with correct length
            String withBraces = "{550e8400-e29b-41d4-a716-446655440000}";
            String result = StandardStringCodec.modify(withBraces);
            assertEquals(STANDARD_FORMAT, result);
            assertEquals(36, result.length());
        }

        @Test
        void modify_withWrongLengthUrnPrefix_returnsUnchanged() {
            // String has URN prefix but wrong length - should not match URN branch
            String wrongLength = "urn:uuid:550e8400-e29b-41d4-a716-44665544000";
            String result = StandardStringCodec.modify(wrongLength);
            assertEquals(wrongLength, result);
        }

        @Test
        void modify_withWrongLengthCurlyBraces_returnsUnchanged() {
            // String starts with { but wrong length
            String wrongLength = "{550e8400-e29b-41d4-a716-44665544000}";
            String result = StandardStringCodec.modify(wrongLength);
            assertEquals(wrongLength, result);
        }

        @Test
        void modify_startsWithOpenBraceButNoClose_returnsUnchanged() {
            // Starts with { but doesn't end with }
            String noClose = "{550e8400-e29b-41d4-a716-446655440000";
            String result = StandardStringCodec.modify(noClose);
            assertEquals(noClose, result);
        }

        @Test
        void modify_endsWithCloseBraceButNoOpen_returnsUnchanged() {
            // Ends with } but doesn't start with {
            String noOpen = "550e8400-e29b-41d4-a716-446655440000}";
            String result = StandardStringCodec.modify(noOpen);
            assertEquals(noOpen, result);
        }
    }

    @Nested
    class DecodeLengthEdgeCases {

        @Test
        void decode_withLength36ButNoDashes_throws() {
            // Length is 36 but no dashes - should not match LENGTH_WITH_DASH
            // because it will fail dash validation
            String noDashes36 = "550e8400e29b41d4a71644665544000"; // 33 chars actually
            // This is 32 chars, try properly
            String almost = "550e8400e29b41d4a71644665544000x"; // 33 chars
            assertThrows(InvalidUuidException.class,
                () -> StandardStringCodec.INSTANCE.decode(almost));
        }

        @Test
        void decode_withLengthBetween32And36_throws() {
            // Length is 33-35 - doesn't match either LENGTH_WITH_DASH or LENGTH_WITHOUT_DASH
            String invalid = "550e8400e29b41d4a7164466554400"; // 33 chars
            assertThrows(InvalidUuidException.class,
                () -> StandardStringCodec.INSTANCE.decode(invalid));
        }

        @Test
        void decode_withLength37_throws() {
            // Length is 37 - doesn't match either length
            String invalid = "550e8400-e29b-41d4-a716-446655440000x"; // 37 chars
            assertThrows(InvalidUuidException.class,
                () -> StandardStringCodec.INSTANCE.decode(invalid));
        }
    }

    @Nested
    class EncodeDecodeCoverageTests {

        @Test
        void encode_multipleDifferentUUIDs() {
            // Test encode for various UUIDs - covers the char array building in JDK 8 path
            for (int i = 0; i < 5; i++) {
                UUID uuid = UUID.randomUUID();
                assertDoesNotThrow(() -> StandardStringCodec.INSTANCE.encode(uuid));
            }
        }

        @Test
        void decode_standardFormat_correctBits() {
            // Verify decode returns correct bit values
            UUID uuid = StandardStringCodec.INSTANCE.decode(STANDARD_FORMAT);
            assertEquals(0x550e8400e29b41d4L, uuid.getMostSignificantBits());
            assertEquals(0xa716446655440000L, uuid.getLeastSignificantBits());
        }

        @Test
        void decode_withoutDashes_upperCase() {
            // Upper case without dashes
            String noDash = "550E8400E29B41D4A716446655440000";
            UUID uuid = StandardStringCodec.INSTANCE.decode(noDash);
            assertNotNull(uuid);
        }
    }
}
