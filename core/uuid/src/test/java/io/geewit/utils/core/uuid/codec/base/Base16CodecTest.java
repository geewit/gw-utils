package io.geewit.utils.core.uuid.codec.base;

import io.geewit.utils.core.uuid.UUID;
import io.geewit.utils.core.uuid.exception.InvalidUuidException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Base16CodecTest {

    @Test
    void encode_validUUID() {
        UUID uuid = new UUID(0x550e8400e29b41d4L, 0xa716446655440000L);
        String encoded = Base16Codec.INSTANCE.encode(uuid);
        assertNotNull(encoded);
        assertEquals(32, encoded.length());
        assertEquals("550e8400e29b41d4a716446655440000", encoded);
    }

    @Test
    void decode_lowerCase() {
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
    void decode_invalidCharThrows() {
        assertThrows(InvalidUuidException.class, () -> Base16Codec.INSTANCE.decode("550e8400e29b41d4a71644665544000g"));
    }

    @Test
    void decode_invalidLengthThrows() {
        assertThrows(InvalidUuidException.class, () -> Base16Codec.INSTANCE.decode("short"));
    }

    @Test
    void encodeDecode_roundTrip() {
        UUID original = new UUID(0x123456789abcdef0L, 0xfedcba9876543210L);
        String encoded = Base16Codec.INSTANCE.encode(original);
        UUID decoded = Base16Codec.INSTANCE.decode(encoded);
        assertEquals(original, decoded);
    }
}
