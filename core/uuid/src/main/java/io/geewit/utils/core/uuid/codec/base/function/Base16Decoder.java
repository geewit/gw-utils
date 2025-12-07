package io.geewit.utils.core.uuid.codec.base.function;

import io.geewit.utils.core.uuid.UUID;
import io.geewit.utils.core.uuid.codec.base.BaseN;

/**
 * Function that decodes a base-16 string to a UUID.
 * <p>
 * It is case insensitive, so it decodes in lower case and upper case.
 * 
 * @see <a href="https://www.rfc-editor.org/rfc/rfc4648">RFC-4648</a>
 */
public final class Base16Decoder extends BaseNDecoder {

    /**
     * Constructor with a base-n.
     * 
     * @param base a base-n
     */
    public Base16Decoder(BaseN base) {
        super(base);
    }

    @Override
    public UUID apply(String string) {

        long msb = (0) | super.get(string, 0);
        msb = (msb << 4) | super.get(string, 1);
        msb = (msb << 4) | super.get(string, 2);
        msb = (msb << 4) | super.get(string, 3);
        msb = (msb << 4) | super.get(string, 4);
        msb = (msb << 4) | super.get(string, 5);
        msb = (msb << 4) | super.get(string, 6);
        msb = (msb << 4) | super.get(string, 7);
        msb = (msb << 4) | super.get(string, 8);
        msb = (msb << 4) | super.get(string, 9);
        msb = (msb << 4) | super.get(string, 10);
        msb = (msb << 4) | super.get(string, 11);
        msb = (msb << 4) | super.get(string, 12);
        msb = (msb << 4) | super.get(string, 13);
        msb = (msb << 4) | super.get(string, 14);
        msb = (msb << 4) | super.get(string, 15);

        long lsb = (0) | super.get(string, 16);
        lsb = (lsb << 4) | super.get(string, 17);
        lsb = (lsb << 4) | super.get(string, 18);
        lsb = (lsb << 4) | super.get(string, 19);
        lsb = (lsb << 4) | super.get(string, 20);
        lsb = (lsb << 4) | super.get(string, 21);
        lsb = (lsb << 4) | super.get(string, 22);
        lsb = (lsb << 4) | super.get(string, 23);
        lsb = (lsb << 4) | super.get(string, 24);
        lsb = (lsb << 4) | super.get(string, 25);
        lsb = (lsb << 4) | super.get(string, 26);
        lsb = (lsb << 4) | super.get(string, 27);
        lsb = (lsb << 4) | super.get(string, 28);
        lsb = (lsb << 4) | super.get(string, 29);
        lsb = (lsb << 4) | super.get(string, 30);
        lsb = (lsb << 4) | super.get(string, 31);

        return new UUID(msb, lsb);
    }
}