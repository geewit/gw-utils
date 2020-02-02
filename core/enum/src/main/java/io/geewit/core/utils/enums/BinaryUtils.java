package io.geewit.core.utils.enums;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
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
        if(enumTypes == null || enumTypes.length == 0) {
            return 0;
        }
        return Arrays.stream(enumTypes).mapToInt(enumType -> 1 << enumType.ordinal()).reduce(0, (a, b) -> a | b);
    }

    /**
     * 打开对应枚举集合的二进制值
     *
     * @param enumSet 枚举集合
     * @param <E>     枚举
     * @return 二进制值
     */
    public static <E extends Enum<E>> int toBinary(Collection<E> enumSet) {
        if (null == enumSet) {
            return 0;
        }
        return enumSet.stream().mapToInt(enu -> 1 << enu.ordinal()).reduce(0, (a, b) -> a | b);
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


    /**
     * 打开对应枚举类所有开关的二进制值
     *
     * @param clazz 枚举类
     * @param <E>   枚举
     * @return 二进制值
     */
    public static <E extends Enum<E>> int allTrue(Class<E> clazz) {
        return EnumSet.allOf(clazz).stream().mapToInt(enu -> (1 << enu.ordinal())).reduce(0, (a, b) -> a | b);
    }



    /**
     * 检查二进制参数的枚举开关
     *
     * @param enu   枚举开关
     * @param value 二进制参数
     * @param <E>   枚举
     * @return true:开|false:关
     */
    public static <E extends Enum<E>> boolean is(E enu, int value) {
        return (1 << enu.ordinal() & value) > 0;
    }

    /**
     * 检查二进制参数的枚举开关集合是否全开
     *
     * @param enumSet 枚举开关集合
     * @param value   二进制参数
     * @param <E>     枚举
     * @return true: 全开
     */
    public static <E extends Enum<E>> boolean hasAll(Collection<E> enumSet, int value) {
        if (null != enumSet) {
            for (E enu : enumSet) {
                if ((1 << enu.ordinal() & value) == 0) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }
}