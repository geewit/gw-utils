package io.geewit.utils.javafx.control.paged;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

public record RowAction<T>(
        String textKey,
        String iconLiteral,          // e.g. "fth-edit"
        Predicate<T> enabledWhen,    // null -> always enabled
        Consumer<T> handler
) {
    public RowAction {
        Objects.requireNonNull(textKey, "textKey");
        Objects.requireNonNull(handler, "handler");
    }

    public boolean isDisabled(T row) {
        return enabledWhen != null && !enabledWhen.test(row);
    }
}
