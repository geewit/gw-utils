package io.geewit.utils.core.uuid.factory;

import io.geewit.utils.core.uuid.UUID;
import io.geewit.utils.core.uuid.enums.UuidVersion;

import java.time.Instant;
import java.util.Objects;

/**
 * Abstract factory that is base for all UUID factories.
 */
public abstract class UuidFactory {

    /**
     * Version number.
     */
    protected final UuidVersion version;

    /**
     * Version bit mask.
     */
    protected final long versionMask;

    /**
     * Default Constructor.
     * <p>
     * The version used is {@link UuidVersion#VERSION_UNKNOWN}.
     */
    public UuidFactory() {
        this.version = UuidVersion.VERSION_UNKNOWN;
        this.versionMask = (long) version.getValue() << 12;
    }

    /**
     * Constructor with a version number.
     *
     * @param version a version number
     */
    public UuidFactory(UuidVersion version) {
        this.version = version;
        this.versionMask = (long) version.getValue() << 12;
    }

    /**
     * Create a UUID
     *
     * @return a UUID
     */
    public abstract UUID create();

    /**
     * Creates a UUID using parameters.
     *
     * @param parameters parameters object
     * @return a UUID
     */
    public abstract UUID create(Parameters parameters);

    /**
     * Parameters object to be used with a {@link UuidFactory#create(Parameters)}.
     *
     * @param instant Instant to be used.
     */
    public record Parameters(Instant instant) {

        /**
         * Constructor using a builder.
         *
         * @param builder a builder
         */
        public Parameters(Builder builder) {
            this(builder.instant); // 显式委托给主构造函数
            Objects.requireNonNull(builder.instant);
        }

        /**
         * Returns a new builder.
         *
         * @return a builder
         */
        public static Builder builder() {
            return new Builder();
        }

        /**
         * Parameters builder.
         */
        public static class Builder {

            /**
             * Instant to be used.
             */
            private Instant instant;

            private Builder() {
            }

            /**
             * Use the instant provided.
             *
             * @param instant an instant
             * @return the builder
             */
            public Builder withInstant(Instant instant) {
                this.instant = instant;
                return this;
            }

            /**
             * Finishes the parameters build.
             *
             * @return the build parameters.
             */
            public Parameters build() {
                return new Parameters(this);
            }
        }
    }

    /**
     * Creates a UUID from a pair of numbers.
     * <p>
     * It applies the version and variant numbers to the resulting UUID.
     *
     * @param msb the most significant bits
     * @param lsb the least significant bits
     * @return a UUID
     */
    protected UUID toUuid(final long msb, final long lsb) {
        final long msb0 = (msb & 0xffffffffffff0fffL) | this.versionMask; // set version
        final long lsb0 = (lsb & 0x3fffffffffffffffL) | 0x8000000000000000L; // set variant
        return new UUID(msb0, lsb0);
    }
}
