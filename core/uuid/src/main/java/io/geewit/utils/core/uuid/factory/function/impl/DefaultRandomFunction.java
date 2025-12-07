package io.geewit.utils.core.uuid.factory.function.impl;

import io.geewit.utils.core.uuid.factory.function.RandomFunction;
import io.geewit.utils.core.uuid.util.internal.RandomUtil;

/**
 * Function that returns an array of bytes with the given length.
 * 
 * @see RandomFunction
 * @see RandomUtil
 */
public final class DefaultRandomFunction implements RandomFunction {

    @Override
    public byte[] apply(final int length) {
        return RandomUtil.nextBytes(length);
    }
}