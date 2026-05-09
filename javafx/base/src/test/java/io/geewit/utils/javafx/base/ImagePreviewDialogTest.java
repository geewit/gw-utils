package io.geewit.utils.javafx.base;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("ImagePreviewDialog Tests")
public class ImagePreviewDialogTest {

    @Nested
    @DisplayName("show() with byte array")
    class ShowWithByteArray {

        @Test
        @DisplayName("should do nothing when image data is null")
        void shouldDoNothingWhenImageDataIsNull() {
            // Returns early when imageData is null
            assertThatNoException().isThrownBy(() ->
                    ImagePreviewDialog.show(null, (byte[]) null, "title"));
        }

        @Test
        @DisplayName("should handle empty image data")
        void shouldHandleEmptyImageData() {
            byte[] imageData = new byte[]{};

            assertThatNoException().isThrownBy(() ->
                    ImagePreviewDialog.show(null, imageData, "title"));
        }

        @Test
        @DisplayName("should handle null title")
        void shouldHandleNullTitle() {
            byte[] imageData = new byte[]{};

            assertThatNoException().isThrownBy(() ->
                    ImagePreviewDialog.show(null, imageData, null));
        }

        @Test
        @DisplayName("should handle blank title")
        void shouldHandleBlankTitle() {
            byte[] imageData = new byte[]{};

            assertThatNoException().isThrownBy(() ->
                    ImagePreviewDialog.show(null, imageData, "   "));
        }
    }

    @Nested
    @DisplayName("show() with Image")
    class ShowWithImage {

        @Test
        @DisplayName("should reject null image")
        void shouldRejectNullImage() {
            assertThatThrownBy(() -> ImagePreviewDialog.show(null, (javafx.scene.image.Image) null, "title"))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("image must not be null");
        }
    }

    @Nested
    @DisplayName("clamp()")
    class ClampMethod {

        @Test
        @DisplayName("should return min when value less than min")
        void shouldReturnMinWhenValueLessThanMin() {
            double result = invokeClamp(5, 10, 100);
            assertThat(result).isEqualTo(10);
        }

        @Test
        @DisplayName("should return max when value greater than max")
        void shouldReturnMaxWhenValueGreaterThanMax() {
            double result = invokeClamp(150, 10, 100);
            assertThat(result).isEqualTo(100);
        }

        @Test
        @DisplayName("should return value when in range")
        void shouldReturnValueWhenInRange() {
            double result = invokeClamp(50, 10, 100);
            assertThat(result).isEqualTo(50);
        }

        @Test
        @DisplayName("should return value when equal to min")
        void shouldReturnValueWhenEqualToMin() {
            double result = invokeClamp(10, 10, 100);
            assertThat(result).isEqualTo(10);
        }

        @Test
        @DisplayName("should return value when equal to max")
        void shouldReturnValueWhenEqualToMax() {
            double result = invokeClamp(100, 10, 100);
            assertThat(result).isEqualTo(100);
        }
    }

    private double invokeClamp(double value, double min, double max) {
        try {
            var method = ImagePreviewDialog.class.getDeclaredMethod("clamp", double.class, double.class, double.class);
            method.setAccessible(true);
            return (double) method.invoke(null, value, min, max);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
