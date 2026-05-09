package io.geewit.utils.core.lang;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StringUtilsTest {

    @Test
    void convert_trueValues_returnsTrue() {
        assertEquals(Boolean.TRUE, StringUtils.convert("true"));
        assertEquals(Boolean.TRUE, StringUtils.convert("TRUE"));
        assertEquals(Boolean.TRUE, StringUtils.convert("on"));
        assertEquals(Boolean.TRUE, StringUtils.convert("ON"));
        assertEquals(Boolean.TRUE, StringUtils.convert("yes"));
        assertEquals(Boolean.TRUE, StringUtils.convert("YES"));
    }

    @Test
    void convert_falseValues_returnsFalse() {
        assertEquals(Boolean.FALSE, StringUtils.convert("false"));
        assertEquals(Boolean.FALSE, StringUtils.convert("FALSE"));
        assertEquals(Boolean.FALSE, StringUtils.convert("off"));
        assertEquals(Boolean.FALSE, StringUtils.convert("no"));
    }

    @Test
    void convert_unknownValue_returnsNull() {
        assertNull(StringUtils.convert("maybe"));
    }

    @Test
    void convert_null_returnsNull() {
        assertNull(StringUtils.convert(null));
    }

    @Test
    void convert_blank_returnsNull() {
        assertNull(StringUtils.convert(""));
        assertNull(StringUtils.convert("   "));
    }

    @Test
    void join_nullArray_returnsNull() {
        assertNull(StringUtils.join(null, ","));
    }

    @Test
    void join_emptyArray_returnsEmptyString() {
        assertEquals("", StringUtils.join(new char[0], ","));
    }

    @Test
    void join_singleElement_noSeparator() {
        assertEquals("a", StringUtils.join(new char[]{'a'}, ","));
    }

    @Test
    void join_multipleElements_withSeparator() {
        assertEquals("a,b,c", StringUtils.join(new char[]{'a', 'b', 'c'}, ","));
    }

    @Test
    void join_withNullSeparator() {
        // When separator is null, StringBuilder.append(null) appends "null"
        assertEquals("anullbnullc", StringUtils.join(new char[]{'a', 'b', 'c'}, null));
    }

    @Test
    void toStringOrDefault_nullValue_returnsDefault() {
        assertEquals("default", StringUtils.<String>toStringOrDefault(null, String::toUpperCase, "default"));
    }

    @Test
    void toStringOrDefault_mapperReturnsNull_returnsDefault() {
        assertEquals("default", StringUtils.toStringOrDefault("input", s -> null, "default"));
    }

    @Test
    void toStringOrDefault_validValue_returnsMapped() {
        assertEquals("INPUT", StringUtils.toStringOrDefault("input", String::toUpperCase, "default"));
    }
}
