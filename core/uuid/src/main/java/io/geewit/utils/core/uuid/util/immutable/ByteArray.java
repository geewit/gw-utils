package io.geewit.utils.core.uuid.util.immutable;

import org.jspecify.annotations.NonNull;

import java.util.Arrays;

/**
 * Immutable array of bytes.
 */
public record ByteArray(byte[] array) {

    public ByteArray(byte[] array) {
        this.array = Arrays.copyOf(array, array.length);
    }

    /**
     * Creates an instance of this class.
     *
     * @param a an array of bytes
     * @return a new instance
     */
    public static ByteArray from(byte[] a) {
        return new ByteArray(a);
    }

    /**
     * Return the byte at a position.
     *
     * @param index the position
     * @return a byte
     */
    public byte get(int index) {
        return array[index];
    }

    /**
     * Returns the array length
     *
     * @return the length
     */
    public int length() {
        return this.array.length;
    }

    /**
     * Returns copy of the array.
     *
     * @return an array of bytes
     */
    @Override
    public byte[] array() {
        return Arrays.copyOf(array, array.length);
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
        if (this.getClass() != that.getClass()) {
            return false;
        }
        ByteArray other = (ByteArray) that;
        return Arrays.equals(this.array, other.array);
    }

    @Override
    public @NonNull String toString() {
        return "ByteArray [array=" + Arrays.toString(array) + "]";
    }
}
