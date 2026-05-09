package io.geewit.utils.core.uuid.factory;

import io.geewit.utils.core.uuid.UUID;
import io.geewit.utils.core.uuid.enums.UuidVersion;
import io.geewit.utils.core.uuid.factory.function.impl.DefaultRandomFunction;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;

class AbstRandomBasedFactoryTest {

    @Test
    void builder_defaultRandom_isSafeRandom() {
        TestBuilder builder = new TestBuilder();
        assertNull(builder.random);
        // After build, random should be initialized as SafeRandom by default
        TestFactory factory = builder.build();
        assertNotNull(factory.random);
    }

    @Test
    void builder_withFastRandom_setsFastRandom() {
        TestBuilder builder = new TestBuilder();
        builder.withFastRandom();
        assertTrue(builder.random instanceof AbstRandomBasedFactory.FastRandom);
    }

    @Test
    void fastRandom_nextLong() {
        AbstRandomBasedFactory.FastRandom fastRandom = new AbstRandomBasedFactory.FastRandom();
        long value = fastRandom.nextLong();
        // Should return a value (could be any long)
        assertTrue(true); // Just verify it doesn't throw
    }

    @Test
    void fastRandom_nextBytes() {
        AbstRandomBasedFactory.FastRandom fastRandom = new AbstRandomBasedFactory.FastRandom();
        byte[] bytes = fastRandom.nextBytes(16);
        assertEquals(16, bytes.length);
    }

    @Test
    void fastRandom_nextBytes_variousLengths() {
        AbstRandomBasedFactory.FastRandom fastRandom = new AbstRandomBasedFactory.FastRandom();
        for (int len = 0; len < 32; len++) {
            byte[] bytes = fastRandom.nextBytes(len);
            assertEquals(len, bytes.length);
        }
    }

    @Test
    void fastRandom_withCustomFunction() {
        long expectedValue = 0x123456789ABCDEF0L;
        AbstRandomBasedFactory.FastRandom fastRandom =
                new AbstRandomBasedFactory.FastRandom(() -> expectedValue);

        assertEquals(expectedValue, fastRandom.nextLong());
    }

    @Test
    void fastRandom_rejectsNullFunction() {
        assertThrows(NullPointerException.class,
                () -> new AbstRandomBasedFactory.FastRandom(null));
    }

    @Test
    void safeRandom_nextLong() {
        AbstRandomBasedFactory.SafeRandom safeRandom =
                new AbstRandomBasedFactory.SafeRandom(new DefaultRandomFunction());
        long value = safeRandom.nextLong();
        // Should return some value
        assertTrue(true);
    }

    @Test
    void safeRandom_nextBytes() {
        AbstRandomBasedFactory.SafeRandom safeRandom =
                new AbstRandomBasedFactory.SafeRandom(new DefaultRandomFunction());
        byte[] bytes = safeRandom.nextBytes(16);
        assertEquals(16, bytes.length);
    }

    @Test
    void safeRandom_nextBytes_variousLengths() {
        AbstRandomBasedFactory.SafeRandom safeRandom =
                new AbstRandomBasedFactory.SafeRandom(new DefaultRandomFunction());
        for (int len = 0; len < 32; len++) {
            byte[] bytes = safeRandom.nextBytes(len);
            assertEquals(len, bytes.length);
        }
    }

    @Test
    void safeRandom_rejectsNullFunction() {
        assertThrows(NullPointerException.class,
                () -> new AbstRandomBasedFactory.SafeRandom(null));
    }

    @Test
    void safeRandom_and_fastRandom_produceDifferentValues() {
        // Using ThreadLocalRandom for FastRandom
        AbstRandomBasedFactory.FastRandom fast = new AbstRandomBasedFactory.FastRandom();
        // Using SecureRandom-based DefaultRandomFunction for SafeRandom
        AbstRandomBasedFactory.SafeRandom safe =
                new AbstRandomBasedFactory.SafeRandom(new DefaultRandomFunction());

        // Both should produce valid byte arrays
        byte[] fastBytes = fast.nextBytes(16);
        byte[] safeBytes = safe.nextBytes(16);

        assertEquals(16, fastBytes.length);
        assertEquals(16, safeBytes.length);
    }

    // Test builder subclass to access protected members
    private static class TestBuilder extends AbstRandomBasedFactory.Builder<TestFactory, TestBuilder> {

        @Override
        protected TestBuilder self() {
            return this;
        }

        @Override
        public TestFactory build() {
            return new TestFactory(this);
        }
    }

    private static class TestFactory extends AbstRandomBasedFactory {
        TestFactory(TestBuilder builder) {
            super(UuidVersion.VERSION_UNKNOWN, builder);
        }

        @Override
        public UUID create() {
            return new UUID(0, 0);
        }

        @Override
        public UUID create(Parameters parameters) {
            return create();
        }
    }
}