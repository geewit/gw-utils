package io.geewit.utils.javafx.base;

import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.stage.Window;

public final class TableViewUtils {
    private TableViewUtils() {
    }

    public static final double DEFAULT_ROW_HEIGHT = 32d;
    public static final double TABLE_HEADER_HEIGHT = 28d;
    public static final int MIN_VISIBLE_ROWS = 1;
    public static final int MAX_VISIBLE_ROWS = 10;
    private static final double HEIGHT_PADDING = 6d;

    public static void adjustTableHeight(TableView<?> tableView) {
        Stage stage = null;
        if (tableView != null && tableView.getScene() != null) {
            Window window = tableView.getScene().getWindow();
            if (window instanceof Stage s) {
                stage = s;
            }
        }
        int maxVisibleRows = tableView != null && tableView.getItems() != null
                ? Math.max(tableView.getItems().size(), MIN_VISIBLE_ROWS)
                : MIN_VISIBLE_ROWS;
        adjustTableHeight(tableView, stage, DEFAULT_ROW_HEIGHT, TABLE_HEADER_HEIGHT, 0, maxVisibleRows);
    }

    public static void adjustTableHeight(TableView<?> tableView, Stage stage) {
        adjustTableHeight(tableView, stage, DEFAULT_ROW_HEIGHT, TABLE_HEADER_HEIGHT, MIN_VISIBLE_ROWS, MAX_VISIBLE_ROWS);
    }

    public static void adjustTableHeight(TableView<?> tableView,
                                         Stage stage,
                                         double defaultRowHeight,
                                         double headerHeight,
                                         int minVisibleRows,
                                         int maxVisibleRows) {
        if (tableView == null) {
            return;
        }
        ObservableList<?> items = tableView.getItems();
        int rowCount = items == null ? 0 : items.size();
        double rowHeight = tableView.getFixedCellSize();
        if (rowHeight <= 0) {
            rowHeight = defaultRowHeight;
        }
        int visibleRows = Math.max(Math.min(rowCount, maxVisibleRows), minVisibleRows);
        double verticalPadding = HEIGHT_PADDING;
        if (tableView.getInsets() != null) {
            verticalPadding += tableView.getInsets().getTop() + tableView.getInsets().getBottom();
        }
        double prefHeight = headerHeight + visibleRows * rowHeight + verticalPadding;
        double maxHeight = headerHeight + Math.max(rowCount, minVisibleRows) * rowHeight + verticalPadding;
        tableView.setPrefHeight(prefHeight);
        tableView.setMinHeight(prefHeight);
        tableView.setMaxHeight(maxHeight);
        if (stage != null) {
            stage.sizeToScene();
        }
    }
}
