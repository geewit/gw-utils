package io.geewit.core.utils;

import java.time.Duration;

/**
 * 数组工具类
 * @author geewit
 */
@SuppressWarnings({"unused"})
public class ArrayUtils {
    /**
     * <p>Checks if the value is in the given array.
     *
     * <p>The method returns {@code false} if a {@code null} array is passed in.
     *
     * @param array  the array to search through
     * @param any  the value to find
     * @return {@code true} if the array contains the object
     */
    @SafeVarargs
    public static <T>boolean containsAny(T[] array, T... any) {
        if (array == null) {
            return false;
        }
        if (any == null) {
            for (T arrayItem : array) {
                if (arrayItem == null) {
                    return true;
                }
            }
        } else {
            for (T arrayItem : array) {
                for (T anyItem : any) {
                    if (arrayItem.equals(anyItem)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static Duration parseDuration(String value) {
        value = "PT" + value.toUpperCase();
        return Duration.parse(value);
    }

//    public static void main(String[] args) {
//        Duration duration = parseDuration("10h30m");
//        System.out.println(duration);
//    }
}
