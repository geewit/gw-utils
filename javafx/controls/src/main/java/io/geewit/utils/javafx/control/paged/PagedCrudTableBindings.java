package io.geewit.utils.javafx.control.paged;

import javafx.scene.control.Button;

public final class PagedCrudTableBindings {

    private PagedCrudTableBindings() {}

    public static <T, K, Q> void bindEditDeleteButtons(
            PagedCrudTableControl<T, K, Q> control,
            Button editButton,
            Button deleteButton
    ) {
        if (editButton != null) {
            editButton.disableProperty().bind(control.selectedItemProperty().isNull());
        }
        if (deleteButton != null) {
            deleteButton.disableProperty().bind(control.selectedItemProperty().isNull());
        }
    }
}
