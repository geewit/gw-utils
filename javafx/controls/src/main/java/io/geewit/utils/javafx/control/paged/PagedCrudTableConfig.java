package io.geewit.utils.javafx.control.paged;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public record PagedCrudTableConfig<T, K, Q>(
        int pageSize,
        Supplier<Q> querySupplier,
        PagedCrudService<T, K, Q> service,

        // Upsert / Delete 所需
        Function<T, K> keyFn,           // upsert 定位

        BiConsumer<T, T> copier,        // src -> target（保持引用不变）
        boolean refreshAfterCopy,       // DTO 非 property 通常 true

        // 外部编辑器 & 删除确认
        Supplier<CompletionStage<Optional<T>>> openCreateEditor,
        Function<T, CompletionStage<Optional<T>>> openEditEditor,
        Function<List<T>, CompletionStage<Boolean>> confirmDelete,

        // i18n & 文案
        MessageProvider messageProvider,
        String placeholderKey,

        // 行级行为（右键菜单 + 双击）
        List<RowAction<T>> rowActions,
        Consumer<T> onRowDoubleClick,

        // error handler
        Consumer<Throwable> errorHandler
) {
    public MessageProvider mp() {
        return messageProvider == null ? MessageProvider.identity() : messageProvider;
    }
}