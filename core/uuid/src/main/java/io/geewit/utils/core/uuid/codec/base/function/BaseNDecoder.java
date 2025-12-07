package io.geewit.utils.core.uuid.codec.base.function;

import io.geewit.utils.core.uuid.UUID;
import io.geewit.utils.core.uuid.codec.base.BaseN;
import io.geewit.utils.core.uuid.exception.InvalidUuidException;
import io.geewit.utils.core.uuid.util.immutable.ByteArray;

import java.util.function.Function;

/**
 * Abstract function to be extended by all decoder functions of this package.
 * <p>
 * If the base-n is case insensitive, it decodes in lower case and upper case.
 */
public abstract class BaseNDecoder implements Function<String, UUID> {

    /**
     * The base-n.
     */
    protected final BaseN base;

    /**
     * The base-n map.
     */
    protected final ByteArray map;

    /**
     * @param base an enumeration that represents the base-n encoding
     */
    public BaseNDecoder(BaseN base) {
        this.base = base;
        this.map = base.getMap();
    }

    protected long get(String string, int i) {

        final int chr = string.charAt(i);
        if (chr > 255) {
            throw InvalidUuidException.newInstance(string);
        }

        final byte value = map.get(chr);
        if (value < 0) {
            throw InvalidUuidException.newInstance(string);
        }
        return value & 0xffL;
    }
}