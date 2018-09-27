package io.geewit.core.utils;

import io.geewit.core.enums.Name;
import io.geewit.core.enums.Value;

import java.util.stream.Stream;

/**
 * 枚举工具类
 * @author geewit
 */
@SuppressWarnings({"unused"})
public class EnumUtils {
    /**
     * 通过token 转成枚举
     * @param clazz 枚举类 class
     * @param token String 类型的参数
     * @param <E>   枚举
     * @return
     */
    public static <E extends Enum<E> & Name> E forToken(Class<E> clazz, String token) {
        return Stream.of(clazz.getEnumConstants())
                .filter(e -> e.getName().equalsIgnoreCase(token) || e.name().equalsIgnoreCase(token))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown token '" + token + "' for enum " + clazz.getName()));
    }

    /**
     * 通过token 转成枚举
     * @param clazz 枚举类 class
     * @param token Integer 类型的参数
     * @param <E>   枚举
     * @return
     */
    public static <E extends Enum<E> & Value> E forToken(Class<E> clazz, Integer token) {
        return Stream.of(clazz.getEnumConstants())
                .filter(e -> e.value() == token)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown token '" + token + "' for enum " + clazz.getName()));
    }
}
