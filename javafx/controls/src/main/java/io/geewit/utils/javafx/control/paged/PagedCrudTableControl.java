package io.geewit.utils.javafx.control.paged;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import lombok.Getter;

import java.util.List;
import java.util.Objects;

public final class PagedCrudTableControl<T, K, Q> extends Control {

    @Getter
    private final ObservableList<T> items = FXCollections.observableArrayList();

    private final ReadOnlyObjectWrapper<T> selectedItem = new ReadOnlyObjectWrapper<>();

    private final ObjectProperty<List<TableColumn<T, ?>>> columns =
            new SimpleObjectProperty<>(this, "columns", List.of());

    private final ObjectProperty<PagedCrudTableConfig<T, K, Q>> config =
            new SimpleObjectProperty<>(this, "config");

    public PagedCrudTableControl() {
        super.getStyleClass().add("paged-crud-table");
    }

    public void initialize(List<TableColumn<T, ?>> columns,
                                                     PagedCrudTableConfig<T, K, Q> config) {
        this.setColumns(columns);
        this.setConfig(Objects.requireNonNull(config, "config must not be null"));
        this.searchWhenSkinReady();
    }

    /**
     * 创建默认的皮肤对象
     *
     * @return 返回一个新的PagedCrudTableSkin实例，用于渲染当前组件的视觉外观
     */
    @Override
    protected Skin<?> createDefaultSkin() {
        return new PagedCrudTableSkin<>(this);
    }

    // ===== API =====
    public List<TableColumn<T, ?>> getColumns() {
        return columns.get();
    }

    public void setColumns(List<TableColumn<T, ?>> cols) {
        this.columns.set(cols == null ? List.of() : List.copyOf(cols));
    }

    public ObjectProperty<List<TableColumn<T, ?>>> columnsProperty() {
        return columns;
    }

    public PagedCrudTableConfig<T, K, Q> getConfig() {
        return config.get();
    }

    public void setConfig(PagedCrudTableConfig<T, K, Q> cfg) {
        this.config.set(Objects.requireNonNull(cfg, "config must not be null"));
    }

    public ObjectProperty<PagedCrudTableConfig<T, K, Q>> configProperty() {
        return config;
    }

    // ===== actions =====

    public void search() {
        Actions.of(this).search();
    }

    public void searchWhenSkinReady() {
        if (getSkin() != null) {
            search();
            return;
        }
        ChangeListener<Skin<?>> skinListener = new ChangeListener<>() {
            @Override
            public void changed(javafx.beans.value.ObservableValue<? extends Skin<?>> obs,
                                Skin<?> oldSkin,
                                Skin<?> newSkin) {
                if (newSkin != null) {
                    skinProperty().removeListener(this);
                    Platform.runLater(() -> search());
                }
            }
        };
        skinProperty().addListener(skinListener);
    }

    public void reloadCurrentPage() {
        Actions.of(this).reloadCurrentPage();
    }

    public void add() {
        Actions.of(this).add();
    }

    public void editSelected() {
        Actions.of(this).editSelected();
    }

    public void deleteSelected() {
        Actions.of(this).deleteSelected();
    }

    ReadOnlyObjectWrapper<T> selectedItemWrapper() { // package-private 给 Skin 用
        return selectedItem;
    }

    public ReadOnlyObjectProperty<T> selectedItemProperty() {
        return selectedItem.getReadOnlyProperty();
    }

    public T getSelectedItem() {
        return selectedItem.get();
    }

    /**
     * 供外部绑定按钮 disabled / selection 使用（例如 edit/delete 按钮）。
     * 注意：如果控件还未完成 CSS/布局，可能需要先调用 applyCss()/layout()。
     */
    public TableView<T> getTableView() {
        Skin<?> skin = this.getSkin();
        if (skin instanceof PagedCrudTableSkin<?, ?, ?> s) {
            @SuppressWarnings("unchecked")
            TableView<T> tv = ((PagedCrudTableSkin<T, K, Q>) s).tableView();
            return tv;
        } else {
            this.applyCss();
            this.layout();
        }
        throw new IllegalStateException("Cannot resolve TableView from control skin.");
    }
}
