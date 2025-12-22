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

    /**
     * 判断指定行是否被禁用
     *
     * @param row 行数据对象
     * @return 如果行被禁用返回true，否则返回false
     */
    public boolean isDisabled(T row) {
        // 检查是否设置了启用条件，并且当前行不满足启用条件时返回true（表示禁用）
        return enabledWhen != null && !enabledWhen.test(row);
    }
}
