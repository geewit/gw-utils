package io.geewit.core.utils;

/**
 * @author geewit
 */
@SuppressWarnings({"unused"})
public class ArrayUtils {
    @SafeVarargs
    public static <T>boolean containsAny(T[] array, T... any) {
        if (array == null) {
            return false;
        }
        if (any == null) {
            for (T anArray : array) {
                if (anArray == null) {
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
}
