package io.geewit.utils.core.lang;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * String convert to Boolean
 * @author geewit
 * @since  2015-05-18
 */
@SuppressWarnings({"unused"})
public class StringUtils {
    private StringUtils() {
    }

    /**
     * 默认的 true 值
     */
    private static final Set<String> trueValues = Stream.of("true", "on", "yes").collect(Collectors.toCollection(HashSet::new));

    /**
     * 默认的 false 值
     */
    private static final Set<String> falseValues = Stream.of("false", "off", "no").collect(Collectors.toCollection(HashSet::new));


    /**
     * 将字符串转换为Boolean类型
     *
     * @param source 待转换的字符串
     * @return 如果字符串匹配trueValues中的值则返回Boolean.TRUE，
     *         如果字符串匹配falseValues中的值则返回Boolean.FALSE，
     *         其他情况返回null
     */
    public static Boolean convert(String source) {
        // 检查字符串是否不为空且经过处理后判断真假值
        if(org.apache.commons.lang3.StringUtils.isNotBlank(source)) {
            source = source.trim().toLowerCase();
            if(trueValues.contains(source)) {
                return Boolean.TRUE;
            } else if(falseValues.contains(source)) {
                return Boolean.FALSE;
            }
        }
        return null;
    }

    /**
     * <p>
     * Joins the elements of the provided array into a single String containing the provided list of elements.
     * </p>
     *
     * <p>
     * No delimiter is added before or after the list. Null objects or empty strings within the array are represented
     * by empty strings.
     * </p>
     *
     * <pre>
     * StringUtils.join(null, *)               = null
     * StringUtils.join([], *)                 = ""
     * StringUtils.join([null], *)             = ""
     * StringUtils.join([1, 2, 3], ';')  = "1;2;3"
     * StringUtils.join([1, 2, 3], null) = "123"
     * </pre>
     *
     * @param array
     *            the array of values to join together, may be null
     * @param separator
     *            the separator character to use
     * @return the joined String, {@code null} if null array input
     */
    public static String join(final char[] array, final String separator) {
        if (array == null) {
            return null;
        }
        final int noOfItems = array.length;
        if (noOfItems == 0) {
            return "";
        }
        final StringBuilder buf = new StringBuilder(noOfItems * 16);
        for (int i = 0; i < array.length; i++) {
            if (i > 0) {
                buf.append(separator);
            }
            buf.append(array[i]);
        }
        return buf.toString();
    }

    /**
     * 将给定值转换为字符串，如果值为null或转换结果为null则返回默认值
     *
     * @param value 要转换的值，可以为null
     * @param mapper 用于将值转换为字符串的函数，不可为null
     * @param defaultValue 当值为null或转换结果为null时返回的默认值，可以为null
     * @param <T> 值的类型
     * @return 转换后的字符串或默认值
     */
    public static <T> String toStringOrDefault(T value,
                                               Function<? super T, String> mapper,
                                               String defaultValue) {
        // 如果值为null，直接返回默认值
        if (value == null) {
            return defaultValue;
        }
        // 应用转换函数，如果结果为null则返回默认值
        return Objects.requireNonNullElse(mapper.apply(value), defaultValue);
    }
}
