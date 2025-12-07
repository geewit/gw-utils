package io.geewit.utils.core.uuid.factory.function;

import java.util.function.IntFunction;

/**
 * Function that must return an array of bytes with the given length.
 */
@FunctionalInterface
public interface RandomFunction extends IntFunction<byte[]> {

}