package io.geewit.utils.core.uuid.codec.base;

import io.geewit.utils.core.uuid.UUID;
import io.geewit.utils.core.uuid.codec.base.function.Base16Decoder;
import io.geewit.utils.core.uuid.exception.InvalidUuidException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests to improve coverage for BaseNDecoder.
 */
class BaseNDecoderTest {

    @Test
    void base16Decoder_decode_validLowercase() {
        Base16Decoder decoder = new Base16Decoder(Base16Codec.INSTANCE.getBase());
        UUID uuid = decoder.apply("550e8400e29b41d4a716446655440000");
        assertNotNull(uuid);
        assertEquals(0x550e8400e29b41d4L, uuid.getMostSignificantBits());
        assertEquals(0xa716446655440000L, uuid.getLeastSignificantBits());
    }

    @Test
    void base16Decoder_decode_validUppercase() {
        Base16Decoder decoder = new Base16Decoder(Base16Codec.INSTANCE.getBase());
        UUID uuid = decoder.apply("550E8400E29B41D4A716446655440000");
        assertNotNull(uuid);
    }

    @Test
    void base16Decoder_decode_validMixedCase() {
        Base16Decoder decoder = new Base16Decoder(Base16Codec.INSTANCE.getBase());
        UUID uuid = decoder.apply("550E8400e29b41d4A716446655440000");
        assertNotNull(uuid);
    }

    @Test
    void base16Decoder_decode_invalidCharThrows() {
        Base16Decoder decoder = new Base16Decoder(Base16Codec.INSTANCE.getBase());
        assertThrows(InvalidUuidException.class,
            () -> decoder.apply("550e8400e29b41d4a71644665544000g"));
    }

    @Test
    void base16Decoder_decode_tooShortThrows() {
        Base16Decoder decoder = new Base16Decoder(Base16Codec.INSTANCE.getBase());
        // Should throw StringIndexOutOfBoundsException when string is too short
        assertThrows(StringIndexOutOfBoundsException.class,
            () -> decoder.apply("550e8400e29b41d4"));
    }

    @Test
    void base16Decoder_decode_tooLongHandlesGracefully() {
        Base16Decoder decoder = new Base16Decoder(Base16Codec.INSTANCE.getBase());
        // 33 chars - Base16Decoder processes positions 0-31 and ignores the rest
        // It does NOT throw, just processes first 32 chars
        UUID uuid = decoder.apply("550e8400e29b41d4a7164466554400001"); // 33 chars
        assertNotNull(uuid);
    }

    @Test
    void base16Decoder_decode_allZeros() {
        Base16Decoder decoder = new Base16Decoder(Base16Codec.INSTANCE.getBase());
        UUID uuid = decoder.apply("00000000000000000000000000000000");
        assertNotNull(uuid);
        assertEquals(0L, uuid.getMostSignificantBits());
        assertEquals(0L, uuid.getLeastSignificantBits());
    }

    @Test
    void base16Decoder_decode_allFs() {
        Base16Decoder decoder = new Base16Decoder(Base16Codec.INSTANCE.getBase());
        UUID uuid = decoder.apply("ffffffffffffffffffffffffffffffff");
        assertNotNull(uuid);
        assertEquals(-1L, uuid.getMostSignificantBits());
        assertEquals(-1L, uuid.getLeastSignificantBits());
    }

    @Test
    void base16Decoder_decode_roundTrip() {
        Base16Decoder decoder = new Base16Decoder(Base16Codec.INSTANCE.getBase());
        UUID original = new UUID(0x123456789ABCDEF0L, 0xFEDCBA9876543210L);
        String encoded = Base16Codec.INSTANCE.encode(original);
        UUID decoded = decoder.apply(encoded);
        assertEquals(original, decoded);
    }

    @Test
    void base16Decoder_decode_multipleValidStrings() {
        Base16Decoder decoder = new Base16Decoder(Base16Codec.INSTANCE.getBase());

        String[] validStrings = {
            "00000000000000000000000000000000",
            "ffffffffffffffffffffffffffffffff",
            "550e8400e29b41d4a716446655440000",
            "ffffffffffff0fff8b3e7e7e7e7e7e7e7"
        };

        for (String str : validStrings) {
            UUID uuid = decoder.apply(str);
            assertNotNull(uuid, "Should decode: " + str);
        }
    }

    @Test
    void base16Decoder_decode_boundaryValues() {
        Base16Decoder decoder = new Base16Decoder(Base16Codec.INSTANCE.getBase());

        // Test boundary values - valid 32 char hex strings
        UUID uuid1 = decoder.apply("00000000000000010000000000000001");
        UUID uuid2 = decoder.apply("0000000000000000fffffffffffffffe");

        assertNotNull(uuid1);
        assertNotNull(uuid2);
        assertNotEquals(uuid1, uuid2);
    }

    @Test
    void base16Decoder_decode_invalidCharInMiddle() {
        Base16Decoder decoder = new Base16Decoder(Base16Codec.INSTANCE.getBase());
        // Valid hex but with invalid char at position 10
        assertThrows(InvalidUuidException.class,
            () -> decoder.apply("550e8400g9b41d4a716446655440000"));
    }

    @Test
    void base16Decoder_decode_specialCharsThrow() {
        Base16Decoder decoder = new Base16Decoder(Base16Codec.INSTANCE.getBase());
        // Characters that exceed 255 in ASCII should throw
        assertThrows(InvalidUuidException.class,
            () -> decoder.apply("550e8400e29b41d4a71644665544000ą")); // ą = 261
    }
}