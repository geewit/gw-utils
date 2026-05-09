package io.geewit.utils.core.uuid.codec;

import io.geewit.utils.core.uuid.UUID;
import io.geewit.utils.core.uuid.codec.base.Base16Codec;
import io.geewit.utils.core.uuid.exception.InvalidUuidException;
import io.geewit.utils.core.uuid.util.UuidValidator;


/**
 * Codec for UUID canonical string as defined in RFC 9562.
 * <p>
 * In the canonical textual representation, the 16 bytes of a UUID are
 * represented as 32 hexadecimal (base-16) digits, displayed in five groups
 * separated by hyphens, in the form 8-4-4-4-12 for a total of 36 characters (32
 * hexadecimal characters and 4 hyphens).
 * <p>
 * This codec decodes (parses) strings in these formats:
 * <ul>
 * <li>000000000000V0000000000000000000 (hexadecimal string)
 * <li>00000000-0000-0000-0000-000000000000 (THE canonical string)
 * <li>{00000000-0000-0000-0000-000000000000} (Microsoft string)
 * <li>urn:uuid:00000000-0000-0000-0000-000000000000 (URN string)
 * </ul>
 * <p>
 * The encoding and decoding processes can be much faster (7x) than
 * {@link UUID#toString()} and {@link UUID#fromString(String)} in JDK 8.
 * <p>
 * If you prefer a string representation without hyphens, use
 * {@link Base16Codec} instead of {@link StandardStringCodec}.
 * {@link Base16Codec} can be much faster (22x) than doing
 * <code>uuid.toString().replaceAll("-", "")</code>.
 * <p>
 * 
 * @see <a href="https://www.rfc-editor.org/rfc/rfc9562.html">RFC 9562</a>
 */
public class StandardStringCodec implements UuidCodec<String> {

    /**
     * A shared immutable instance.
     */
    public static final StandardStringCodec INSTANCE = new StandardStringCodec();

    private static final int DASH_POSITION_1 = 8;
    private static final int DASH_POSITION_2 = 13;
    private static final int DASH_POSITION_3 = 18;
    private static final int DASH_POSITION_4 = 23;

    private static final int LENGTH_WITH_DASH = 36;
    private static final int LENGTH_WITHOUT_DASH = 32;
    private static final int LENGTH_WITH_URN_PREFIX = 45;
    private static final int LENGTH_WITH_CURLY_BRACES = 38;

    private static final byte[] MAP = Base16Codec.INSTANCE.getBase().getMap().array();

    private static final String URN_PREFIX = "urn:uuid:";

    /**
     * Get a string from a UUID.
     * <p>
     * It can be much faster than {@link UUID#toString()} in JDK 8.
     * 
     * @param uuid a UUID
     * @return a UUID string
     * @throws InvalidUuidException if the argument is invalid
     */
    @Override
    public String encode(UUID uuid) {
        UuidValidator.validate(uuid);
        return uuid.toString();
    }

    /**
     * Get a UUID from a string.
     * <p>
     * It accepts strings:
     * <ul>
     * <li>With URN prefix: "urn:uuid:";
     * <li>With curly braces: '{' and '}';
     * <li>With upper or lower case;
     * <li>With or without hyphens.
     * </ul>
     * <p>
     * It can be much faster than {@link UUID#fromString(String)} in JDK 8.
     * <p>
     * It also can be twice as fast as {@link UUID#fromString(String)} in JDK 11.
     * 
     * @param string a UUID string
     * @return a UUID
     * @throws InvalidUuidException if the argument is invalid
     */
    @Override
    public UUID decode(final String string) {

        if (string == null) {
            throw InvalidUuidException.newInstance(null);
        }

        final String modified = modify(string);

        if (modified.length() == LENGTH_WITH_DASH) {
            validate(modified);
            return parse(modified);
        }

        if (modified.length() == LENGTH_WITHOUT_DASH) {
            return Base16Codec.INSTANCE.decode(modified);
        }

        throw InvalidUuidException.newInstance(modified);
    }

    private UUID parse(final String string) {

        long msb = (0) | this.get(string, 0);
        msb = (msb << 4) | this.get(string, 1);
        msb = (msb << 4) | this.get(string, 2);
        msb = (msb << 4) | this.get(string, 3);
        msb = (msb << 4) | this.get(string, 4);
        msb = (msb << 4) | this.get(string, 5);
        msb = (msb << 4) | this.get(string, 6);
        msb = (msb << 4) | this.get(string, 7);

        msb = (msb << 4) | this.get(string, 9);
        msb = (msb << 4) | this.get(string, 10);
        msb = (msb << 4) | this.get(string, 11);
        msb = (msb << 4) | this.get(string, 12);

        msb = (msb << 4) | this.get(string, 14);
        msb = (msb << 4) | this.get(string, 15);
        msb = (msb << 4) | this.get(string, 16);
        msb = (msb << 4) | this.get(string, 17);

        long lsb = (0) | this.get(string, 19);
        lsb = (lsb << 4) | this.get(string, 20);
        lsb = (lsb << 4) | this.get(string, 21);
        lsb = (lsb << 4) | this.get(string, 22);

        lsb = (lsb << 4) | this.get(string, 24);
        lsb = (lsb << 4) | this.get(string, 25);
        lsb = (lsb << 4) | this.get(string, 26);
        lsb = (lsb << 4) | this.get(string, 27);
        lsb = (lsb << 4) | this.get(string, 28);
        lsb = (lsb << 4) | this.get(string, 29);
        lsb = (lsb << 4) | this.get(string, 30);
        lsb = (lsb << 4) | this.get(string, 31);
        lsb = (lsb << 4) | this.get(string, 32);
        lsb = (lsb << 4) | this.get(string, 33);
        lsb = (lsb << 4) | this.get(string, 34);
        lsb = (lsb << 4) | this.get(string, 35);

        return new UUID(msb, lsb);
    }

    protected static String modify(final String string) {

        // UUID URN format: "urn:uuid:00000000-0000-0000-0000-000000000000"
        if (string.length() == LENGTH_WITH_URN_PREFIX && string.startsWith(URN_PREFIX)) {
            return string.substring(URN_PREFIX.length()); // Remove the URN prefix: "urn:uuid:"
        }

        // Curly braces format: "{00000000-0000-0000-0000-000000000000}"
        if (string.length() == LENGTH_WITH_CURLY_BRACES && string.startsWith("{") && string.endsWith("}")) {
            return string.substring(1, string.length() - 1); // Remove curly braces: '{' and '}'
        }

        return string;
    }

    private static void validate(final String string) {
        if (string.charAt(DASH_POSITION_1) != '-' || string.charAt(DASH_POSITION_2) != '-'
                || string.charAt(DASH_POSITION_3) != '-' || string.charAt(DASH_POSITION_4) != '-') {
            throw InvalidUuidException.newInstance(string);
        }
    }

    private long get(final String string, final int i) {

        final int chr = string.charAt(i);
        if (chr > 255) {
            throw InvalidUuidException.newInstance(string);
        }

        final byte value = MAP[chr];
        if (value < 0) {
            throw InvalidUuidException.newInstance(string);
        }

        return value & 0xffL;
    }
}
