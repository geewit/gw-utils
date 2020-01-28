package io.geewit.core.utils.enums;


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
     * 正排序转二进制
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

}
