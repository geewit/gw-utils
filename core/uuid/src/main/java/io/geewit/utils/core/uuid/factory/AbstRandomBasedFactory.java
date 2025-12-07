package io.geewit.utils.core.uuid.factory;

import io.geewit.utils.core.uuid.UUID;
import io.geewit.utils.core.uuid.enums.UuidVersion;
import io.geewit.utils.core.uuid.factory.function.RandomFunction;
import io.geewit.utils.core.uuid.factory.function.impl.DefaultRandomFunction;
import io.geewit.utils.core.uuid.util.internal.ByteUtil;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.IntFunction;
import java.util.function.LongSupplier;

/**
 * Abstract factory for creating random-based unique identifiers (UUIDv4).
 *
 * @see RandomFunction
 */
public abstract class AbstRandomBasedFactory extends UuidFactory {

    /**
     * The random generator.
     */
    protected final IRandom random;

    /**
     * Constructor with a version number and a builder
     *
     * @param version a version number
     * @param builder a builder
     */
    protected AbstRandomBasedFactory(UuidVersion version, Builder<?, ?> builder) {
        super(version);
        this.random = builder.getRandom();
    }

    @Override
    public UUID create(Parameters parameters) {
        return create(); // ignore parameters
    }

    /**
     * Abstract builder for creating a random-based factory.
     *
     * @param <T> factory type
     * @param <B> builder type
     */
    protected abstract static class Builder<T, B extends Builder<T, B>> {

        /**
         * A random generator.
         */
        protected IRandom random;

        /**
         * Get the random generator.
         *
         * @return a random generator
         */
        protected IRandom getRandom() {
            if (this.random == null) {
                this.random = new SafeRandom(new DefaultRandomFunction());
            }
            return this.random;
        }

        /**
         * Set the random generator with a fast algorithm.
         * Use it to replace the {@link DefaultRandomFunction} with
         * {@link ThreadLocalRandom}.
         *
         * @return the generator
         */
        public B withFastRandom() {
            this.random = new FastRandom();
            return self();
        }

        /**
         * Returns the concrete builder instance.
         *
         * @return the builder itself
         */
        protected abstract B self();

        /**
         * Finishes the factory building.
         *
         * @return the build factory
         */
        public abstract T build();
    }

    /**
     * Interface for random generator.
     */
    protected interface IRandom {

        /**
         * Return a random number.
         *
         * @return a number
         */
        long nextLong();

        /**
         * Return a random array of bytes.
         *
         * @param length the length
         * @return an array
         */
        byte[] nextBytes(int length);
    }

    /**
     * A long random generator.
     */
    protected record FastRandom(LongSupplier randomFunction) implements IRandom {

        /**
         * Default constructor.
         */
        public FastRandom() {
            this(newFastFunction());
        }

        /**
         * Constructor with a function which returns random numbers.
         *
         * @param randomFunction a function
         */
        protected FastRandom(LongSupplier randomFunction) {
            this.randomFunction = Objects.requireNonNull(randomFunction);
        }

        @Override
        public long nextLong() {
            return randomFunction.getAsLong();
        }

        @Override
        public byte[] nextBytes(int length) {

            int shift = 0;
            long random = 0;
            final byte[] bytes = new byte[length];

            for (int i = 0; i < length; i++) {
                if (shift < Byte.SIZE) {
                    shift = Long.SIZE;
                    random = randomFunction.getAsLong();
                }
                shift -= Byte.SIZE; // 56, 48, 42...
                bytes[i] = (byte) (random >>> shift);
            }

            return bytes;
        }

        /**
         * Returns a new random function.
         *
         * @return a function
         */
        private static LongSupplier newFastFunction() {
            return () -> ThreadLocalRandom.current().nextLong();
        }
    }

    /**
     * A byte random generator.
     */
    protected record SafeRandom(IntFunction<byte[]> randomFunction) implements IRandom {

        /**
         * Constructor with a function which returns random numbers.
         *
         * @param randomFunction a function
         */
        protected SafeRandom(IntFunction<byte[]> randomFunction) {
            this.randomFunction = Objects.requireNonNull(randomFunction);
        }

        @Override
        public long nextLong() {
            byte[] bytes = this.randomFunction.apply(Long.BYTES);
            return ByteUtil.toNumber(bytes);
        }

        @Override
        public byte[] nextBytes(int length) {
            return this.randomFunction.apply(length);
        }
    }
}
