package io.geewit.core.utils;

import io.geewit.core.enums.Name;
import io.geewit.core.enums.Value;

import java.util.stream.Stream;

/**
 * @author geewit
 */
@SuppressWarnings({"unused"})
public class EnumUtils {
    public static <E extends Enum<E> & Name> E forToken(Class<E> clazz, String token) {
        return Stream.of(clazz.getEnumConstants())
                .filter(e -> e.getName().equalsIgnoreCase(token) || e.name().equalsIgnoreCase(token))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown token '" + token + "' for enum " + clazz.getName()));
    }

    public static <E extends Enum<E> & Value> E forToken(Class<E> clazz, Integer token) {
        return Stream.of(clazz.getEnumConstants())
                .filter(e -> e.value() == token)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown token '" + token + "' for enum " + clazz.getName()));
    }
}
