package io.geewit.core.utils.enums;

import java.util.stream.Stream;

/**
 * 枚举工具类
 * @author geewit
 */
@SuppressWarnings({"unused"})
public class EnumUtils {

    private EnumUtils() {
    }

    /**
     * 通过token 转成枚举
     * @param clazz 枚举类 class
     * @param token String 类型的参数
     * @param <E>   枚举
     * @return      枚举
     */
    public static <E extends Enum<E> & Name> E forToken(Class<E> clazz, String token) {
        if(token == null) {
            return null;
        }
        return Stream.of(clazz.getEnumConstants())
                .filter(e -> e.getName().equalsIgnoreCase(token) || e.name().equalsIgnoreCase(token))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown token '" + token + "' for enum " + clazz.getName()));
    }

    /**
     * 通过token 转成枚举
     * @param clazz 枚举类 class
     * @param token String 类型的参数
     * @param defaultEnum 默认枚举值
     * @param <E>   枚举
     * @return      枚举
     */
    public static <E extends Enum<E> & Name> E forToken(Class<E> clazz, String token, E defaultEnum) {
        if(token == null) {
            return null;
        }
        return Stream.of(clazz.getEnumConstants())
                .filter(e -> e.getName().equalsIgnoreCase(token) || e.name().equalsIgnoreCase(token))
                .findFirst()
                .orElse(defaultEnum);
    }

    /**
     * 通过token 转成枚举
     * @param clazz 枚举类 class
     * @param token Integer 类型的参数
     * @param <E>   枚举
     * @return      枚举
     */
    public static <E extends Enum<E> & Value> E forToken(Class<E> clazz, Integer token) {
        if(token == null) {
            return null;
        }
        return Stream.of(clazz.getEnumConstants())
                .filter(e -> e.value() == token)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown token '" + token + "' for enum " + clazz.getName()));
    }

    /**
     * 通过token 转成枚举
     * @param clazz 枚举类 class
     * @param token Integer 类型的参数
     * @param defaultEnum 默认枚举值
     * @param <E>   枚举
     * @return      枚举
     */
    public static <E extends Enum<E> & Value> E forToken(Class<E> clazz, Integer token, E defaultEnum) {
        if(token == null) {
            return null;
        }
        return Stream.of(clazz.getEnumConstants())
                .filter(e -> e.value() == token)
                .findFirst()
                .orElse(defaultEnum);
    }
}
