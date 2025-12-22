package io.geewit.utils.javafx.control.paged;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
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
    // ---------- convenient defaults ----------
    public static final int DEFAULT_PAGE_SIZE = 20;

    private static final BiConsumer<Object, Object> NOOP_COPIER = (_, _) -> {};
    @SuppressWarnings("unchecked")
    public static <T> BiConsumer<T, T> noopCopier() {
        return (BiConsumer<T, T>) NOOP_COPIER;
    }

    private static final Consumer<Throwable> DEFAULT_ERROR_HANDLER = Throwable::printStackTrace;

    private static <T> Supplier<CompletionStage<Optional<T>>> defaultCreateEditor() {
        return () -> CompletableFuture.completedFuture(Optional.empty());
    }

    private static <T> Function<T, CompletionStage<Optional<T>>> defaultEditEditor() {
        return _ -> CompletableFuture.completedFuture(Optional.empty());
    }

    private static <T> Function<List<T>, CompletionStage<Boolean>> defaultConfirmDelete() {
        return _ -> CompletableFuture.completedFuture(Boolean.TRUE);
    }

    public MessageProvider mp() {
        return messageProvider == null ? MessageProvider.identity() : messageProvider;
    }

    // ---------- Builder ----------
    public static <T, K, Q> Builder<T, K, Q> builder() {
        return new Builder<>();
    }

    public static final class Builder<T, K, Q> {
        private Integer pageSize;
        private Supplier<Q> querySupplier;
        private PagedCrudService<T, K, Q> service;

        private Function<T, K> keyFn;

        private BiConsumer<T, T> copier;
        private Boolean refreshAfterCopy;

        private Supplier<CompletionStage<Optional<T>>> openCreateEditor;
        private Function<T, CompletionStage<Optional<T>>> openEditEditor;
        private Function<List<T>, CompletionStage<Boolean>> confirmDelete;

        private MessageProvider messageProvider;
        private String placeholderKey;

        private List<RowAction<T>> rowActions;
        private Consumer<T> onRowDoubleClick;

        private Consumer<Throwable> errorHandler;

        private Builder() {}

        public Builder<T, K, Q> pageSize(int pageSize) {
            this.pageSize = pageSize;
            return this;
        }

        public Builder<T, K, Q> querySupplier(Supplier<Q> querySupplier) {
            this.querySupplier = querySupplier;
            return this;
        }

        public Builder<T, K, Q> service(PagedCrudService<T, K, Q> service) {
            this.service = service;
            return this;
        }

        public Builder<T, K, Q> keyFn(Function<T, K> keyFn) {
            this.keyFn = keyFn;
            return this;
        }

        /** ✅ copier 默认 no-op */
        public Builder<T, K, Q> copier(BiConsumer<T, T> copier) {
            this.copier = copier;
            return this;
        }

        public Builder<T, K, Q> refreshAfterCopy(boolean refreshAfterCopy) {
            this.refreshAfterCopy = refreshAfterCopy;
            return this;
        }

        public Builder<T, K, Q> openCreateEditor(Supplier<CompletionStage<Optional<T>>> openCreateEditor) {
            this.openCreateEditor = openCreateEditor;
            return this;
        }

        public Builder<T, K, Q> openEditEditor(Function<T, CompletionStage<Optional<T>>> openEditEditor) {
            this.openEditEditor = openEditEditor;
            return this;
        }

        public Builder<T, K, Q> confirmDelete(Function<List<T>, CompletionStage<Boolean>> confirmDelete) {
            this.confirmDelete = confirmDelete;
            return this;
        }

        public Builder<T, K, Q> messageProvider(MessageProvider messageProvider) {
            this.messageProvider = messageProvider;
            return this;
        }

        public Builder<T, K, Q> placeholderKey(String placeholderKey) {
            this.placeholderKey = placeholderKey;
            return this;
        }

        public Builder<T, K, Q> rowActions(List<RowAction<T>> rowActions) {
            this.rowActions = rowActions;
            return this;
        }

        public Builder<T, K, Q> onRowDoubleClick(Consumer<T> onRowDoubleClick) {
            this.onRowDoubleClick = onRowDoubleClick;
            return this;
        }

        public Builder<T, K, Q> errorHandler(Consumer<Throwable> errorHandler) {
            this.errorHandler = errorHandler;
            return this;
        }

        public PagedCrudTableConfig<T, K, Q> build() {
            int ps = (pageSize == null || pageSize <= 0) ? DEFAULT_PAGE_SIZE : pageSize;

            Supplier<Q> qs = Objects.requireNonNull(querySupplier, "querySupplier must not be null");
            PagedCrudService<T, K, Q> svc = Objects.requireNonNull(service, "service must not be null");
            Function<T, K> kf = Objects.requireNonNull(keyFn, "keyFn must not be null");

            BiConsumer<T, T> cp = copier == null ? PagedCrudTableConfig.noopCopier() : copier;
            boolean rac = refreshAfterCopy == null || refreshAfterCopy; // 默认 true

            Supplier<CompletionStage<Optional<T>>> ce = openCreateEditor == null ? defaultCreateEditor() : openCreateEditor;
            Function<T, CompletionStage<Optional<T>>> ee = openEditEditor == null ? defaultEditEditor() : openEditEditor;
            Function<List<T>, CompletionStage<Boolean>> cd = confirmDelete == null ? defaultConfirmDelete() : confirmDelete;

            MessageProvider mp = messageProvider == null ? MessageProvider.identity() : messageProvider;
            String pk = placeholderKey; // 可为 null

            List<RowAction<T>> ra = rowActions == null ? List.of() : List.copyOf(rowActions);
            Consumer<T> dc = onRowDoubleClick; // 可为 null

            Consumer<Throwable> eh = errorHandler == null ? DEFAULT_ERROR_HANDLER : errorHandler;

            return new PagedCrudTableConfig<>(
                    ps,
                    qs,
                    svc,
                    kf,
                    cp,
                    rac,
                    ce,
                    ee,
                    cd,
                    mp,
                    pk,
                    ra,
                    dc,
                    eh
            );
        }
    }
}
