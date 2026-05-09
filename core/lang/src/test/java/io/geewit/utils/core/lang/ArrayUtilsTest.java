package io.geewit.utils.core.lang;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ArrayUtilsTest {

    @Test
    void containsAny_nullArray_returnsFalse() {
        assertFalse(ArrayUtils.containsAny(null, "a"));
    }

    @Test
    void containsAny_nullSearchItems_findsNullInArray() {
        assertTrue(ArrayUtils.containsAny(new String[]{"a", null, "c"}, (String[]) null));
    }

    @Test
    void containsAny_nullSearchItems_noNullInArray() {
        assertFalse(ArrayUtils.containsAny(new String[]{"a", "b", "c"}, (String[]) null));
    }

    @Test
    void containsAny_itemFound_returnsTrue() {
        assertTrue(ArrayUtils.containsAny(new String[]{"a", "b", "c"}, "b"));
    }

    @Test
    void containsAny_itemNotFound_returnsFalse() {
        assertFalse(ArrayUtils.containsAny(new String[]{"a", "b", "c"}, "d"));
    }

    @Test
    void containsAny_multipleSearchItems_oneFound() {
        assertTrue(ArrayUtils.containsAny(new String[]{"a", "b", "c"}, "d", "b"));
    }

    @Test
    void containsAny_multipleSearchItems_noneFound() {
        assertFalse(ArrayUtils.containsAny(new String[]{"a", "b", "c"}, "d", "e"));
    }
}
