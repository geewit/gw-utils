package io.geewit.utils.javafx.control.paged;

import javafx.scene.control.Skin;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

// ===== Actions =====
public final class Actions<T, K, Q> {

    private final PagedCrudTableSkin<T, K, Q> pagedCrudTableSkin;

    public Actions(PagedCrudTableSkin<T, K, Q> pagedCrudTableSkin) {
        this.pagedCrudTableSkin = pagedCrudTableSkin;
    }

    public void search() {
        PagedCrudTableSkin.runOnFx(() -> pagedCrudTableSkin.pagination.setCurrentPageIndex(0));
        pagedCrudTableSkin.loadPage(0);
    }

    public void reloadCurrentPage() {
        pagedCrudTableSkin.loadPage(pagedCrudTableSkin.pagination.getCurrentPageIndex());
    }

    public void add() {
        PagedCrudTableConfig<T, K, Q> cfg = pagedCrudTableSkin.getSkinnable().getConfig();
        if (cfg == null) {
            return;
        }
        Consumer<T> wb = pagedCrudTableSkin.upsertWriteBack(cfg);

        cfg.openCreateEditor().get()
                .thenAccept(opt -> opt.ifPresent(wb))
                .exceptionally(ex -> {
                    cfg.errorHandler().accept(ex);
                    return null;
                });
    }

    public void editSelected() {
        PagedCrudTableConfig<T, K, Q> cfg = pagedCrudTableSkin.getSkinnable().getConfig();
        if (cfg == null) {
            return;
        }

        T selected = pagedCrudTableSkin.table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }

        Consumer<T> wb = pagedCrudTableSkin.upsertWriteBack(cfg);

        cfg.openEditEditor().apply(selected)
                .thenAccept(opt -> opt.ifPresent(wb))
                .exceptionally(ex -> {
                    cfg.errorHandler().accept(ex);
                    return null;
                });
    }

    public void deleteSelected() {
        PagedCrudTableConfig<T, K, Q> cfg = pagedCrudTableSkin.getSkinnable().getConfig();
        if (cfg == null) {
            return;
        }

        List<T> selected = pagedCrudTableSkin.selectedRows();
        if (selected.isEmpty()) {
            return;
        }

        cfg.confirmDelete().apply(selected)
                .thenCompose(ok -> {
                    if (!Boolean.TRUE.equals(ok)) {
                        return CompletableFuture.completedFuture(null);
                    }
                    return pagedCrudTableSkin.deleteBatch(cfg, selected)
                            .toCompletableFuture();
                })
                .thenRun(() -> PagedCrudTableSkin.runOnFx(() -> {
                    // UI 先移除，再 reload 确保 pageCount/total 一致
                    pagedCrudTableSkin.getSkinnable().getItems().removeAll(selected);
                    pagedCrudTableSkin.table.getSelectionModel().clearSelection();
                    pagedCrudTableSkin.loadPage(pagedCrudTableSkin.pagination.getCurrentPageIndex());
                }))
                .exceptionally(ex -> {
                    cfg.errorHandler().accept(ex);
                    return null;
                });
    }

    public static <T, K, Q> Actions<T, K, Q> of(PagedCrudTableControl<T, K, Q> c) {
        Skin<?> skin = c.getSkin();
        if (skin instanceof PagedCrudTableSkin<?, ?, ?> s) {
            @SuppressWarnings("unchecked")
            PagedCrudTableSkin<T, K, Q> ss = (PagedCrudTableSkin<T, K, Q>) s;
            return ss.actions;
        } else {
            c.applyCss();
            c.layout();
        }
        throw new IllegalStateException("Skin not initialized yet.");
    }
}
