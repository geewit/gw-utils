package io.geewit.utils.core.uuid.codec.base;

import io.geewit.utils.core.uuid.UUID;
import io.geewit.utils.core.uuid.exception.InvalidUuidException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BaseNCodecTest {

    @Test
    void encode_validUUID() {
        UUID uuid = new UUID(0x550e8400e29b41d4L, 0xa716446655440000L);
        String encoded = Base16Codec.INSTANCE.encode(uuid);
        assertNotNull(encoded);
        assertEquals(32, encoded.length());
    }

    @Test
    void encode_roundTrip() {
        UUID original = new UUID(0x123456789ABCDEF0L, 0xFEDCBA9876543210L);
        String encoded = Base16Codec.INSTANCE.encode(original);
        UUID decoded = Base16Codec.INSTANCE.decode(encoded);
        assertEquals(original, decoded);
    }

    @Test
    void encode_nullUUIDThrows() {
        assertThrows(InvalidUuidException.class, () -> Base16Codec.INSTANCE.encode(null));
    }

    @Test
    void decode_validString() {
        String str = "550e8400e29b41d4a716446655440000";
        UUID uuid = Base16Codec.INSTANCE.decode(str);
        assertNotNull(uuid);
        assertEquals(0x550e8400e29b41d4L, uuid.getMostSignificantBits());
        assertEquals(0xa716446655440000L, uuid.getLeastSignificantBits());
    }

    @Test
    void decode_upperCase() {
        String str = "550E8400E29B41D4A716446655440000";
        UUID uuid = Base16Codec.INSTANCE.decode(str);
        assertNotNull(uuid);
    }

    @Test
    void decode_lowerCase() {
        String str = "550e8400e29b41d4a716446655440000";
        UUID uuid = Base16Codec.INSTANCE.decode(str);
        assertNotNull(uuid);
    }

    @Test
    void decode_mixedCase() {
        String str = "550E8400e29b41d4A716446655440000";
        UUID uuid = Base16Codec.INSTANCE.decode(str);
        assertNotNull(uuid);
    }

    @Test
    void decode_invalidCharThrows() {
        String str = "550e8400e29b41d4a71644665544000g";
        assertThrows(InvalidUuidException.class, () -> Base16Codec.INSTANCE.decode(str));
    }

    @Test
    void decode_tooShortThrows() {
        String str = "550e8400e29b41d4";
        assertThrows(InvalidUuidException.class, () -> Base16Codec.INSTANCE.decode(str));
    }

    @Test
    void decode_tooLongThrows() {
        String str = "550e8400e29b41d4a7164466554400001";
        assertThrows(InvalidUuidException.class, () -> Base16Codec.INSTANCE.decode(str));
    }

    @Test
    void decode_nullThrows() {
        assertThrows(InvalidUuidException.class, () -> Base16Codec.INSTANCE.decode(null));
    }

    @Test
    void getBase_returnsBase16() {
        assertEquals(16, Base16Codec.INSTANCE.getBase().getRadix());
    }

    @Test
    void encode_consistentResults() {
        UUID uuid = new UUID(0x550e8400e29b41d4L, 0xa716446655440000L);
        String first = Base16Codec.INSTANCE.encode(uuid);
        String second = Base16Codec.INSTANCE.encode(uuid);
        assertEquals(first, second);
    }

    @Test
    void decode_consistentResults() {
        String str = "550e8400e29b41d4a716446655440000";
        UUID first = Base16Codec.INSTANCE.decode(str);
        UUID second = Base16Codec.INSTANCE.decode(str);
        assertEquals(first, second);
    }
}