package io.geewit.utils.core.uuid.codec.base;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BaseNTest {

    @Test
    void constructor_withSimpleAlphabet() {
        BaseN base = new BaseN("0123456789");
        assertEquals(10, base.getRadix());
        assertEquals(39, base.getLength()); // CEIL(128 / log2(10)) ≈ 39
        assertEquals('0', base.getPadding());
    }

    @Test
    void constructor_withHexAlphabet() {
        BaseN base = new BaseN("0123456789abcdef");
        assertEquals(16, base.getRadix());
        assertEquals(32, base.getLength()); // 128 / 4 = 32 for base-16
        assertEquals('0', base.getPadding());
    }

    @Test
    void constructor_withExpandedRange() {
        // "0-9" should expand to "0123456789"
        BaseN base = new BaseN("0-9");
        assertEquals(10, base.getRadix());
        assertEquals('0', base.getPadding());
    }

    @Test
    void constructor_withLowercaseRange() {
        // "a-z" should expand to 26 letters
        BaseN base = new BaseN("a-z");
        assertEquals(26, base.getRadix());
        assertEquals('a', base.getPadding());
    }

    @Test
    void constructor_withUppercaseRange() {
        // "A-Z" should expand to 26 letters
        BaseN base = new BaseN("A-Z");
        assertEquals(26, base.getRadix());
        assertEquals('A', base.getPadding());
    }

    @Test
    void constructor_withMixedRanges() {
        // "a-z0-9" should expand
        BaseN base = new BaseN("a-z0-9");
        assertEquals(36, base.getRadix());
        assertEquals('a', base.getPadding());
    }

    @Test
    void constructor_withAllDigits() {
        BaseN base = new BaseN("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ+/");
        assertEquals(64, base.getRadix());
        assertEquals('0', base.getPadding());
    }

    @Test
    void constructor_rejectsTooShortAlphabet() {
        assertThrows(IllegalArgumentException.class, () -> new BaseN("a"));
    }

    @Test
    void constructor_rejectsTooLongAlphabet() {
        // Alphabet with 65 characters should fail
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 65; i++) {
            sb.append((char) ('a' + (i % 26)));
        }
        assertThrows(IllegalArgumentException.class, () -> new BaseN(sb.toString()));
    }

    @Test
    void constructor_validBase2() {
        BaseN base = new BaseN("01");
        assertEquals(2, base.getRadix());
        assertEquals(128, base.getLength()); // 128 bits / 1 bit per char = 128
    }

    @Test
    void constructor_validBase64() {
        BaseN base = new BaseN("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/");
        assertEquals(64, base.getRadix());
    }

    @Test
    void getAlphabet_returnsImmutable() {
        BaseN base = new BaseN("0123456789abcdef");
        assertNotNull(base.getAlphabet());
        assertEquals(16, base.getAlphabet().length());
    }

    @Test
    void getMap_returnsImmutable() {
        BaseN base = new BaseN("0123456789abcdef");
        assertNotNull(base.getMap());
        assertEquals(256, base.getMap().length());
    }

    @Test
    void sensitiveAlphabet_caseSensitive() {
        // Mixed case alphabet should be case-sensitive
        BaseN base = new BaseN("ABCdef");
        assertEquals(6, base.getRadix());

        // The map should differentiate between upper and lower case
        // In case-sensitive mode, 'A' maps to 0 and 'a' maps to -1 (not in alphabet)
        // because "ABCdef" only contains uppercase letters
        byte mapA = base.getMap().get((int) 'A');
        byte mapa = base.getMap().get((int) 'a');
        assertEquals(0, mapA);
        assertEquals(-1, mapa); // 'a' is not in "ABCdef" alphabet
    }

    @Test
    void insensitiveAlphabet_caseInsensitive() {
        // All lowercase alphabet should be case-insensitive
        BaseN base = new BaseN("abcdef");
        assertEquals(6, base.getRadix());

        // Both 'a' and 'A' should map to the same value
        byte mapa = base.getMap().get((int) 'a');
        byte mapA = base.getMap().get((int) 'A');
        assertEquals(mapa, mapA);
        assertEquals(0, mapa); // 'a' is at index 0
    }

    @Test
    void padding_isFirstCharOfAlphabet() {
        BaseN base = new BaseN("abc");
        assertEquals('a', base.getPadding());
    }

    @Test
    void base16_expectedLength() {
        BaseN base = new BaseN("0123456789abcdef");
        // 128 bits / log2(16) = 128 / 4 = 32
        assertEquals(32, base.getLength());
    }

    @Test
    void base32_expectedLength() {
        // Standard base32 alphabet
        BaseN base = new BaseN("ABCDEFGHIJKLMNOPQRSTUVWXYZ234567");
        // 128 bits / log2(32) = 128 / 5 = 25.6 -> ceil = 26
        assertEquals(26, base.getLength());
    }

    @Test
    void base36_expectedLength() {
        BaseN base = new BaseN("0123456789abcdefghijklmnopqrstuvwxyz");
        // 128 bits / log2(36) ≈ 128 / 5.17 ≈ 24.75 -> ceil = 25
        assertEquals(25, base.getLength());
    }

    @Test
    void base2_expectedLength() {
        BaseN base = new BaseN("01");
        // 128 bits / log2(2) = 128 / 1 = 128
        assertEquals(128, base.getLength());
    }
}