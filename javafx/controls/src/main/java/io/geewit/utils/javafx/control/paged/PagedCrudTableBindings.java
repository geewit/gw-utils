package io.geewit.utils.javafx.control.paged;

import javafx.scene.control.Button;
import javafx.scene.control.TableView;

import java.util.Objects;

public final class PagedCrudTableBindings {

    private PagedCrudTableBindings() {}

    public static <T, K, Q> void bindEditDeleteButtons(
            Button editButton,
            Button deleteButton,
            PagedCrudTableControl<T, K, Q> control
    ) {
        Objects.requireNonNull(control, "control");

        TableView<T> table = control.getTableView();

        if (editButton != null) {
            editButton.disableProperty()
                    .bind(table.getSelectionModel().selectedItemProperty().isNull());
        }
        if (deleteButton != null) {
            deleteButton.disableProperty()
                    .bind(table.getSelectionModel().selectedItemProperty().isNull());
        }
    }
}
