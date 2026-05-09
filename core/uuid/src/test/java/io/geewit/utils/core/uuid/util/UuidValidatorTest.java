package io.geewit.utils.core.uuid.util;

import io.geewit.utils.core.uuid.UUID;
import io.geewit.utils.core.uuid.exception.InvalidUuidException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UuidValidatorTest {

    @Test
    void isValid_null() {
        assertFalse(UuidValidator.isValid(null));
    }

    @Test
    void isValid_empty() {
        assertFalse(UuidValidator.isValid(""));
    }

    @Test
    void isValid_withDashes() {
        assertTrue(UuidValidator.isValid("550e8400-e29b-41d4-a716-446655440000"));
    }

    @Test
    void isValid_withoutDashes() {
        assertTrue(UuidValidator.isValid("550e8400e29b41d4a716446655440000"));
    }

    @Test
    void isValid_upperCase() {
        assertTrue(UuidValidator.isValid("550E8400-E29B-41D4-A716-446655440000"));
    }

    @Test
    void isValid_invalidChar() {
        assertFalse(UuidValidator.isValid("550e8400-e29b-41d4-a716-44665544000g"));
    }

    @Test
    void isValid_wrongDashCount() {
        assertFalse(UuidValidator.isValid("550e8400-e29b-41d4-a716-44665544000-0"));
    }

    @Test
    void isValid_wrongLength() {
        assertFalse(UuidValidator.isValid("550e8400-e29b-41d4-a716-44665544000"));
    }

    @Test
    void validate_uuid_nullThrows() {
        assertThrows(InvalidUuidException.class, () -> UuidValidator.validate((UUID) null));
    }

    @Test
    void validate_uuid_nonNull() {
        assertDoesNotThrow(() -> UuidValidator.validate(UUID.randomUUID()));
    }

    @Test
    void validate_uuidWithVersion_valid() {
        UUID uuid = UUID.randomUUID(); // version 4
        assertDoesNotThrow(() -> UuidValidator.validate(uuid, 4));
    }

    @Test
    void validate_uuidWithVersion_invalid() {
        UUID uuid = UUID.randomUUID(); // version 4
        assertThrows(InvalidUuidException.class, () -> UuidValidator.validate(uuid, 1));
    }

    @Test
    void validate_bytes_nullThrows() {
        assertThrows(InvalidUuidException.class, () -> UuidValidator.validate((byte[]) null));
    }

    @Test
    void validate_bytes_wrongLength() {
        assertThrows(InvalidUuidException.class, () -> UuidValidator.validate(new byte[15]));
    }

    @Test
    void validate_bytes_valid() {
        assertDoesNotThrow(() -> UuidValidator.validate(new byte[16]));
    }

    @Test
    void validate_bytesWithVersion_valid() {
        byte[] bytes = new byte[16];
        bytes[6] = (byte) 0x40; // version 4
        bytes[8] = (byte) 0x80; // variant 2
        assertDoesNotThrow(() -> UuidValidator.validate(bytes, 4));
    }

    @Test
    void validate_bytesWithVersion_invalid() {
        byte[] bytes = new byte[16];
        assertThrows(InvalidUuidException.class, () -> UuidValidator.validate(bytes, 4));
    }

    @Test
    void validate_string_nullThrows() {
        assertThrows(InvalidUuidException.class, () -> UuidValidator.validate((String) null));
    }

    @Test
    void validate_string_invalid() {
        assertThrows(InvalidUuidException.class, () -> UuidValidator.validate("invalid"));
    }

    @Test
    void validate_string_valid() {
        assertDoesNotThrow(() -> UuidValidator.validate("550e8400-e29b-41d4-a716-446655440000"));
    }

    @Test
    void validate_stringWithVersion_valid() {
        assertDoesNotThrow(() -> UuidValidator.validate("550e8400-e29b-41d4-a716-446655440000", 4));
    }

    @Test
    void validate_stringWithVersion_invalid() {
        assertThrows(InvalidUuidException.class, () -> UuidValidator.validate("550e8400-e29b-41d4-a716-446655440000", 1));
    }

    @Test
    void validate_charArray_nullThrows() {
        assertThrows(InvalidUuidException.class, () -> UuidValidator.validate((char[]) null));
    }

    @Test
    void validate_charArray_valid() {
        assertDoesNotThrow(() -> UuidValidator.validate("550e8400-e29b-41d4-a716-446655440000".toCharArray()));
    }

    @Test
    void validate_charArrayWithVersion_valid() {
        assertDoesNotThrow(() -> UuidValidator.validate("550e8400-e29b-41d4-a716-446655440000".toCharArray(), 4));
    }

    @Test
    void validate_charArrayWithVersion_invalid() {
        assertThrows(InvalidUuidException.class, () -> UuidValidator.validate("550e8400-e29b-41d4-a716-446655440000".toCharArray(), 1));
    }
}
