package io.geewit.utils.javafx.base;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("ConfirmDialogHelper Tests")
public class ConfirmDialogHelperTest {

    @Nested
    @DisplayName("builder()")
    class BuilderMethod {

        @Test
        @DisplayName("should create a new builder instance")
        void shouldCreateNewBuilder() {
            ConfirmDialogHelper.ConfirmBuilder builder = ConfirmDialogHelper.builder();
            assertThat(builder).isNotNull();
        }
    }

    @Nested
    @DisplayName("ConfirmBuilder")
    class BuilderChaining {

        @Test
        @DisplayName("should allow method chaining")
        void shouldAllowMethodChaining() {
            ConfirmDialogHelper.ConfirmBuilder builder = ConfirmDialogHelper.builder()
                    .message("Test message")
                    .title("Test title")
                    .header("Test header")
                    .onOk(() -> {})
                    .onCancel(() -> {});

            assertThat(builder).isNotNull();
        }

        @Test
        @DisplayName("should allow setting owner to null")
        void shouldAllowNullOwner() {
            ConfirmDialogHelper.ConfirmBuilder builder = ConfirmDialogHelper.builder()
                    .owner(null);

            assertThat(builder).isNotNull();
        }

        @Test
        @DisplayName("should allow setting message to null")
        void shouldAllowNullMessage() {
            ConfirmDialogHelper.ConfirmBuilder builder = ConfirmDialogHelper.builder()
                    .message(null);

            assertThat(builder).isNotNull();
        }

        @Test
        @DisplayName("should allow setting title to null")
        void shouldAllowNullTitle() {
            ConfirmDialogHelper.ConfirmBuilder builder = ConfirmDialogHelper.builder()
                    .title(null);

            assertThat(builder).isNotNull();
        }

        @Test
        @DisplayName("should allow setting header to null")
        void shouldAllowNullHeader() {
            ConfirmDialogHelper.ConfirmBuilder builder = ConfirmDialogHelper.builder()
                    .header(null);

            assertThat(builder).isNotNull();
        }

        @Test
        @DisplayName("should allow multiple method calls")
        void shouldAllowMultipleMethodCalls() {
            ConfirmDialogHelper.ConfirmBuilder builder = ConfirmDialogHelper.builder();
            builder.message("msg1");
            builder.message("msg2");
            builder.title("title1");
            builder.title("title2");

            assertThat(builder).isNotNull();
        }
    }

    @Nested
    @DisplayName("ConfirmBuilder.show() validation")
    class ShowValidation {

        @Test
        @DisplayName("should throw NullPointerException when message is null")
        void shouldThrowWhenMessageIsNull() {
            ConfirmDialogHelper.ConfirmBuilder builder = ConfirmDialogHelper.builder()
                    .message(null)
                    .onOk(() -> {});

            assertThatThrownBy(builder::show)
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("message must not be null");
        }

        @Test
        @DisplayName("should throw NullPointerException when okAction is null")
        void shouldThrowWhenOkActionIsNull() {
            ConfirmDialogHelper.ConfirmBuilder builder = ConfirmDialogHelper.builder()
                    .message("Test message")
                    .onOk(null);

            assertThatThrownBy(builder::show)
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("okAction must not be null");
        }
    }
}
