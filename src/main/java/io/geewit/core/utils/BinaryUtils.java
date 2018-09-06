package io.geewit.core.utils;


import java.util.EnumSet;
import java.util.Iterator;


/**
 * 二进制工具类
 * @author geewit
 * @since  2015/5/27
 */
@SuppressWarnings({"unused", "unchecked"})
public class BinaryUtils {
    public static <E extends Enum<E>> int toBinary(E enumType) {
        int value = 0;

        if (null != enumType) {
            EnumSet<E> enumSet = EnumSet.allOf(enumType.getDeclaringClass());
            for(E e : enumSet) {
                if(e.equals(enumType)) {
                    value = 1 << e.ordinal();
                }
            }
        }
        return value;
    }

    /**
     * 正排序转二进制
     * @param enumTypes
     * @param <E>
     * @return
     */
    @SafeVarargs
    public static <E extends Enum<?>> int toBinary(E... enumTypes) {
        int value = 0;
        int bitValue = 1;
        for (int i = enumTypes.length - 1; i >= 0; i--) {
            E enumType = enumTypes[i];
            EnumSet<? extends Enum<?>> enumSet = EnumSet.allOf(enumType.getDeclaringClass());
            Iterator<? extends Enum<?>> iterator = enumSet.iterator();
            int eValue = 0;
            while (iterator.hasNext()) {
                E e = (E)iterator.next();
                if(e.equals(enumType)) {
                    eValue |= 1 << e.ordinal();
                }
            }
            value += eValue * bitValue;
            if(i > 0) {
                bitValue = bitValue << enumSet.size();
            }
        }

        return value;
    }

    /**
     * 反排序转二进制
     * @param enumTypes
     * @param <E>
     * @return
     */
    @SafeVarargs
    public static <E extends Enum<?>> int toBinaryReverse(E... enumTypes) {
        int value = 0;
        int bitValue = 1;
        for (int i = 0; i < enumTypes.length; i++) {
            E enumType = enumTypes[i];
            EnumSet<? extends Enum<?>> enumSet = EnumSet.allOf(enumType.getDeclaringClass());
            Iterator<? extends Enum<?>> iterator = enumSet.iterator();
            int eValue = 0;
            while (iterator.hasNext()) {
                E e = (E)iterator.next();
                if(e.equals(enumType)) {
                    eValue |= 1 << e.ordinal();
                }
            }
            value += eValue * bitValue;
            if(i < enumTypes.length - 1) {
                bitValue = bitValue << enumSet.size();
            }
        }

        return value;
    }
}
