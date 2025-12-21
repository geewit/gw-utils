package io.geewit.utils.javafx.control.paged;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public final class PagedCrudTableSkin<T, K, Q> extends SkinBase<PagedCrudTableControl<T, K, Q>> {

    final TableView<T> table = new TableView<>();
    final Pagination pagination = new Pagination();

    final Actions<T, K, Q> actions = new Actions<>(this);

    public PagedCrudTableSkin(PagedCrudTableControl<T, K, Q> control) {
        super(control);

        VBox root = new VBox(10, table, pagination);
        VBox.setVgrow(table, Priority.ALWAYS);
        getChildren().add(root);

        table.setItems(control.getItems());
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // 列变化：重建
        control.columnsProperty().addListener((_, _, _) -> rebuildColumns());
        this.rebuildColumns();

        // 配置变化：刷新 placeholder/rowFactory 并重新查询
        control.configProperty().addListener((_, _, _) -> {
            this.applyPlaceholder();
            this.applyRowFactory();
            actions.search();
        });

        // page 切换：加载页
        pagination.currentPageIndexProperty().addListener((_, _, nv) -> {
            if (nv == null) return;
            loadPage(nv.intValue());
        });

        // 初始应用
        this.applyPlaceholder();
        this.applyRowFactory();
        if (control.getConfig() != null) {
            actions.search();
        }
    }

    TableView<T> tableView() {
        return table;
    }

    private void rebuildColumns() {
        table.getColumns().clear();
        List<TableColumn<T, ?>> cols = super.getSkinnable().getColumns();
        if (cols != null && !cols.isEmpty()) {
            table.getColumns().addAll(cols);
        }
    }

    private void applyPlaceholder() {
        PagedCrudTableConfig<T, K, Q> cfg = super.getSkinnable().getConfig();
        if (cfg == null) {
            table.setPlaceholder(new Label(""));
            return;
        }
        String key = cfg.placeholderKey();
        String text = (key == null || key.isBlank()) ? "" : cfg.mp().message(key);
        table.setPlaceholder(new Label(text));
    }

    private void applyRowFactory() {
        PagedCrudTableConfig<T, K, Q> cfg = super.getSkinnable().getConfig();
        if (cfg == null) {
            table.setRowFactory(_ -> new TableRow<>());
            return;
        }

        final Consumer<T> onDblClick = cfg.onRowDoubleClick();

        table.setRowFactory(_ -> {
            TableRow<T> row = new TableRow<>();

            // 右键菜单：仅对非空行
            ContextMenu menu = this.buildContextMenu(cfg, row);
            row.contextMenuProperty().bind(
                    Bindings.when(row.emptyProperty()).then((ContextMenu) null).otherwise(menu)
            );

            // 双击
            row.setOnMouseClicked(evt -> {
                if (evt.getButton() != MouseButton.PRIMARY) {
                    return;
                }
                if (evt.getClickCount() != 2) {
                    return;
                }
                if (row.isEmpty()) {
                    return;
                }

                if (onDblClick != null) {
                    onDblClick.accept(row.getItem());
                }
                evt.consume();
            });

            return row;
        });
    }

    private ContextMenu buildContextMenu(PagedCrudTableConfig<T, K, Q> cfg, TableRow<T> row) {
        ContextMenu menu = new ContextMenu();
        List<RowAction<T>> actions = cfg.rowActions();
        if (actions == null || actions.isEmpty()) {
            return menu;
        }

        for (RowAction<T> action : actions) {
            MenuItem item = new MenuItem(cfg.mp().message(action.textKey()));

            if (action.iconLiteral() != null && !action.iconLiteral().isBlank()) {
                item.setGraphic(new FontIcon(action.iconLiteral()));
            }

            item.setOnAction(_ -> {
                T data = row.getItem();
                if (data == null) {
                    return;
                }
                if (action.isDisabled(data)) {
                    return;
                }
                action.handler().accept(data);
            });

            item.disableProperty().bind(Bindings.createBooleanBinding(() -> {
                T data = row.getItem();
                return data == null || action.isDisabled(data);
            }, row.itemProperty()));

            menu.getItems().add(item);
        }

        return menu;
    }

    void loadPage(int pageIndex) {
        PagedCrudTableConfig<T, K, Q> cfg = super.getSkinnable().getConfig();
        if (cfg == null) {
            return;
        }

        Q q = cfg.querySupplier().get();
        cfg.service().query(q, pageIndex, cfg.pageSize())
                .thenAccept(result -> runOnFx(() -> {
                    ObservableList<T> items = super.getSkinnable().getItems();
                    items.setAll(result.records());
                    int pages = Math.max(1, result.totalPages(cfg.pageSize()));
                    pagination.setPageCount(pages);
                }))
                .exceptionally(ex -> {
                    cfg.errorHandler().accept(ex);
                    return null;
                });
    }

    Consumer<T> upsertWriteBack(PagedCrudTableConfig<T, K, Q> cfg) {
        return incoming -> runOnFx(() -> {
            if (incoming == null) {
                return;
            }

            ObservableList<T> items = super.getSkinnable().getItems();
            K key = cfg.keyFn().apply(incoming);

            OptionalInt idxOptional = IntStream.range(0, items.size())
                    .filter(i -> Objects.equals(cfg.keyFn().apply(items.get(i)), key))
                    .findFirst();

            if (idxOptional.isPresent()) {
                T existing = items.get(idxOptional.getAsInt());
                cfg.copier().accept(incoming, existing);
                if (cfg.refreshAfterCopy()) {
                    table.refresh();
                }
                table.getSelectionModel().select(existing);
                table.scrollTo(existing);
            } else {
                items.add(incoming);
                table.getSelectionModel().select(incoming);
                table.scrollTo(incoming);
            }
        });
    }

    List<T> selectedRows() {
        List<T> selected = new ArrayList<>(table.getSelectionModel().getSelectedItems());
        if (!selected.isEmpty()) {
            return selected;
        }
        T one = table.getSelectionModel().getSelectedItem();
        return one == null ? List.of() : List.of(one);
    }

    private List<K> selectedIds(PagedCrudTableConfig<T, K, Q> cfg, List<T> rows) {
        return rows.stream()
                .map(cfg.idFn())
                .filter(Objects::nonNull)
                .distinct()
                .toList();
    }

    CompletionStage<Void> deleteBatch(PagedCrudTableConfig<T, K, Q> cfg,
                                              List<T> rows) {
        List<K> ids = this.selectedIds(cfg, rows);
        if (ids.isEmpty()) {
            return CompletableFuture.completedFuture(null);
        }
        return cfg.service().deleteByIds(ids);
    }

    static void runOnFx(Runnable r) {
        if (Platform.isFxApplicationThread()) {
            r.run();
        } else {
            Platform.runLater(r);
        }
    }

}

