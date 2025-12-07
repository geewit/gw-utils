package io.geewit.utils.core.uuid.util.internal;

/**
 * Utility class that contains many static methods for byte handling.
 */
public final class ByteUtil {

    private ByteUtil() {
    }

    /**
     * Get a number from a given array of bytes.
     * 
     * @param bytes a byte array
     * @return a long
     */
    public static long toNumber(final byte[] bytes) {
        return toNumber(bytes, 0, bytes.length);
    }

    /**
     * Get a number from a given array of bytes.
     * 
     * @param bytes a byte array
     * @param start first byte of the array
     * @param end   last byte of the array (exclusive)
     * @return a long
     */
    public static long toNumber(final byte[] bytes, final int start, final int end) {
        long result = 0;
        for (int i = start; i < end; i++) {
            result = (result << 8) | (bytes[i] & 0xffL);
        }
        return result;
    }
}
