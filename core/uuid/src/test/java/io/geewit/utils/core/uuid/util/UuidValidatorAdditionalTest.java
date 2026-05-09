package io.geewit.utils.core.uuid.util;

import io.geewit.utils.core.uuid.UUID;
import io.geewit.utils.core.uuid.exception.InvalidUuidException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Additional tests for UuidValidator to improve branch coverage.
 */
class UuidValidatorAdditionalTest {

    @Nested
    class IsValidBranchTests {

        @Test
        void isValid_withEmptyString_returnsFalse() {
            // Already tested null, but empty string is different branch
            assertFalse(UuidValidator.isValid(""));
        }

        @Test
        void isValid_withWhitespaceUUID_returnsFalse() {
            // Contains whitespace characters that MAP will reject
            assertFalse(UuidValidator.isValid("550e8400 e29b-41d4-a716-446655440000"));
        }

        @Test
        void isValid_withAllDashesButWrongPosition_returnsFalse() {
            // Wrong dash position
            assertFalse(UuidValidator.isValid("550e8400-e29b-41d4a-716-446655440000"));
        }

        @Test
        void isValid_withDashInWrongPlace_returnsFalse() {
            // Dashes at wrong positions
            assertFalse(UuidValidator.isValid("55-0e8400-e29b-41d4-a716-446655440000"));
        }
    }

    @Nested
    class ValidateUuidWithVersionBranchTests {

        @Test
        void validate_uuidWithVersion_wrongVariant() {
            // UUID with correct version but wrong variant should throw
            // Version 4 but variant 0
            UUID uuid = new UUID(0x4000000000000000L, 0x0000000000000000L);
            assertThrows(InvalidUuidException.class,
                () -> UuidValidator.validate(uuid, 4));
        }

        @Test
        void validate_uuidWithVersion_invalidVersion_neverMatches() {
            // Using version number outside 0-15 range in validation
            UUID uuid = UUID.randomUUID();
            // Version 0xf is valid, but let's use one that won't match
            // Actually version 15 is the highest valid version
            // Try version 0 - won't match a v4 UUID
            assertThrows(InvalidUuidException.class,
                () -> UuidValidator.validate(uuid, 0));
        }

        @Test
        void validate_uuidWithVersion_version15ButWrongVariant() {
            // Version 15 exists but variant won't match
            UUID uuid = new UUID(0xF000000000000000L, 0x0000000000000000L);
            assertThrows(InvalidUuidException.class,
                () -> UuidValidator.validate(uuid, 15));
        }
    }

    @Nested
    class ValidateBytesWithVersionBranchTests {

        @Test
        void validate_bytesWithVersion_wrongVariant() {
            // Bytes with version 4 but variant 0
            byte[] bytes = new byte[16];
            bytes[6] = (byte) 0x40; // version 4
            bytes[8] = (byte) 0x00; // variant 0 (wrong)
            assertThrows(InvalidUuidException.class,
                () -> UuidValidator.validate(bytes, 4));
        }

        @Test
        void validate_bytesWithVersion_wrongVersion() {
            // Bytes with version 1 but we validate for version 4
            byte[] bytes = new byte[16];
            bytes[6] = (byte) 0x10; // version 1
            bytes[8] = (byte) 0x80; // variant 2
            assertThrows(InvalidUuidException.class,
                () -> UuidValidator.validate(bytes, 4));
        }

        @Test
        void validate_bytesWithVersion_allZerosBytes() {
            // All zeros with version 0
            byte[] bytes = new byte[16];
            assertThrows(InvalidUuidException.class,
                () -> UuidValidator.validate(bytes, 0));
        }
    }

    @Nested
    class ValidateStringWithVersionBranchTests {

        @Test
        void validate_stringWithVersion_wrongVersion() {
            // Valid UUID string but wrong version number
            String validFormat = "550e8400-e29b-41d4-a716-446655440000";
            // This is a v4 UUID, validate for v1 should fail
            assertThrows(InvalidUuidException.class,
                () -> UuidValidator.validate(validFormat, 1));
        }

        @Test
        void validate_stringWithVersion_wrongVariant() {
            // Valid format but variant at position 19 is wrong
            // Using 'c' instead of valid variant chars 8,9,a,b,A,B
            String wrongVariant = "550e8400-e29b-41d4-c716-446655440000";
            assertThrows(InvalidUuidException.class,
                () -> UuidValidator.validate(wrongVariant, 4));
        }

        @Test
        void validate_stringWithVersion_validVariantUpperCase() {
            // Valid variant 'B' (uppercase)
            String validUpperB = "550e8400-e29b-41d4-B716-446655440000";
            assertDoesNotThrow(() -> UuidValidator.validate(validUpperB, 4));
        }
    }

    @Nested
    class IsParseableBranchTests {

        @Test
        void isParseable_withDashInInvalidPosition() {
            // Wrong position for dash - dashCount > 0 but not at correct positions
            char[] chars = "55-0e8400-e29b-41d4-a716-446655440000".toCharArray();
            assertFalse(UuidValidator.isParseable(chars));
        }

        @Test
        void isParseable_withValid36CharUUID() {
            // 36 chars with 4 dashes at correct positions
            char[] chars = "550e8400-e29b-41d4-a716-446655440000".toCharArray();
            assertTrue(UuidValidator.isParseable(chars));
        }

        @Test
        void isParseable_withValid32CharUUID_noDashes() {
            // 32 chars without dashes
            char[] chars = "550e8400e29b41d4a716446655440000".toCharArray();
            assertTrue(UuidValidator.isParseable(chars));
        }

        @Test
        void isParseable_withTooManyDashes() {
            // 36 chars but too many dashes
            char[] chars = "55-0e8400-e29b-41d4-a716-44-6655440000".toCharArray();
            assertFalse(UuidValidator.isParseable(chars));
        }
    }

    @Nested
    class IsVersionCharArrayBranchTests {

        @Test
        void isVersion_forCharArray_wrongLength_returnsFalse() {
            // isVersion(char[], int) returns false for length != 32 or 36
            char[] wrongLength = "550e8400e29b41d4a716446655440".toCharArray(); // 31 chars
            // Need to test via validate which calls isNotParseable which calls isVersion
            assertThrows(InvalidUuidException.class,
                () -> UuidValidator.validate(wrongLength, 4));
        }

        @Test
        void isVersion_forCharArray_versionOutOfRange() {
            // Version number > 15 should return false from isVersion
            char[] chars = "550e8400e29b41d4a716446655440000".toCharArray();
            // Use version 16 which is out of range
            assertThrows(InvalidUuidException.class,
                () -> UuidValidator.validate(chars, 16));
        }
    }

    @Nested
    class IsVersionByteArrayBranchTests {

        @Test
        void isVersion_forByteArray_versionOutOfRange() {
            // Version number > 15 should return false from isVersion
            // Use version 16 which is out of 0-15 range
            byte[] bytes = new byte[16];
            bytes[6] = (byte) 0x10; // version 1
            bytes[8] = (byte) 0x80;
            assertThrows(InvalidUuidException.class,
                () -> UuidValidator.validate(bytes, 16));
        }
    }

    @Nested
    class ValidateCharArrayBranchTests {

        @Test
        void validate_charArrayWithVersion_valid() {
            char[] chars = "550e8400-e29b-41d4-a716-446655440000".toCharArray();
            assertDoesNotThrow(() -> UuidValidator.validate(chars, 4));
        }

        @Test
        void validate_charArrayWithVersion_wrongVersion() {
            char[] chars = "550e8400-e29b-41d4-a716-446655440000".toCharArray();
            assertThrows(InvalidUuidException.class,
                () -> UuidValidator.validate(chars, 7));
        }

        @Test
        void validate_charArray_withInvalidChar() {
            char[] chars = "550e8400-e29b-41d4-a716-44665544000g".toCharArray();
            assertThrows(InvalidUuidException.class,
                () -> UuidValidator.validate(chars));
        }
    }
}
