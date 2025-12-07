package io.geewit.utils.core.uuid.codec.base.function;

import io.geewit.utils.core.uuid.UUID;
import io.geewit.utils.core.uuid.codec.base.BaseN;


/**
 * Function that encodes a UUID to a base-16 string.
 * <p>
 * It encodes in lower case only.
 * 
 * @see <a href="https://www.rfc-editor.org/rfc/rfc4648">RFC-4648</a>
 */
public final class Base16Encoder extends BaseNEncoder {

    private static final int CHAR_LENGTH = 32;

    /**
     * Constructor with a base-n.
     * 
     * @param base a base-n
     */
    public Base16Encoder(BaseN base) {
        super(base);
    }

    @Override
    public String apply(UUID uuid) {

        final char[] chars = new char[CHAR_LENGTH];
        final long msb = uuid.getMostSignificantBits();
        final long lsb = uuid.getLeastSignificantBits();

        chars[0x00] = super.get(msb >>> 0x3c & 0xf);
        chars[0x01] = super.get(msb >>> 0x38 & 0xf);
        chars[0x02] = super.get(msb >>> 0x34 & 0xf);
        chars[0x03] = super.get(msb >>> 0x30 & 0xf);
        chars[0x04] = super.get(msb >>> 0x2c & 0xf);
        chars[0x05] = super.get(msb >>> 0x28 & 0xf);
        chars[0x06] = super.get(msb >>> 0x24 & 0xf);
        chars[0x07] = super.get(msb >>> 0x20 & 0xf);
        chars[0x08] = super.get(msb >>> 0x1c & 0xf);
        chars[0x09] = super.get(msb >>> 0x18 & 0xf);
        chars[0x0a] = super.get(msb >>> 0x14 & 0xf);
        chars[0x0b] = super.get(msb >>> 0x10 & 0xf);
        chars[0x0c] = super.get(msb >>> 0x0c & 0xf);
        chars[0x0d] = super.get(msb >>> 0x08 & 0xf);
        chars[0x0e] = super.get(msb >>> 0x04 & 0xf);
        chars[0x0f] = super.get(msb & 0xf);

        chars[0x10] = super.get(lsb >>> 0x3c & 0xf);
        chars[0x11] = super.get(lsb >>> 0x38 & 0xf);
        chars[0x12] = super.get(lsb >>> 0x34 & 0xf);
        chars[0x13] = super.get(lsb >>> 0x30 & 0xf);
        chars[0x14] = super.get(lsb >>> 0x2c & 0xf);
        chars[0x15] = super.get(lsb >>> 0x28 & 0xf);
        chars[0x16] = super.get(lsb >>> 0x24 & 0xf);
        chars[0x17] = super.get(lsb >>> 0x20 & 0xf);
        chars[0x18] = super.get(lsb >>> 0x1c & 0xf);
        chars[0x19] = super.get(lsb >>> 0x18 & 0xf);
        chars[0x1a] = super.get(lsb >>> 0x14 & 0xf);
        chars[0x1b] = super.get(lsb >>> 0x10 & 0xf);
        chars[0x1c] = super.get(lsb >>> 0x0c & 0xf);
        chars[0x1d] = super.get(lsb >>> 0x08 & 0xf);
        chars[0x1e] = super.get(lsb >>> 0x04 & 0xf);
        chars[0x1f] = super.get(lsb & 0xf);

        return new String(chars);
    }
}
