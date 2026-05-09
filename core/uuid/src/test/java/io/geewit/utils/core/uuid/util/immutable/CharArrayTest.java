package io.geewit.utils.core.uuid.util.immutable;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests to improve coverage for CharArray.
 */
class CharArrayTest {

    @Test
    void constructor_copiesArray() {
        char[] original = {'a', 'b', 'c'};
        CharArray charArray = new CharArray(original);

        // Modify original array
        original[0] = 'z';

        // CharArray should have a copy, not the original
        assertEquals('a', charArray.get(0));
        assertEquals('b', charArray.get(1));
        assertEquals('c', charArray.get(2));
    }

    @Test
    void from_createsInstance() {
        char[] data = {'a', 'b', 'c', 'd'};
        CharArray charArray = CharArray.from(data);

        assertNotNull(charArray);
        assertEquals(4, charArray.length());
        assertEquals('a', charArray.get(0));
    }

    @Test
    void get_returnsCorrectValue() {
        char[] data = {'A', 'B', 'C', 'D'};
        CharArray charArray = CharArray.from(data);

        assertEquals('A', charArray.get(0));
        assertEquals('B', charArray.get(1));
        assertEquals('C', charArray.get(2));
        assertEquals('D', charArray.get(3));
    }

    @Test
    void get_throwsOnInvalidIndex() {
        char[] data = {'a', 'b', 'c'};
        CharArray charArray = CharArray.from(data);

        assertThrows(ArrayIndexOutOfBoundsException.class, () -> charArray.get(-1));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> charArray.get(3));
    }

    @Test
    void length_returnsCorrectValue() {
        char[] data = {'a', 'b', 'c', 'd', 'e'};
        CharArray charArray = CharArray.from(data);
        assertEquals(5, charArray.length());

        char[] emptyData = {};
        CharArray emptyArray = CharArray.from(emptyData);
        assertEquals(0, emptyArray.length());
    }

    @Test
    void array_returnsClone() {
        char[] data = {'a', 'b', 'c'};
        CharArray charArray = CharArray.from(data);

        char[] returned = charArray.array();
        assertNotSame(data, returned);
        assertArrayEquals(data, returned);

        // Modify returned array
        returned[0] = 'z';

        // Original should be unchanged
        assertEquals('a', charArray.get(0));
    }

    @Test
    void hashCode_consistentForEqualArrays() {
        char[] data1 = {'a', 'b', 'c'};
        char[] data2 = {'a', 'b', 'c'};

        CharArray charArray1 = CharArray.from(data1);
        CharArray charArray2 = CharArray.from(data2);

        assertEquals(charArray1.hashCode(), charArray2.hashCode());
    }

    @Test
    void hashCode_differentForDifferentArrays() {
        char[] data1 = {'a', 'b', 'c'};
        char[] data2 = {'a', 'b', 'd'};

        CharArray charArray1 = CharArray.from(data1);
        CharArray charArray2 = CharArray.from(data2);

        assertNotEquals(charArray1.hashCode(), charArray2.hashCode());
    }

    @Test
    void equals_sameInstance() {
        char[] data = {'a', 'b', 'c'};
        CharArray charArray = CharArray.from(data);

        assertTrue(charArray.equals(charArray));
    }

    @Test
    void equals_nullReturnsFalse() {
        char[] data = {'a', 'b', 'c'};
        CharArray charArray = CharArray.from(data);

        assertFalse(charArray.equals(null));
    }

    @Test
    void equals_sameClassEqualContent() {
        char[] data1 = {'a', 'b', 'c'};
        char[] data2 = {'a', 'b', 'c'};

        CharArray charArray1 = CharArray.from(data1);
        CharArray charArray2 = CharArray.from(data2);

        assertTrue(charArray1.equals(charArray2));
        assertTrue(charArray2.equals(charArray1));
    }

    @Test
    void equals_differentContentReturnsFalse() {
        char[] data1 = {'a', 'b', 'c'};
        char[] data2 = {'a', 'b', 'd'};

        CharArray charArray1 = CharArray.from(data1);
        CharArray charArray2 = CharArray.from(data2);

        assertFalse(charArray1.equals(charArray2));
    }

    @Test
    void equals_differentLengthReturnsFalse() {
        char[] data1 = {'a', 'b', 'c'};
        char[] data2 = {'a', 'b'};

        CharArray charArray1 = CharArray.from(data1);
        CharArray charArray2 = CharArray.from(data2);

        assertFalse(charArray1.equals(charArray2));
    }

    @Test
    void equals_differentClassReturnsFalse() {
        char[] data = {'a', 'b', 'c'};
        CharArray charArray = CharArray.from(data);

        assertFalse(charArray.equals("not a CharArray"));
        assertFalse(charArray.equals(123));
    }

    @Test
    void toString_containsArrayContents() {
        char[] data = {'a', 'b', 'c'};
        CharArray charArray = CharArray.from(data);

        String str = charArray.toString();
        assertNotNull(str);
        assertTrue(str.contains("CharArray"));
        assertTrue(str.contains(Arrays.toString(data)));
    }

    @Test
    void equals_differentClass_sameContentReturnsFalse() {
        // Even if content looks same, different class should not be equal
        char[] data = {'a', 'b', 'c'};
        CharArray charArray = CharArray.from(data);

        Object differentClass = new Object() {
            @Override
            public boolean equals(Object o) {
                return o instanceof CharArray && ((CharArray) o).array()[0] == 'a';
            }

            @Override
            public int hashCode() {
                return 1;
            }
        };

        assertFalse(charArray.equals(differentClass));
    }

    @Test
    void emptyArray_worksCorrectly() {
        char[] empty = {};
        CharArray charArray = CharArray.from(empty);

        assertEquals(0, charArray.length());
        assertArrayEquals(new char[]{}, charArray.array());
    }
}