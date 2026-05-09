package io.geewit.utils.core.uuid.util.immutable;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests to improve coverage for ByteArray.
 */
class ByteArrayTest {

    @Test
    void constructor_copiesArray() {
        byte[] original = {1, 2, 3};
        ByteArray byteArray = new ByteArray(original);

        // Modify original array
        original[0] = 99;

        // ByteArray should have a copy, not the original
        assertEquals(1, byteArray.get(0));
        assertEquals(2, byteArray.get(1));
        assertEquals(3, byteArray.get(2));
    }

    @Test
    void from_createsInstance() {
        byte[] data = {1, 2, 3, 4};
        ByteArray byteArray = ByteArray.from(data);

        assertNotNull(byteArray);
        assertEquals(4, byteArray.length());
        assertEquals(1, byteArray.get(0));
    }

    @Test
    void get_returnsCorrectValue() {
        byte[] data = {0x12, 0x34, 0x56, 0x78};
        ByteArray byteArray = ByteArray.from(data);

        assertEquals(0x12, byteArray.get(0));
        assertEquals(0x34, byteArray.get(1));
        assertEquals(0x56, byteArray.get(2));
        assertEquals(0x78, byteArray.get(3));
    }

    @Test
    void get_throwsOnInvalidIndex() {
        byte[] data = {1, 2, 3};
        ByteArray byteArray = ByteArray.from(data);

        assertThrows(ArrayIndexOutOfBoundsException.class, () -> byteArray.get(-1));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> byteArray.get(3));
    }

    @Test
    void length_returnsCorrectValue() {
        byte[] data = {1, 2, 3, 4, 5};
        ByteArray byteArray = ByteArray.from(data);
        assertEquals(5, byteArray.length());

        byte[] emptyData = {};
        ByteArray emptyArray = ByteArray.from(emptyData);
        assertEquals(0, emptyArray.length());
    }

    @Test
    void array_returnsCopy() {
        byte[] data = {1, 2, 3};
        ByteArray byteArray = ByteArray.from(data);

        byte[] returned = byteArray.array();
        assertNotSame(data, returned);
        assertArrayEquals(data, returned);

        // Modify returned array
        returned[0] = 99;

        // Original should be unchanged
        assertEquals(1, byteArray.get(0));
    }

    @Test
    void hashCode_consistentForEqualArrays() {
        byte[] data1 = {1, 2, 3};
        byte[] data2 = {1, 2, 3};

        ByteArray byteArray1 = ByteArray.from(data1);
        ByteArray byteArray2 = ByteArray.from(data2);

        assertEquals(byteArray1.hashCode(), byteArray2.hashCode());
    }

    @Test
    void hashCode_differentForDifferentArrays() {
        byte[] data1 = {1, 2, 3};
        byte[] data2 = {1, 2, 4};

        ByteArray byteArray1 = ByteArray.from(data1);
        ByteArray byteArray2 = ByteArray.from(data2);

        assertNotEquals(byteArray1.hashCode(), byteArray2.hashCode());
    }

    @Test
    void equals_sameInstance() {
        byte[] data = {1, 2, 3};
        ByteArray byteArray = ByteArray.from(data);

        assertTrue(byteArray.equals(byteArray));
    }

    @Test
    void equals_nullReturnsFalse() {
        byte[] data = {1, 2, 3};
        ByteArray byteArray = ByteArray.from(data);

        assertFalse(byteArray.equals(null));
    }

    @Test
    void equals_sameClassEqualContent() {
        byte[] data1 = {1, 2, 3};
        byte[] data2 = {1, 2, 3};

        ByteArray byteArray1 = ByteArray.from(data1);
        ByteArray byteArray2 = ByteArray.from(data2);

        assertTrue(byteArray1.equals(byteArray2));
        assertTrue(byteArray2.equals(byteArray1));
    }

    @Test
    void equals_differentContentReturnsFalse() {
        byte[] data1 = {1, 2, 3};
        byte[] data2 = {1, 2, 4};

        ByteArray byteArray1 = ByteArray.from(data1);
        ByteArray byteArray2 = ByteArray.from(data2);

        assertFalse(byteArray1.equals(byteArray2));
    }

    @Test
    void equals_differentLengthReturnsFalse() {
        byte[] data1 = {1, 2, 3};
        byte[] data2 = {1, 2};

        ByteArray byteArray1 = ByteArray.from(data1);
        ByteArray byteArray2 = ByteArray.from(data2);

        assertFalse(byteArray1.equals(byteArray2));
    }

    @Test
    void equals_differentClassReturnsFalse() {
        byte[] data = {1, 2, 3};
        ByteArray byteArray = ByteArray.from(data);

        assertFalse(byteArray.equals("not a ByteArray"));
        assertFalse(byteArray.equals(123));
    }

    @Test
    void toString_containsArrayContents() {
        byte[] data = {1, 2, 3};
        ByteArray byteArray = ByteArray.from(data);

        String str = byteArray.toString();
        assertNotNull(str);
        assertTrue(str.contains("ByteArray"));
        assertTrue(str.contains(Arrays.toString(data)));
    }

    @Test
    void equals_differentClass_sameContentReturnsFalse() {
        // Even if content looks same, different class should not be equal
        byte[] data = {1, 2, 3};
        ByteArray byteArray = ByteArray.from(data);

        Object differentClass = new Object() {
            @Override
            public boolean equals(Object o) {
                return o instanceof ByteArray && ((ByteArray) o).array()[0] == 1;
            }

            @Override
            public int hashCode() {
                return 1;
            }
        };

        assertFalse(byteArray.equals(differentClass));
    }

    @Test
    void emptyArray_worksCorrectly() {
        byte[] empty = {};
        ByteArray byteArray = ByteArray.from(empty);

        assertEquals(0, byteArray.length());
        assertArrayEquals(new byte[]{}, byteArray.array());
    }
}