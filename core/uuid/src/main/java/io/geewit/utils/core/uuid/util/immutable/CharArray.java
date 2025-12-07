package io.geewit.utils.core.uuid.util.immutable;

import org.jspecify.annotations.NonNull;

import java.util.Arrays;

/**
 * Immutable array of chars.
 */
public record CharArray(char[] array) {

    public CharArray(char[] array) {
        this.array = Arrays.copyOf(array, array.length);
    }

    /**
     * Creates an instance of this class.
     *
     * @param a an array of chars.
     * @return a new instance
     */
    public static CharArray from(char[] a) {
        return new CharArray(a);
    }

    /**
     * Return the char at a position.
     *
     * @param index the position
     * @return a char
     */
    public char get(int index) {
        return array[index];
    }

    /**
     * Returns the array length.
     *
     * @return the length
     */
    public int length() {
        return this.array.length;
    }

    /**
     * Returns copy of the array.
     *
     * @return an array of chars
     */
    @Override
    public char[] array() {
        return array.clone();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(array);
        return result;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        CharArray other = (CharArray) that;
        return Arrays.equals(this.array, other.array);
    }

    @Override
    public @NonNull String toString() {
        return "CharArray [array=" + Arrays.toString(array) + "]";
    }
}
