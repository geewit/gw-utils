package io.geewit.utils.core.uuid.codec.base;

import io.geewit.utils.core.uuid.UUID;
import io.geewit.utils.core.uuid.codec.UuidCodec;
import io.geewit.utils.core.uuid.codec.base.function.BaseNDecoder;
import io.geewit.utils.core.uuid.codec.base.function.BaseNEncoder;
import io.geewit.utils.core.uuid.exception.InvalidUuidException;
import io.geewit.utils.core.uuid.util.UuidValidator;
import lombok.Getter;

import java.util.function.Function;

/**
 * Abstract class that contains the basic functionality for base-n codecs of
 * this package.
 */
public abstract class BaseNCodec implements UuidCodec<String> {

    /**
     * The base-n.
     * -- GETTER --
     *  Get the base-n encoding object.
     */
    @Getter
    protected final BaseN base;

    /**
     * An encoder function.
     */
    protected final Function<UUID, String> encoder;
    /**
     * A decoder function.
     */
    protected final Function<String, UUID> decoder;

    /**
     * @param base    an object that represents the base-n encoding
     * @param encoder a functional encoder
     * @param decoder a functional decoder
     */
    protected BaseNCodec(BaseN base,
                         BaseNEncoder encoder,
                         BaseNDecoder decoder) {
        this.base = base;
        this.encoder = encoder;
        this.decoder = decoder;
    }

    /**
     * Get an encoded string from a UUID.
     * 
     * @param uuid a UUID
     * @return an encoded string
     * @throws InvalidUuidException if the argument is invalid
     */
    @Override
    public String encode(UUID uuid) {
        try {
            UuidValidator.validate(uuid);
            return encoder.apply(uuid);
        } catch (RuntimeException e) {
            throw new InvalidUuidException(e.getMessage(), e);
        }
    }

    /**
     * Get a UUID from an encoded string.
     * 
     * @param string the encoded string
     * @return a UUID
     * @throws InvalidUuidException if the argument is invalid
     */
    @Override
    public UUID decode(String string) {
        try {
            this.validate(string);
            return decoder.apply(string);
        } catch (RuntimeException e) {
            throw new InvalidUuidException(e.getMessage(), e);
        }
    }

    protected void validate(String string) {
        if (string == null || string.length() != this.base.getLength()) {
            throw InvalidUuidException.newInstance(string);
        }
    }
}
