package io.geewit.utils.core.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EnumUtilsTest {

    enum TestNameEnum implements Name {
        FOO, BAR, BAZ;

        @Override
        public String getName() {
            return name().toLowerCase();
        }
    }

    enum TestValueEnum implements Value<Integer> {
        ONE(1), TWO(2), THREE(3);

        private final int value;

        TestValueEnum(int value) {
            this.value = value;
        }

        @Override
        public Integer value() {
            return value;
        }
    }

    @Test
    void forToken_byName() {
        assertEquals(TestNameEnum.FOO, EnumUtils.forToken(TestNameEnum.class, "foo"));
    }

    @Test
    void forToken_byEnumName() {
        assertEquals(TestNameEnum.FOO, EnumUtils.forToken(TestNameEnum.class, "FOO"));
    }

    @Test
    void forToken_nullToken() {
        assertNull(EnumUtils.forToken(TestNameEnum.class, null));
    }

    @Test
    void forToken_unknownThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> EnumUtils.forToken(TestNameEnum.class, "unknown"));
    }

    @Test
    void forToken_withDefault_found() {
        assertEquals(TestNameEnum.BAR, EnumUtils.forToken(TestNameEnum.class, "bar", TestNameEnum.FOO));
    }

    @Test
    void forToken_withDefault_notFound() {
        assertEquals(TestNameEnum.FOO, EnumUtils.forToken(TestNameEnum.class, "unknown", TestNameEnum.FOO));
    }

    @Test
    void forToken_withDefault_nullToken() {
        assertEquals(TestNameEnum.FOO, EnumUtils.forToken(TestNameEnum.class, null, TestNameEnum.FOO));
    }

    @Test
    void forValue_found() {
        assertEquals(TestValueEnum.TWO, EnumUtils.forValue(TestValueEnum.class, 2));
    }

    @Test
    void forValue_nullValue() {
        assertNull(EnumUtils.forValue(TestValueEnum.class, null));
    }

    @Test
    void forValue_unknownThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> EnumUtils.forValue(TestValueEnum.class, 999));
    }

    @Test
    void forValue_withDefault_found() {
        assertEquals(TestValueEnum.TWO, EnumUtils.forValue(TestValueEnum.class, 2, TestValueEnum.ONE));
    }

    @Test
    void forValue_withDefault_notFound() {
        assertEquals(TestValueEnum.ONE, EnumUtils.forValue(TestValueEnum.class, 999, TestValueEnum.ONE));
    }

    @Test
    void forValue_withDefault_nullValue() {
        assertEquals(TestValueEnum.ONE, EnumUtils.forValue(TestValueEnum.class, null, TestValueEnum.ONE));
    }
}
