package io.geewit.utils.core.uuid.codec.base.function;

import io.geewit.utils.core.uuid.UUID;
import io.geewit.utils.core.uuid.codec.base.BaseN;
import io.geewit.utils.core.uuid.util.immutable.CharArray;

import java.util.function.Function;

/**
 * Abstract function to be extended by all encoder functions of this package.
 * <p>
 * If the base-n is case insensitive, it encodes in lower case only.
 */
public abstract class BaseNEncoder implements Function<UUID, String> {

    /**
     * The base-n.
     */
    protected final BaseN base;

    /**
     * The base-n alphabet.
     */
    protected final CharArray alphabet;

    /**
     * @param base an object that represents the base-n encoding
     */
    public BaseNEncoder(BaseN base) {
        this.base = base;
        this.alphabet = base.getAlphabet();
    }

    protected char get(final long index) {
        return alphabet.get((int) index);
    }
}
