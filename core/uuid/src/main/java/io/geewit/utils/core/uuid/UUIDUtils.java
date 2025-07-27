package io.geewit.utils.core.uuid;


import org.apache.commons.codec.binary.Hex;

/**
 * 来自 JDK {@link java.util.UUID} 去掉 ‘-’ 的工具类
 *
 * @author geewit
 * @since 2015-05-18
 */
@SuppressWarnings({"unused"})
public final class UUIDUtils {
    /**
     * {@value}
     */
    public static final String UUID_ZERO = "00000000000000000000000000000000";
    private static final int UUID_LENGTH = 32;

    /**
     * Generates a random string UUID.
     *
     * @return uuid
     */
    public static String randomUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * Validate whether the given UUID.
     *
     * @param value uuid
     * @return true or false
     */
    public static boolean isUUID(String value) {
        if (null == value || value.length() != UUID_LENGTH) {
            return false;
        }
        for (int i = 0; i < UUID_LENGTH; i++) {
            char ch = value.charAt(i);
            if ((ch < '0' || ch > '9') && (ch < 'a' || ch > 'f') && (ch < 'A' || ch > 'F')) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns !isUUID()
     *
     * @param value uuid
     * @return true or false
     */
    public static boolean isNotUUID(String value) {
        return !isUUID(value);
    }

    /**
     * Converts bytes array uuid to string
     *
     * @param value uuid
     * @return string with length 36
     */
    public static String bytesToString(byte[] value) {
        if (value.length != 16) {
            throw new IllegalArgumentException("Invalid UUID bytes");
        }
        char[] chs = Hex.encodeHex(value);
        return String.valueOf(chs, 0, 7) + String.valueOf(chs, 8, 4) + String.valueOf(chs, 12, 4) + String.valueOf(chs, 16, 4) + String.valueOf(chs, 20, 16);
    }

//    public static void main(String[] args) {
//        System.out.println(randomUUID());
//    }
}
