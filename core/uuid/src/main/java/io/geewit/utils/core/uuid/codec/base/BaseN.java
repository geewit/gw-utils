package io.geewit.utils.core.uuid.codec.base;

import io.geewit.utils.core.uuid.util.immutable.ByteArray;
import io.geewit.utils.core.uuid.util.immutable.CharArray;
import lombok.Getter;

import java.util.Arrays;

/**
 * Class that represents the base-n encodings.
 */
@Getter
public final class BaseN {

    /**
     * -- GETTER --
     *  Returns the radix of the base-n.
     *
     */
    private final int radix;
    private final int length;
    private final char padding;
    private final CharArray alphabet;
    private final ByteArray map;

    /**
     * The minimum radix: 2.
     */
    private static final int RADIX_MIN = 2;
    /**
     * The maximum radix: 64.
     */
    private static final int RADIX_MAX = 64;

    private static final int UUID_BITS = 128;

    /**
     * Public constructor for the base-n object.
     * <p>
     * The radix is the alphabet size.
     * <p>
     * The supported alphabet sizes are from 2 to 64.
     * <p>
     * If there are mixed cases in the alphabet, the base-n is case SENSITIVE.
     * <p>
     * The encoded string length is equal to `CEIL(128 / LOG2(n))`, where n is the
     * radix. The encoded string is padded to fit the expected length.
     * <p>
     * The padding character is the first character of the string. For example, the
     * padding character for the alphabet "abcdef0123456" is 'a'.
     * <p>
     * The example below shows how to create a {@link BaseN} for an hypothetical
     * base-26 encoding that contains only letters. You only need to pass a string
     * with 26 characters.
     * 
     * <pre>{@code
     * String alphabet = "abcdefghijklmnopqrstuvwxyz";
     * BaseN base = new BaseN(alphabet);
     * }</pre>
     * 
     * Alphabet strings similar to "a-f0-9" are expanded to "abcdef0123456789". The
     * same example using the string "a-z" instead of "abcdefghijklmnopqrstuvwxyz":
     * 
     * <pre>{@code
     * String alphabet = "a-z";
     * BaseN base = new BaseN(alphabet);
     * }</pre>
     * 
     * @param alphabet the alphabet to be used
     */
    public BaseN(String alphabet) {

        // expand the alphabet, if necessary
        String charset = alphabet.indexOf('-') >= 0 ? expand(alphabet) : alphabet;

        // check the alphabet length
        if (charset.length() < RADIX_MIN || charset.length() > RADIX_MAX) {
            throw new IllegalArgumentException("Unsupported length: " + charset.length());
        }

        // set the radix field
        this.radix = charset.length();

        // set the length field
        this.length = (int) Math.ceil(UUID_BITS / (Math.log(this.radix) / Math.log(2)));

        // set the padding field
        this.padding = charset.charAt(0);

        // set the sensitive field
        boolean sensitive = sensitive(charset);

        // set the alphabet field
        this.alphabet = CharArray.from(charset.toCharArray());

        // set the map field
        this.map = map(charset, sensitive);
    }

    private static boolean sensitive(String charset) {
        String lowercase = charset.toLowerCase();
        String uppercase = charset.toUpperCase();
        return !(charset.equals(lowercase) || charset.equals(uppercase));
    }

    private static ByteArray map(String alphabet, boolean sensitive) {
        
        // initialize the map with -1
        byte[] mapping = new byte[256];
        Arrays.fill(mapping, (byte) -1);
        
        // map the alphabets chars to values
        for (int i = 0; i < alphabet.length(); i++) {
            if (sensitive) {
                mapping[alphabet.charAt(i)] = (byte) i;
            } else {
                mapping[alphabet.toLowerCase().charAt(i)] = (byte) i;
                mapping[alphabet.toUpperCase().charAt(i)] = (byte) i;
            }
        }
        
        return ByteArray.from(mapping);
    }

    /**
     * Expands character sequences similar to 0-9, a-z and A-Z.
     * 
     * @param string a string to be expanded
     * @return a string
     */
    private static String expand(String string) {

        StringBuilder buffer = new StringBuilder();

        int i = 1;
        while (i <= string.length()) {
            final char a = string.charAt(i - 1); // previous char
            if ((i < string.length() - 1) && (string.charAt(i) == '-')) {
                final char b = string.charAt(i + 1); // next char
                char[] expanded = expand(a, b);
                if (expanded.length != 0) {
                    i += 2; // skip
                    buffer.append(expanded);
                } else {
                    buffer.append(a);
                }
            } else {
                buffer.append(a);
            }
            i++;
        }

        return buffer.toString();
    }

    /**
     * Expands a character sequence similar to 0-9, a-z and A-Z.
     * 
     * @param a the first character of the sequence
     * @param b the last character of the sequence
     * @return an expanded sequence of characters
     */
    private static char[] expand(char a, char b) {
        char[] expanded = expand(a, b, '0', '9'); // digits (0-9)
        if (expanded.length == 0) {
            expanded = expand(a, b, 'a', 'z'); // lower case letters (a-z)
        }
        if (expanded.length == 0) {
            expanded = expand(a, b, 'A', 'Z'); // upper case letters (A-Z)
        }
        return expanded;
    }

    private static char[] expand(char a, char b, char min, char max) {
        if (!isValidRange(a, b, min, max)) {
            return new char[0];
        }

        return fillRange(a, b);
    }

    private static boolean isValidRange(char start, char end, char min, char max) {
        return start <= end && start >= min && end <= max;
    }

    private static char[] fillRange(char start, char end) {
        char[] buffer = new char[(end - start) + 1];
        for (int i = 0; i < buffer.length; i++) {
            buffer[i] = (char) (start + i);
        }
        return buffer;
    }
}