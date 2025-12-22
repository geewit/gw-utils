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
        super.getChildren().add(root);

        table.setItems(control.getItems());
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // 列变化：重建
        control.columnsProperty().addListener((_, _, _) -> this.rebuildColumns());
        this.rebuildColumns();

        // 配置变化：刷新 placeholder/rowFactory 并重新查询
        control.configProperty().addListener((_, _, _) -> {
            this.applyPlaceholder();
            this.applyRowFactory();
            actions.search();
        });

        // page 切换：加载页
        pagination.currentPageIndexProperty().addListener((_, _, nv) -> {
            if (nv == null) {
                return;
            }
            this.loadPage(nv.intValue());
        });

        // 初始应用
        this.applyPlaceholder();
        this.applyRowFactory();
        if (control.getConfig() != null) {
            actions.search();
        }
    }

    /**
     * 获取表格视图对象
     *
     * @return 返回当前实例中的表格视图组件
     */
    TableView<T> tableView() {
        return table;
    }

    /**
     * 重新构建表格列结构
     * 该方法用于清除当前表格的所有列，并根据可装配对象的列配置重新添加列数据。
     * 主要用于在表格结构发生变化时进行刷新重建。
     */
    private void rebuildColumns() {
        // 清除现有表格列
        table.getColumns().clear();

        // 获取并添加新的列配置
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

    /**
     * 应用行工厂配置到表格中
     *
     * <p>该方法根据配置信息设置表格的行工厂，包括右键菜单和双击事件处理。
     * 如果没有配置信息，则使用默认的行工厂。</p>
     */
    private void applyRowFactory() {
        PagedCrudTableConfig<T, K, Q> cfg = super.getSkinnable().getConfig();
        if (cfg == null) {
            table.setRowFactory(_ -> new TableRow<>());
            return;
        }

        final Consumer<T> onDblClick = cfg.onRowDoubleClick();

        // 设置自定义行工厂，包含右键菜单和双击事件处理
        table.setRowFactory(_ -> {
            TableRow<T> row = new TableRow<>();

            // 右键菜单：仅对非空行
            ContextMenu menu = this.buildContextMenu(cfg, row);
            row.contextMenuProperty().bind(
                    Bindings.when(row.emptyProperty())
                            .then((ContextMenu) null)
                            .otherwise(menu)
            );

            // 双击事件处理：仅处理左键双击非空行的情况
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

    /**
     * 构建表格行的上下文菜单
     *
     * @param cfg 表格配置对象，包含菜单项配置和消息提供器
     * @param row 表格行对象，用于获取当前行的数据
     * @return 构建好的上下文菜单对象
     */
    private ContextMenu buildContextMenu(PagedCrudTableConfig<T, K, Q> cfg, TableRow<T> row) {
        ContextMenu menu = new ContextMenu();

        // 获取消息提供器，如果为空则返回空菜单
        MessageProvider mp = cfg.mp();
        if (mp == null) {
            return menu;
        }

        // 根据配置创建菜单项列表
        List<MenuItem> menuItems = cfg.rowActions().stream()
                .filter(Objects::nonNull)
                .map(action -> this.createMenuItem(action, mp, row))
                .toList();

        menu.getItems().addAll(menuItems);
        return menu;
    }

    /**
     * 创建菜单项
     *
     * @param action 行操作对象，包含菜单项的相关配置信息
     * @param mp 消息提供者，用于获取菜单项显示文本
     * @param row 表格行对象，表示当前操作所在的行数据
     * @return 配置好的菜单项对象
     */
    private MenuItem createMenuItem(RowAction<T> action,
                                    MessageProvider mp,
                                    TableRow<T> row) {
        // 创建菜单项并设置显示文本
        MenuItem item = new MenuItem(mp.message(action.textKey()));

        // 如果配置了图标，则为菜单项设置图标
        if (action.iconLiteral() != null && !action.iconLiteral().isBlank()) {
            item.setGraphic(new FontIcon(action.iconLiteral()));
        }

        // 设置菜单项点击事件处理器
        item.setOnAction(_ -> this.handleAction(action, row));

        // 绑定菜单项禁用状态，根据行数据和操作配置动态判断是否禁用
        item.disableProperty().bind(Bindings.createBooleanBinding(
                () -> this.isActionDisabled(action, row),
                row.itemProperty()
        ));

        return item;
    }

    /**
     * 处理行操作事件
     *
     * @param action 行操作对象，包含操作的处理器和禁用状态判断逻辑
     * @param row 表格行对象，包含行数据
     */
    private void handleAction(RowAction<T> action, TableRow<T> row) {
        // 过滤出非空且未被禁用的数据项，并执行相应的操作处理器
        Optional.ofNullable(row.getItem())
                .filter(data -> !action.isDisabled(data))
                .ifPresent(data -> {
                    if (action.handler() != null) {
                        action.handler().accept(data);
                    }
                });
    }

    private boolean isActionDisabled(RowAction<T> action, TableRow<T> row) {
        return Optional.ofNullable(row.getItem())
                .map(action::isDisabled)
                .orElse(true);
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

