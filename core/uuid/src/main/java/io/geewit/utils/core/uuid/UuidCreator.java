package io.geewit.utils.core.uuid;

import io.geewit.utils.core.uuid.codec.StandardStringCodec;
import io.geewit.utils.core.uuid.exception.InvalidUuidException;
import io.geewit.utils.core.uuid.factory.UuidFactory;
import io.geewit.utils.core.uuid.factory.UuidFactory.Parameters;
import io.geewit.utils.core.uuid.factory.standard.TimeOrderedEpochFactory;

import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

/**
 * Facade for everything.
 * <p>
 * All UUID types can be generated from this entry point.
 */
public final class UuidCreator {

    private static final UUID UUID_NIL = new UUID(0x0000000000000000L, 0x0000000000000000L);
    private static final UUID UUID_MAX = new UUID(0xffffffffffffffffL, 0xffffffffffffffffL);

    private UuidCreator() {
    }

    /**
     * Returns a Nil UUID.
     * <p>
     * Nil UUID is a special UUID that has all 128 bits set to ZERO.
     * <p>
     * The canonical string of Nil UUID is
     * <code>00000000-0000-0000-0000-000000000000</code>.
     * 
     * @return a Nil UUID
     */
    public static UUID getNil() {
        return UUID_NIL;
    }

    /**
     * Returns a Max UUID.
     * <p>
     * Max UUID is a special UUID that has all 128 bits set to ONE.
     * <p>
     * The canonical string of Max UUID is
     * <code>FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFFF</code>.
     *
     * @return a Max UUID
     * @since 5.0.0
     * @see <a href=
     *      "https://www.ietf.org/archive/id/draft-peabody-dispatch-new-uuid-format-04.html">New
     *      UUID Formats</a>
     */
    public static UUID getMax() {
        return UUID_MAX;
    }

    /**
     * Returns a string from a UUID.
     * <p>
     * It can be much faster than {@link UUID#toString()} in JDK 8.
     * 
     * @param uuid a UUID
     * @return a UUID string
     * @throws InvalidUuidException if the argument is invalid
     */
    public static String toString(UUID uuid) {
        return StandardStringCodec.INSTANCE.encode(uuid);
    }

    /**
     * Returns a UUID from a string.
     * <p>
     * It accepts strings:
     * <ul>
     * <li>With URN prefix: "urn:uuid:";
     * <li>With curly braces: '{' and '}';
     * <li>With upper or lower case;
     * <li>With or without hyphens.
     * </ul>
     * <p>
     * It can be much faster than {@link UUID#fromString(String)} in JDK 8.
     * <p>
     * It also can be twice as fast as {@link UUID#fromString(String)} in JDK 11.
     * 
     * @param uuid a UUID string
     * @return a UUID
     * @throws InvalidUuidException if the argument is invalid
     */
    public static UUID fromString(String uuid) {
        return StandardStringCodec.INSTANCE.decode(uuid);
    }

    /**
     * Returns a time-ordered unique identifier that uses Unix Epoch (UUIDv7).
     * <p>
     * This method produces identifiers with 3 parts: time, counter and random.
     * <p>
     * The counter bits are incremented by 1 when the time repeats.
     * <p>
     * The random bits are generated with each method invocation.
     * 
     * @return a UUIDv7
     * @since 5.0.0
     * @see TimeOrderedEpochFactory
     * @see <a href=
     *      "https://www.ietf.org/archive/id/draft-peabody-dispatch-new-uuid-format-04.html">New
     *      UUID Formats</a>
     */
    public static UUID getTimeOrderedEpoch() {
        return UUID7.create();
    }

    /**
     * Returns a fast time-ordered unique identifier that uses Unix Epoch (UUIDv7).
     * <p>
     * This method produces identifiers with 3 parts: time, counter and random.
     * <p>
     * The counter bits are incremented by 1 when the time repeats.
     * <p>
     * The random bits are generated with each method invocation.
     * <p>
     * It employs {@link ThreadLocalRandom} which works very well, although not
     * cryptographically strong. It can be useful, for example, for logging.
     * <p>
     * Security-sensitive applications that require a cryptographically secure
     * pseudo-random generator should use {@link UuidCreator#getTimeOrderedEpoch()}.
     * 
     * @return a UUIDv7
     * @since 6.0.0
     * @see TimeOrderedEpochFactory
     * @see <a href=
     *      "https://www.ietf.org/archive/id/draft-peabody-dispatch-new-uuid-format-04.html">New
     *      UUID Formats</a>
     */
    public static UUID getTimeOrderedEpochFast() {
        return UUID7_FAST.create();
    }

    /**
     * Returns a time-ordered unique identifier that uses Unix Epoch (UUIDv7).
     * <p>
     * This method produces identifiers with 2 parts: time and monotonic random.
     * <p>
     * The monotonic random bits are incremented by 1 when the time repeats.
     * 
     * @return a UUIDv7
     * @since 5.0.0
     * @see TimeOrderedEpochFactory
     * @see <a href=
     *      "https://www.ietf.org/archive/id/draft-peabody-dispatch-new-uuid-format-04.html">New
     *      UUID Formats</a>
     */
    public static UUID getTimeOrderedEpochPlus1() {
        return UUID7_PLUS_1.create();
    }

    /**
     * Returns a time-ordered unique identifier that uses Unix Epoch (UUIDv7).
     * <p>
     * This method produces identifiers with 2 parts: time and monotonic random.
     * <p>
     * The monotonic random bits are incremented by a random number between 1 and
     * 2^32 when the time repeats.
     * 
     * @return a UUIDv7
     * @since 5.0.0
     * @see TimeOrderedEpochFactory
     * @see <a href=
     *      "https://www.ietf.org/archive/id/draft-peabody-dispatch-new-uuid-format-04.html">New
     *      UUID Formats</a>
     */
    public static UUID getTimeOrderedEpochPlusN() {
        return UUID7_PLUS_N.create();
    }

    /**
     * Returns a time-ordered unique identifier that uses Unix Epoch (UUIDv7) for a
     * given instant.
     * <p>
     * This method produces identifiers with 2 parts: time and secure random.
     * <p>
     * The 48 bits of the time component are filled with the bits of the given
     * instant and the other 74 bits are random.
     * <p>
     * For example, the maximum UUIDv7 for 2022-02-22 22:22:22.222 is
     * `{@code 017f2387-460e-7012-b345-6789abcdef01}`, where `{@code 017f2387-460e}`
     * is the timestamp in hexadecimal.
     * <p>
     * The random bits are generated with each method invocation.
     *
     * @param instant a given instant
     * @return a UUIDv7
     * @since 5.3.3
     */
    public static UUID getTimeOrderedEpoch(Instant instant) {
        return UUID7.create(Parameters.builder().withInstant(instant).build());
    }

    /**
     * Returns the minimum UUIDv7 for a given instant.
     * <p>
     * The 48 bits of the time component are filled with the bits of the given
     * instant and the other 74 bits are all set to ZERO.
     * <p>
     * For example, the minimum UUIDv7 for 2022-02-22 22:22:22.222 is
     * `{@code 017f2387-460e-7000-8000-000000000000}`, where `{@code 017f2387-460e}`
     * is the timestamp in hexadecimal.
     * <p>
     * It can be useful to find all records before or after a specific timestamp in
     * a table without a `{@code created_at}` field.
     * 
     * @param instant a given instant
     * @return a UUIDv7
     */
    public static UUID getTimeOrderedEpochMin(Instant instant) {
        Objects.requireNonNull(instant, "Null instant");
        final long time = instant.toEpochMilli();
        return new UUID((time << 16) | 0x7000L, 0x8000000000000000L);
    }

    /**
     * Returns the maximum UUIDv7 for a given instant.
     * <p>
     * The 48 bits of the time component are filled with the bits of the given
     * instant and the other 74 bits are all set to ONE.
     * <p>
     * For example, the maximum UUIDv7 for 2022-02-22 22:22:22.222 is
     * `{@code 017f2387-460e-7fff-bfff-ffffffffffff}`, where `{@code 017f2387-460e}`
     * is the timestamp in hexadecimal.
     * <p>
     * It can be useful to find all records before or after a specific timestamp in
     * a table without a `{@code created_at}` field.
     * 
     * @param instant a given instant
     * @return a UUIDv7
     */
    public static UUID getTimeOrderedEpochMax(Instant instant) {
        Objects.requireNonNull(instant, "Null instant");
        final long time = instant.toEpochMilli();
        return new UUID((time << 16) | 0x7fffL, 0xbfffffffffffffffL);
    }

    // ***************************************
    // Lazy holders
    // ***************************************
    private static final Proxy UUID7 = new Proxy(Proxy.UUID7);
    private static final Proxy UUID7_FAST = new Proxy(Proxy.UUID7_FAST);
    private static final Proxy UUID7_PLUS_1 = new Proxy(Proxy.UUID7_PLUS_1);
    private static final Proxy UUID7_PLUS_N = new Proxy(Proxy.UUID7_PLUS_N);

    private static class Proxy extends UuidFactory {

        private UuidFactory factory = null;
        private final Supplier<UuidFactory> supplier;
        private static final ReentrantLock lock = new ReentrantLock();

        // @formatter:off
        static final Supplier<UuidFactory> UUID7 = TimeOrderedEpochFactory::new;
        static final Supplier<UuidFactory> UUID7_FAST = () -> TimeOrderedEpochFactory.builder().withFastRandom().build();
        static final Supplier<UuidFactory> UUID7_PLUS_1 = () -> TimeOrderedEpochFactory.builder().withIncrementPlus1().build();
        static final Supplier<UuidFactory> UUID7_PLUS_N = () -> TimeOrderedEpochFactory.builder().withIncrementPlusN().build();
        // @formatter:on

        public Proxy(Supplier<UuidFactory> supplier) {
            this.supplier = supplier;
        }

        private UuidFactory get() {

            if (factory != null) {
                return factory;
            }

            lock.lock();
            try {
                if (factory == null) {
                    this.factory = supplier.get();
                }
                return this.factory;
            } finally {
                lock.unlock();
            }
        }

        @Override
        public UUID create() {
            return this.get().create();
        }

        @Override
        public UUID create(Parameters parameters) {
            return this.get().create(parameters);
        }
    }
}
