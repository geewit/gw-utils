package io.geewit.core.utils.lang.enums;


import java.util.EnumSet;
import java.util.Iterator;


/**
 * 二进制工具类
 * @author geewit
 * @since  2015-05-27
 */
@SuppressWarnings({"unused"})
public class BinaryUtils {
    /**
     * 枚举转二进制
     * @param enumType  枚举类型
     * @return          二进制
     */
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
     * @param enumTypes 枚举类型
     * @return          枚举
     */
    @SafeVarargs
    public static <E extends Enum<E>> int toBinary(E... enumTypes) {
        int value = 0;
        int bitValue = 1;
        for (int i = enumTypes.length - 1; i >= 0; i--) {
            E enumType = enumTypes[i];
            EnumSet<E> enumSet = EnumSet.allOf(enumType.getDeclaringClass());
            Iterator<E> iterator = enumSet.iterator();
            int eValue = 0;
            while (iterator.hasNext()) {
                E e = iterator.next();
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
     * @param enumTypes 枚举类型
     * @return          枚举
     */
    @SafeVarargs
    public static <E extends Enum<E>> int toBinaryReverse(E... enumTypes) {
        int value = 0;
        int bitValue = 1;
        for (int i = 0; i < enumTypes.length; i++) {
            E enumType = enumTypes[i];
            EnumSet<E> enumSet = EnumSet.allOf(enumType.getDeclaringClass());
            Iterator<E> iterator = enumSet.iterator();
            int eValue = 0;
            while (iterator.hasNext()) {
                E e = iterator.next();
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
