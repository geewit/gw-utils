package io.geewit.utils.core.uuid.codec;

import io.geewit.utils.core.uuid.UUID;
import io.geewit.utils.core.uuid.exception.InvalidUuidException;


/**
 * Interface to be implemented by all codecs of this package.
 * <p>
 * All implementations of this interface throw {@link InvalidUuidException} if
 * an invalid argument argument is given.
 * <p>
 * The {@link RuntimeException} cases that can be detected beforehand are
 * translated into an {@link InvalidUuidException}.
 * 
 * @param <T> the type encoded to and decoded from.
 * @see InvalidUuidException
 */
public interface UuidCodec<T> {

    /**
     * Get a generic type from a UUID.
     * 
     * @param uuid a UUID
     * @return a generic type
     * @throws InvalidUuidException if the argument is invalid
     */
    T encode(UUID uuid);

    /**
     * Get a UUID from a generic type.
     * 
     * @param type a generic type
     * @return a UUID
     * @throws InvalidUuidException if the argument is invalid
     */
    UUID decode(T type);
}