package io.geewit.core.utils.enums;


import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

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
        int value;
        if (null != enumType) {
            value = 1 << enumType.ordinal();
        } else {
            value = 0;
        }
        return value;
    }

    /**
     * 转二进制掩码
     * @param enumTypes 枚举类型
     * @return          枚举
     */
    @SafeVarargs
    public static <E extends Enum<E>> int toBinary(E... enumTypes) {
        int value = 0;
        if(enumTypes != null && enumTypes.length > 0) {
            for (E enumType : enumTypes) {
                value |= 1 << enumType.ordinal();
            }
        }
        return value;
    }

    /**
     * 二进制掩码转枚举集合
     * @param binary 二进制掩码
     * @param clazz 枚举类型
     * @return 枚举集合
     */
    public static <E extends Enum<E>> EnumSet<E> fromBinary(int binary, Class<E> clazz) {
        return EnumSet.allOf(clazz).stream().filter(e -> ((binary & 1 << e.ordinal()) > 0)).collect(Collectors.toCollection(() -> EnumSet.noneOf(clazz)));
    }
}
