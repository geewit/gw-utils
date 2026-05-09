package io.geewit.utils.javafx.control;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("NumericTextField Tests")
@ExtendWith(ApplicationExtension.class)
class NumericTextFieldTest {

    @Nested
    @DisplayName("getIntValue()")
    class GetIntValueMethod {

        @Test
        @DisplayName("should return 0 for empty text")
        void shouldReturnZeroForEmptyText() {
            NumericTextField field = new NumericTextField();
            field.setText("");
            assertThat(field.getIntValue()).isZero();
        }

        @Test
        @DisplayName("should return 0 for blank text")
        void shouldReturnZeroForBlankText() {
            NumericTextField field = new NumericTextField();
            field.setText("   ");
            assertThat(field.getIntValue()).isZero();
        }

        @Test
        @DisplayName("should return 0 for null text")
        void shouldReturnZeroForNullText() {
            NumericTextField field = new NumericTextField();
            field.setText(null);
            assertThat(field.getIntValue()).isZero();
        }

        @Test
        @DisplayName("should return 0 for just minus sign")
        void shouldReturnZeroForJustMinusSign() {
            NumericTextField field = new NumericTextField();
            field.setText("-");
            assertThat(field.getIntValue()).isZero();
        }

        @Test
        @DisplayName("should return parsed value for valid integer")
        void shouldReturnParsedValueForValidInteger() {
            NumericTextField field = new NumericTextField();
            field.setText("123");
            assertThat(field.getIntValue()).isEqualTo(123);
        }
    }

    @Nested
    @DisplayName("getDoubleValue()")
    class GetDoubleValueMethod {

        @Test
        @DisplayName("should return 0.0 for empty text")
        void shouldReturnZeroForEmptyText() {
            NumericTextField field = new NumericTextField();
            field.setText("");
            assertThat(field.getDoubleValue()).isZero();
        }

        @Test
        @DisplayName("should return 0.0 for blank text")
        void shouldReturnZeroForBlankText() {
            NumericTextField field = new NumericTextField();
            field.setText("   ");
            assertThat(field.getDoubleValue()).isZero();
        }

        @Test
        @DisplayName("should return 0.0 for null text")
        void shouldReturnZeroForNullText() {
            NumericTextField field = new NumericTextField();
            field.setText(null);
            assertThat(field.getDoubleValue()).isZero();
        }

        @Test
        @DisplayName("should return 0.0 for just minus sign")
        void shouldReturnZeroForJustMinusSign() {
            NumericTextField field = new NumericTextField();
            field.setText("-");
            assertThat(field.getDoubleValue()).isZero();
        }

        @Test
        @DisplayName("should return 0.0 for just decimal point")
        void shouldReturnZeroForJustDecimalPoint() {
            NumericTextField field = new NumericTextField();
            field.setText(".");
            assertThat(field.getDoubleValue()).isZero();
        }

        @Test
        @DisplayName("should return 0.0 for minus and decimal point")
        void shouldReturnZeroForMinusAndDecimalPoint() {
            NumericTextField field = new NumericTextField();
            field.setText("-.");
            assertThat(field.getDoubleValue()).isZero();
        }

        @Test
        @DisplayName("should return parsed value for valid double")
        void shouldReturnParsedValueForValidDouble() {
            NumericTextField field = new NumericTextField(true, false);
            field.setText("123.45");
            assertThat(field.getDoubleValue()).isEqualTo(123.45);
        }
    }

    @Nested
    @DisplayName("setAllowDecimal()")
    class SetAllowDecimalMethod {

        @Test
        @DisplayName("should update allowDecimal and reconfigure formatter")
        void shouldUpdateAllowDecimalAndReconfigureFormatter() {
            NumericTextField field = new NumericTextField(false, false);
            assertThat(field.isAllowDecimal()).isFalse();

            field.setAllowDecimal(true);

            assertThat(field.isAllowDecimal()).isTrue();
        }

        @Test
        @DisplayName("should not reconfigure when value unchanged")
        void shouldNotReconfigureWhenValueUnchanged() {
            NumericTextField field = new NumericTextField(false, false);

            // This is a simple test to verify no exceptions are thrown
            field.setAllowDecimal(false);
            assertThat(field.isAllowDecimal()).isFalse();
        }
    }

    @Nested
    @DisplayName("setAllowNegative()")
    class SetAllowNegativeMethod {

        @Test
        @DisplayName("should update allowNegative and reconfigure formatter")
        void shouldUpdateAllowNegativeAndReconfigureFormatter() {
            NumericTextField field = new NumericTextField(false, false);
            assertThat(field.isAllowNegative()).isFalse();

            field.setAllowNegative(true);

            assertThat(field.isAllowNegative()).isTrue();
        }

        @Test
        @DisplayName("should not reconfigure when value unchanged")
        void shouldNotReconfigureWhenValueUnchanged() {
            NumericTextField field = new NumericTextField(false, false);

            // This is a simple test to verify no exceptions are thrown
            field.setAllowNegative(false);
            assertThat(field.isAllowNegative()).isFalse();
        }
    }

    @Nested
    @DisplayName("setMaxFractionDigits()")
    class SetMaxFractionDigitsMethod {

        @Test
        @DisplayName("should update maxFractionDigits and reconfigure formatter")
        void shouldUpdateMaxFractionDigitsAndReconfigureFormatter() {
            NumericTextField field = new NumericTextField(true, false);
            assertThat(field.getMaxFractionDigits()).isNull();

            field.setMaxFractionDigits(2);

            assertThat(field.getMaxFractionDigits()).isEqualTo(2);
        }

        @Test
        @DisplayName("should treat negative value as null")
        void shouldTreatNegativeValueAsNull() {
            NumericTextField field = new NumericTextField(true, false);
            field.setMaxFractionDigits(-1);

            assertThat(field.getMaxFractionDigits()).isNull();
        }

        @Test
        @DisplayName("should not reconfigure when value unchanged")
        void shouldNotReconfigureWhenValueUnchanged() {
            NumericTextField field = new NumericTextField(true, false);
            field.setMaxFractionDigits(2);

            field.setMaxFractionDigits(2);

            assertThat(field.getMaxFractionDigits()).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("constructor")
    class ConstructorMethod {

        @Test
        @DisplayName("should set allowDecimal and allowNegative from parameters")
        void shouldSetAllowDecimalAndAllowNegativeFromParameters() {
            NumericTextField field = new NumericTextField(true, true);
            assertThat(field.isAllowDecimal()).isTrue();
            assertThat(field.isAllowNegative()).isTrue();
        }

        @Test
        @DisplayName("should default maxFractionDigits to null")
        void shouldDefaultMaxFractionDigitsToNull() {
            NumericTextField field = new NumericTextField();
            assertThat(field.getMaxFractionDigits()).isNull();
        }
    }
}