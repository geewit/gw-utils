package io.geewit.core.utils.enums;


import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 枚举开关Map和二进制之间的转换工具类
 *
 * @author geewit
 */
@SuppressWarnings({"unused"})
public class EnumMapUtils {
    /**
     * 枚举开关Map转成二进制
     *
     * @param enumMap 枚举开关Map
     * @param <E>     枚举
     * @return 二进制
     */
    public static <E extends Enum<E>> int toBinary(Map<E, Boolean> enumMap) {
        if (enumMap == null || enumMap.isEmpty()) {
            return 0;
        }
        return enumMap.keySet().stream().filter(enumMap::get).mapToInt(enu -> 1 << enu.ordinal()).reduce(0, (a, b) -> a | b);
    }

    /**
     * 二进制转成枚举开关Map
     *
     * @param clazz 枚举类
     * @param value 二进制参数
     * @param <E>   枚举
     * @return 开关Map
     */
    public static <E extends Enum<E>> Map<E, Boolean> toEnumMap(Class<E> clazz, int value) {
        return EnumSet.allOf(clazz).stream().collect(Collectors.toMap(enu -> enu, enu -> (value & (1 << enu.ordinal())) > 0, (a, b) -> b));
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
     * 新建一个枚举开关集合
     *
     * @param enu  枚举类
     * @param bool 开关
     * @param <E>  枚举
     * @return 枚举开关集合
     */
    public static <E extends Enum<E>> Map<E, Boolean> newEnumMap(E enu, boolean bool) {
        Map<E, Boolean> enumMap = new HashMap<>();
        enumMap.put(enu, bool);
        return enumMap;
    }
}
