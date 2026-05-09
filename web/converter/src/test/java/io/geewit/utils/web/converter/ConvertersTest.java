package io.geewit.utils.web.converter;

import io.geewit.utils.core.enums.Name;
import io.geewit.utils.core.enums.Value;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalConverter;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Comprehensive tests for all Spring Converters in the web/converter module.
 */
@DisplayName("Converter Tests")
class ConvertersTest {

    // ==================== Test Enums ====================

    enum TestStatus implements Name {
        ACTIVE("Active"),
        INACTIVE("Inactive"),
        PENDING("Pending");

        private final String name;

        TestStatus(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    enum IntPriority implements Value<Integer> {
        LOW(1),
        MEDIUM(2),
        HIGH(3);

        private final Integer value;

        IntPriority(Integer value) {
            this.value = value;
        }

        @Override
        public Integer value() {
            return value;
        }
    }

    enum LongCode implements Value<Long> {
        A(100L),
        B(200L),
        C(300L);

        private final Long value;

        LongCode(Long value) {
            this.value = value;
        }

        @Override
        public Long value() {
            return value;
        }
    }

    // Enum implementing both Name and Value<Integer>
    enum Color implements Name, Value<Integer> {
        RED(1, "Red"),
        GREEN(2, "Green"),
        BLUE(3, "Blue");

        private final Integer value;
        private final String name;

        Color(Integer value, String name) {
            this.value = value;
            this.name = name;
        }

        @Override
        public Integer value() {
            return value;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    // ==================== DateReadConverter Tests ====================

    @Nested
    @DisplayName("DateReadConverter")
    class DateReadConverterTests {

        private DateReadConverter converter;

        @BeforeEach
        void setUp() {
            converter = new DateReadConverter();
        }

        @Test
        @DisplayName("should parse yyyy-MM-dd format")
        void shouldParseDateFormat() {
            Date result = converter.convert("2024-01-15");
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("should parse yyyy-MM-dd HH:mm:ss format")
        void shouldParseDateTimeFormat() {
            Date result = converter.convert("2024-01-15 13:45:30");
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("should parse HH:mm:ss format")
        void shouldParseTimeFormat() {
            Date result = converter.convert("13:45:30");
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("should return null for invalid date string")
        void shouldReturnNullForInvalidDate() {
            Date result = converter.convert("invalid-date");
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should return null for malformed date")
        void shouldReturnNullForMalformedDate() {
            Date result = converter.convert("2024-13-45"); // invalid month/day
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should return null for wrong format")
        void shouldReturnNullForWrongFormat() {
            Date result = converter.convert("01/15/2024"); // US format, not supported
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should return null for empty string")
        void shouldReturnNullForEmptyString() {
            Date result = converter.convert("");
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should return null for partial time")
        void shouldReturnNullForPartialTime() {
            Date result = converter.convert("13:45"); // missing seconds
            assertThat(result).isNull();
        }
    }

    // ==================== EnumNameReadConverter Tests ====================

    @Nested
    @DisplayName("EnumNameReadConverter")
    class EnumNameReadConverterTests {

        private EnumNameReadConverter<TestStatus> converter;

        @BeforeEach
        void setUp() {
            converter = new EnumNameReadConverter<>();
        }

        @Test
        @DisplayName("matches() should return true when source is String and target implements Name")
        void matchesShouldReturnTrueForValidTypes() {
            TypeDescriptor sourceType = TypeDescriptor.valueOf(String.class);
            TypeDescriptor targetType = TypeDescriptor.valueOf(TestStatus.class);

            boolean result = converter.matches(sourceType, targetType);

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("matches() should return false when source is not String")
        void matchesShouldReturnFalseWhenSourceIsNotString() {
            TypeDescriptor sourceType = TypeDescriptor.valueOf(Integer.class);
            TypeDescriptor targetType = TypeDescriptor.valueOf(TestStatus.class);

            boolean result = converter.matches(sourceType, targetType);

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("matches() should return false when target does not implement Name")
        void matchesShouldReturnFalseWhenTargetDoesNotImplementName() {
            TypeDescriptor sourceType = TypeDescriptor.valueOf(String.class);
            TypeDescriptor targetType = TypeDescriptor.valueOf(IntPriority.class); // Only implements Value, not Name

            boolean result = converter.matches(sourceType, targetType);

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("convert() should return enum by name")
        void convertShouldReturnEnumByName() {
            // Set up target type via matches()
            converter.matches(TypeDescriptor.valueOf(String.class), TypeDescriptor.valueOf(TestStatus.class));

            TestStatus result = converter.convert("Active");

            assertThat(result).isEqualTo(TestStatus.ACTIVE);
        }

        @Test
        @DisplayName("convert() should return enum by enum constant name (case insensitive)")
        void convertShouldReturnEnumByConstantNameCaseInsensitive() {
            // Set up target type via matches()
            converter.matches(TypeDescriptor.valueOf(String.class), TypeDescriptor.valueOf(TestStatus.class));

            TestStatus result = converter.convert("active");

            assertThat(result).isEqualTo(TestStatus.ACTIVE);
        }

        @Test
        @DisplayName("convert() should throw IllegalArgumentException for unknown token")
        void convertShouldThrowForUnknownToken() {
            // Set up target type via matches()
            converter.matches(TypeDescriptor.valueOf(String.class), TypeDescriptor.valueOf(TestStatus.class));

            assertThatThrownBy(() -> converter.convert("UNKNOWN"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Unknown token");
        }

        @Test
        @DisplayName("implements ConditionalConverter interface")
        void implementsConditionalConverter() {
            assertThat(converter).isInstanceOf(ConditionalConverter.class);
        }
    }

    // ==================== EnumNameWriteConverter Tests ====================

    @Nested
    @DisplayName("EnumNameWriteConverter")
    class EnumNameWriteConverterTests {

        private EnumNameWriteConverter<TestStatus> converter;

        @BeforeEach
        void setUp() {
            converter = new EnumNameWriteConverter<>();
        }

        @Test
        @DisplayName("matches() should return true when source implements Name and target is String")
        void matchesShouldReturnTrueForValidTypes() {
            TypeDescriptor sourceType = TypeDescriptor.valueOf(TestStatus.class);
            TypeDescriptor targetType = TypeDescriptor.valueOf(String.class);

            boolean result = converter.matches(sourceType, targetType);

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("matches() should return false when target is not String")
        void matchesShouldReturnFalseWhenTargetIsNotString() {
            TypeDescriptor sourceType = TypeDescriptor.valueOf(TestStatus.class);
            TypeDescriptor targetType = TypeDescriptor.valueOf(Integer.class);

            boolean result = converter.matches(sourceType, targetType);

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("matches() should return false when source does not implement Name")
        void matchesShouldReturnFalseWhenSourceDoesNotImplementName() {
            TypeDescriptor sourceType = TypeDescriptor.valueOf(IntPriority.class); // Only implements Value
            TypeDescriptor targetType = TypeDescriptor.valueOf(String.class);

            boolean result = converter.matches(sourceType, targetType);

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("convert() should return name string")
        void convertShouldReturnNameString() {
            String result = converter.convert(TestStatus.ACTIVE);

            assertThat(result).isEqualTo("Active");
        }

        @Test
        @DisplayName("convert() should return name for different enum values")
        void convertShouldReturnNameForDifferentValues() {
            assertThat(converter.convert(TestStatus.INACTIVE)).isEqualTo("Inactive");
            assertThat(converter.convert(TestStatus.PENDING)).isEqualTo("Pending");
        }

        @Test
        @DisplayName("implements ConditionalConverter interface")
        void implementsConditionalConverter() {
            assertThat(converter).isInstanceOf(ConditionalConverter.class);
        }
    }

    // ==================== EnumValueReadConverter Tests ====================

    @Nested
    @DisplayName("EnumValueReadConverter")
    class EnumValueReadConverterTests {

        private EnumValueReadConverter<IntPriority, Integer> intConverter;
        private EnumValueReadConverter<LongCode, Long> longConverter;

        @BeforeEach
        void setUp() {
            intConverter = new EnumValueReadConverter<>();
            longConverter = new EnumValueReadConverter<>();
        }

        @Test
        @DisplayName("matches() should return true for int source type")
        void matchesShouldReturnTrueForIntSource() {
            TypeDescriptor sourceType = TypeDescriptor.valueOf(int.class);
            TypeDescriptor targetType = TypeDescriptor.valueOf(IntPriority.class);

            boolean result = intConverter.matches(sourceType, targetType);

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("matches() should return true for Integer source type")
        void matchesShouldReturnTrueForIntegerSource() {
            TypeDescriptor sourceType = TypeDescriptor.valueOf(Integer.class);
            TypeDescriptor targetType = TypeDescriptor.valueOf(IntPriority.class);

            boolean result = intConverter.matches(sourceType, targetType);

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("matches() should return true for long source type")
        void matchesShouldReturnTrueForLongSource() {
            TypeDescriptor sourceType = TypeDescriptor.valueOf(long.class);
            TypeDescriptor targetType = TypeDescriptor.valueOf(LongCode.class);

            boolean result = longConverter.matches(sourceType, targetType);

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("matches() should return true for Long source type")
        void matchesShouldReturnTrueForLongWrapperSource() {
            TypeDescriptor sourceType = TypeDescriptor.valueOf(Long.class);
            TypeDescriptor targetType = TypeDescriptor.valueOf(LongCode.class);

            boolean result = longConverter.matches(sourceType, targetType);

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("matches() should return true for Number source type")
        void matchesShouldReturnTrueForNumberSource() {
            TypeDescriptor sourceType = TypeDescriptor.valueOf(Number.class);
            TypeDescriptor targetType = TypeDescriptor.valueOf(IntPriority.class);

            boolean result = intConverter.matches(sourceType, targetType);

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("matches() should return false for non-numeric source type")
        void matchesShouldReturnFalseForNonNumericSource() {
            TypeDescriptor sourceType = TypeDescriptor.valueOf(String.class);
            TypeDescriptor targetType = TypeDescriptor.valueOf(IntPriority.class);

            boolean result = intConverter.matches(sourceType, targetType);

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("matches() should return false when target does not implement Value")
        void matchesShouldReturnFalseWhenTargetDoesNotImplementValue() {
            TypeDescriptor sourceType = TypeDescriptor.valueOf(Integer.class);
            TypeDescriptor targetType = TypeDescriptor.valueOf(TestStatus.class); // Only implements Name

            boolean result = intConverter.matches(sourceType, targetType);

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("convert() with Integer should return enum by value")
        void convertWithIntegerShouldReturnEnumByValue() {
            // Set up target type via matches()
            intConverter.matches(TypeDescriptor.valueOf(Integer.class), TypeDescriptor.valueOf(IntPriority.class));

            IntPriority result = intConverter.convert(2);

            assertThat(result).isEqualTo(IntPriority.MEDIUM);
        }

        @Test
        @DisplayName("convert() with Integer should return first matching enum")
        void convertWithIntegerShouldReturnFirstMatching() {
            // Set up target type via matches()
            intConverter.matches(TypeDescriptor.valueOf(Integer.class), TypeDescriptor.valueOf(IntPriority.class));

            IntPriority result = intConverter.convert(1);

            assertThat(result).isEqualTo(IntPriority.LOW);
        }

        @Test
        @DisplayName("convert() with Long should return enum by value")
        void convertWithLongShouldReturnEnumByValue() {
            // Set up target type via matches()
            longConverter.matches(TypeDescriptor.valueOf(Long.class), TypeDescriptor.valueOf(LongCode.class));

            LongCode result = longConverter.convert(200L);

            assertThat(result).isEqualTo(LongCode.B);
        }

        @Test
        @DisplayName("convert() with non-Integer/Long Number should return null")
        @SuppressWarnings("unchecked")
        void convertWithOtherNumberShouldReturnNull() {
            // Create a raw type converter to test runtime instanceof behavior
            EnumValueReadConverter rawConverter = new EnumValueReadConverter() {};
            rawConverter.matches(TypeDescriptor.valueOf(Number.class), TypeDescriptor.valueOf(IntPriority.class));

            // Double is not Integer or Long
            IntPriority result = (IntPriority) rawConverter.convert(Double.valueOf(1.0));

            assertThat(result).isNull();
        }

        @Test
        @DisplayName("convert() should throw IllegalArgumentException for unknown value")
        void convertShouldThrowForUnknownValue() {
            // Set up target type via matches()
            intConverter.matches(TypeDescriptor.valueOf(Integer.class), TypeDescriptor.valueOf(IntPriority.class));

            assertThatThrownBy(() -> intConverter.convert(99))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Unknown value");
        }

        @Test
        @DisplayName("implements ConditionalConverter interface")
        void implementsConditionalConverter() {
            assertThat(intConverter).isInstanceOf(ConditionalConverter.class);
        }
    }

    // ==================== EnumValueWriteConverter Tests ====================

    @Nested
    @DisplayName("EnumValueWriteConverter")
    class EnumValueWriteConverterTests {

        private EnumValueWriteConverter<IntPriority, Integer> intConverter;
        private EnumValueWriteConverter<LongCode, Long> longConverter;

        @BeforeEach
        void setUp() {
            intConverter = new EnumValueWriteConverter<>();
            longConverter = new EnumValueWriteConverter<>();
        }

        @Test
        @DisplayName("matches() should return true when source implements Value and target is int")
        void matchesShouldReturnTrueForIntTarget() {
            TypeDescriptor sourceType = TypeDescriptor.valueOf(IntPriority.class);
            TypeDescriptor targetType = TypeDescriptor.valueOf(int.class);

            boolean result = intConverter.matches(sourceType, targetType);

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("matches() should return true when source implements Value and target is Integer")
        void matchesShouldReturnTrueForIntegerTarget() {
            TypeDescriptor sourceType = TypeDescriptor.valueOf(IntPriority.class);
            TypeDescriptor targetType = TypeDescriptor.valueOf(Integer.class);

            boolean result = intConverter.matches(sourceType, targetType);

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("matches() should return true when source implements Value and target is long")
        void matchesShouldReturnTrueForLongTarget() {
            TypeDescriptor sourceType = TypeDescriptor.valueOf(LongCode.class);
            TypeDescriptor targetType = TypeDescriptor.valueOf(long.class);

            boolean result = longConverter.matches(sourceType, targetType);

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("matches() should return true when source implements Value and target is Long")
        void matchesShouldReturnTrueForLongWrapperTarget() {
            TypeDescriptor sourceType = TypeDescriptor.valueOf(LongCode.class);
            TypeDescriptor targetType = TypeDescriptor.valueOf(Long.class);

            boolean result = longConverter.matches(sourceType, targetType);

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("matches() should return true when source implements Value and target is Number")
        void matchesShouldReturnTrueForNumberTarget() {
            TypeDescriptor sourceType = TypeDescriptor.valueOf(IntPriority.class);
            TypeDescriptor targetType = TypeDescriptor.valueOf(Number.class);

            boolean result = intConverter.matches(sourceType, targetType);

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("matches() should return false when target is not numeric")
        void matchesShouldReturnFalseForNonNumericTarget() {
            TypeDescriptor sourceType = TypeDescriptor.valueOf(IntPriority.class);
            TypeDescriptor targetType = TypeDescriptor.valueOf(String.class);

            boolean result = intConverter.matches(sourceType, targetType);

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("matches() should return false when source does not implement Value")
        void matchesShouldReturnFalseWhenSourceDoesNotImplementValue() {
            TypeDescriptor sourceType = TypeDescriptor.valueOf(TestStatus.class); // Only implements Name
            TypeDescriptor targetType = TypeDescriptor.valueOf(Integer.class);

            boolean result = intConverter.matches(sourceType, targetType);

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("convert() should return Integer value")
        void convertShouldReturnIntegerValue() {
            Integer result = intConverter.convert(IntPriority.HIGH);

            assertThat(result).isEqualTo(3);
        }

        @Test
        @DisplayName("convert() should return value for different enum values")
        void convertShouldReturnValueForDifferentEnums() {
            assertThat(intConverter.convert(IntPriority.LOW)).isEqualTo(1);
            assertThat(intConverter.convert(IntPriority.MEDIUM)).isEqualTo(2);
        }

        @Test
        @DisplayName("convert() should return Long value")
        void convertShouldReturnLongValue() {
            Long result = longConverter.convert(LongCode.C);

            assertThat(result).isEqualTo(300L);
        }

        @Test
        @DisplayName("convert() should return value for different Long enum values")
        void convertShouldReturnLongValueForDifferentEnums() {
            assertThat(longConverter.convert(LongCode.A)).isEqualTo(100L);
            assertThat(longConverter.convert(LongCode.B)).isEqualTo(200L);
        }

        @Test
        @DisplayName("implements ConditionalConverter interface")
        void implementsConditionalConverter() {
            assertThat(intConverter).isInstanceOf(ConditionalConverter.class);
        }
    }

    // ==================== Integration-style tests ====================

    @Nested
    @DisplayName("Converter edge cases and integration")
    class EdgeCaseTests {

        @Test
        @DisplayName("EnumNameReadConverter stores targetType for convert() to use")
        void enumNameReadConverterStoresTargetType() {
            EnumNameReadConverter<TestStatus> converter = new EnumNameReadConverter<>();

            // Call matches to set targetType
            converter.matches(TypeDescriptor.valueOf(String.class), TypeDescriptor.valueOf(TestStatus.class));

            // convert() should use stored targetType
            TestStatus result = converter.convert("Pending");
            assertThat(result).isEqualTo(TestStatus.PENDING);
        }

        @Test
        @DisplayName("EnumValueReadConverter stores targetType for convert() to use")
        void enumValueReadConverterStoresTargetType() {
            EnumValueReadConverter<IntPriority, Integer> converter = new EnumValueReadConverter<>();

            // Call matches to set targetType
            converter.matches(TypeDescriptor.valueOf(Integer.class), TypeDescriptor.valueOf(IntPriority.class));

            // convert() should use stored targetType
            IntPriority result = converter.convert(3);
            assertThat(result).isEqualTo(IntPriority.HIGH);
        }

        @Test
        @DisplayName("Enum converters work with enum implementing both Name and Value")
        void enumImplementingBothNameAndValue() {
            // Test that Color enum can work with Name converters
            EnumNameReadConverter<Color> nameReadConverter = new EnumNameReadConverter<>();
            nameReadConverter.matches(TypeDescriptor.valueOf(String.class), TypeDescriptor.valueOf(Color.class));

            EnumNameWriteConverter<Color> nameWriteConverter = new EnumNameWriteConverter<>();

            Color color = nameReadConverter.convert("Red");
            assertThat(color).isEqualTo(Color.RED);
            assertThat(nameWriteConverter.convert(color)).isEqualTo("Red");

            // Test Value converters with Color
            EnumValueReadConverter<Color, Integer> valueReadConverter = new EnumValueReadConverter<>();
            valueReadConverter.matches(TypeDescriptor.valueOf(Integer.class), TypeDescriptor.valueOf(Color.class));

            EnumValueWriteConverter<Color, Integer> valueWriteConverter = new EnumValueWriteConverter<>();

            Color colorByValue = valueReadConverter.convert(2);
            assertThat(colorByValue).isEqualTo(Color.GREEN);
            assertThat(valueWriteConverter.convert(colorByValue)).isEqualTo(2);
        }
    }
}
