package io.geewit.utils.core.lang;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * String convert to Boolean
 * @author geewit
 * @since  2015-05-18
 */
@SuppressWarnings({"unused"})
public class StringUtils {
    private static final Set<String> trueValues = Stream.of("true", "on", "yes").collect(Collectors.toCollection(HashSet::new));
    private static final Set<String> falseValues = Stream.of("false", "off", "no").collect(Collectors.toCollection(HashSet::new));

    public static Boolean convert(String source) {
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
        if (noOfItems <= 0) {
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
}
