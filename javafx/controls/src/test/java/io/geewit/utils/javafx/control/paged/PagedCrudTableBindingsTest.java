package io.geewit.utils.javafx.control.paged;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

import javafx.scene.control.Button;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PagedCrudTableBindings Tests")
@ExtendWith(ApplicationExtension.class)
class PagedCrudTableBindingsTest {

    @Nested
    @DisplayName("bindEditDeleteButtons")
    class BindEditDeleteButtons {

        @Test
        @DisplayName("should bind edit button to selection")
        void shouldBindEditButtonToSelection() {
            // Given
            PagedCrudTableControl<String, String, String> control = new PagedCrudTableControl<>();
            Button editButton = new Button("Edit");
            Button deleteButton = new Button("Delete");

            // When
            PagedCrudTableBindings.bindEditDeleteButtons(control, editButton, deleteButton);

            // Then - buttons should be disabled when no selection
            assertThat(editButton.disableProperty().getValue()).isTrue();
            assertThat(deleteButton.disableProperty().getValue()).isTrue();
        }

        @Test
        @DisplayName("should allow null edit button")
        void shouldAllowNullEditButton() {
            // Given
            PagedCrudTableControl<String, String, String> control = new PagedCrudTableControl<>();
            Button deleteButton = new Button("Delete");

            // When/Then - should not throw
            PagedCrudTableBindings.bindEditDeleteButtons(control, null, deleteButton);
        }

        @Test
        @DisplayName("should allow null delete button")
        void shouldAllowNullDeleteButton() {
            // Given
            PagedCrudTableControl<String, String, String> control = new PagedCrudTableControl<>();
            Button editButton = new Button("Edit");

            // When/Then - should not throw
            PagedCrudTableBindings.bindEditDeleteButtons(control, editButton, null);
        }

        @Test
        @DisplayName("should allow both buttons null")
        void shouldAllowBothButtonsNull() {
            // Given
            PagedCrudTableControl<String, String, String> control = new PagedCrudTableControl<>();

            // When/Then - should not throw
            PagedCrudTableBindings.bindEditDeleteButtons(control, null, null);
        }
    }
}