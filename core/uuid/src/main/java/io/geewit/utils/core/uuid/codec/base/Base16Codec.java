package io.geewit.utils.core.uuid.codec.base;

import io.geewit.utils.core.uuid.codec.base.function.Base16Decoder;
import io.geewit.utils.core.uuid.codec.base.function.Base16Encoder;

/**
 * Codec for base-16 as defined in RFC-4648.
 * <p>
 * It is case insensitive, so it decodes from lower and upper case, but encodes
 * to lower case only.
 * <p>
 * It can be up to 22x faster than doing
 * <code>uuid.toString().replaceAll("-", "")`</code>.
 * 
 * @see <a href="https://www.rfc-editor.org/rfc/rfc4648">RFC-4648</a>
 */
public final class Base16Codec extends BaseNCodec {

    private static final BaseN BASE_N = new BaseN("0-9a-f");

    /**
     * A shared immutable instance.
     */
    public static final Base16Codec INSTANCE = new Base16Codec();

    /**
     * Default constructor.
     */
    public Base16Codec() {
        super(BASE_N, new Base16Encoder(BASE_N), new Base16Decoder(BASE_N));
    }
}
