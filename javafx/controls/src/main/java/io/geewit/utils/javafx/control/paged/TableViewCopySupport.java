package io.geewit.utils.javafx.control.paged;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.util.Callback;

import java.util.ArrayList;
import javafx.collections.ObservableList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * TableView 复制与可选文本单元格支持工具类。
 *
 * <p>提供以下能力：</p>
 * <ul>
 *   <li>为 TableView 安装 Ctrl+C / Command+C 单元格复制（TSV 格式）</li>
 *   <li>根据字段 ID 为指定列应用只读 TextField 单元格，支持鼠标选中文本</li>
 * </ul>
 */
public final class TableViewCopySupport {

    private static final String INSTALL_MARK_KEY = TableViewCopySupport.class.getName() + ".installed";
    private static final String ORIGINAL_CELL_FACTORY_KEY = TableViewCopySupport.class.getName() + ".originalCellFactory";
    private static final String STYLE_CLASS_TEXT_FIELD = "selectable-table-cell-text-field";

    private TableViewCopySupport() {
        // utility class
    }

    /**
     * 为指定 TableView 安装单元格复制快捷键支持。
     *
     * <p>安装后会启用 cell selection 模式，并监听 {@code Ctrl+C / Command+C}
     * 将选中单元格内容以 TSV 格式写入系统剪贴板。</p>
     *
     * @param tableView 目标表格，不能为 null
     */
    public static void installCellCopy(TableView<?> tableView) {
        Objects.requireNonNull(tableView, "tableView must not be null");

        if (tableView.getProperties().containsKey(INSTALL_MARK_KEY)) {
            return;
        }
        tableView.getProperties().put(INSTALL_MARK_KEY, Boolean.TRUE);

        tableView.getSelectionModel().setCellSelectionEnabled(true);

        KeyCodeCombination copyShortcut = new KeyCodeCombination(KeyCode.C, KeyCombination.SHORTCUT_DOWN);

        tableView.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (!copyShortcut.match(event)) {
                return;
            }

            @SuppressWarnings("unchecked")
            ObservableList<TablePosition<?, ?>> selectedCells =
                    (ObservableList<TablePosition<?, ?>>) (ObservableList<?>) tableView.getSelectionModel().getSelectedCells();
            if (selectedCells == null || selectedCells.isEmpty()) {
                return;
            }

            String tsv = buildTsv(tableView, selectedCells);
            if (tsv.isEmpty()) {
                return;
            }

            ClipboardContent content = new ClipboardContent();
            content.putString(tsv);
            Clipboard.getSystemClipboard().setContent(content);
            event.consume();
        });
    }

    /**
     * 根据字段 ID 集合，为匹配的列安装可选文本单元格工厂。
     *
     * <p>支持多级表头（自动递归展开 leaf columns）。</p>
     *
     * <p>对于匹配的列，会安装只读 TextField 单元格工厂；
     * 对于不再匹配的列，如果之前被修改过，会恢复原始 cell factory。</p>
     *
     * @param columns             表格列集合
     * @param selectableFieldIds  允许鼠标选中文本的字段 ID 集合
     * @param <S>                 表格数据类型
     */
    @SuppressWarnings("unchecked")
    public static <S> void applySelectableTextFields(
            Collection<TableColumn<S, ?>> columns,
            Collection<String> selectableFieldIds) {

        if (columns == null || columns.isEmpty()) {
            return;
        }

        Set<String> idSet = normalizeFieldIds(selectableFieldIds);
        List<TableColumn<S, ?>> leafColumns = flattenColumns(columns);

        for (TableColumn<S, ?> column : leafColumns) {
            String fieldId = resolveFieldId(column);
            boolean shouldBeSelectable = fieldId != null && idSet.contains(fieldId);

            @SuppressWarnings("unchecked")
            Callback<TableColumn<S, Object>, TableCell<S, Object>> currentFactory =
                    (Callback<TableColumn<S, Object>, TableCell<S, Object>>) (Callback<?, ?>) column.getCellFactory();

            boolean wasSelectable = column.getProperties().containsKey(ORIGINAL_CELL_FACTORY_KEY);

            if (shouldBeSelectable) {
                if (!wasSelectable) {
                    // Save original factory before replacing
                    column.getProperties().put(ORIGINAL_CELL_FACTORY_KEY, currentFactory);
                    // Install selectable factory - need to cast because of raw type issues
                    installSelectableFactory(column);
                }
            } else {
                if (wasSelectable) {
                    // Restore original factory
                    @SuppressWarnings("unchecked")
                    Callback<TableColumn<S, Object>, TableCell<S, Object>> originalFactory =
                            (Callback<TableColumn<S, Object>, TableCell<S, Object>>) column.getProperties().get(ORIGINAL_CELL_FACTORY_KEY);
                    @SuppressWarnings("unchecked")
                    TableColumn<S, Object> typedColumn = (TableColumn<S, Object>) column;
                    typedColumn.setCellFactory(originalFactory);
                    column.getProperties().remove(ORIGINAL_CELL_FACTORY_KEY);
                }
            }
        }
    }

    /**
     * 创建只读 TextField 单元格工厂，支持鼠标选中文本。
     *
     * <p>左键单击选中当前单元格/行；左键双击消费事件避免触发表格行双击。
     * 右键显示合并菜单：包含"复制该列"以及调用方在行级别配置的原有菜单项。</p>
     *
     * @param <S> 表格行数据类型
     * @param <V> 单元格值类型
     * @return 单元格工厂
     */
    public static <S, V> Callback<TableColumn<S, V>, TableCell<S, V>> selectableTextCellFactory() {
        return column -> new TableCell<>() {
            private final TextField textField = createSelectableTextField();
            private MenuItem copyItem;

            {
                textField.setOnMouseClicked(event -> {
                    if (event.getButton() != MouseButton.PRIMARY) {
                        return;
                    }
                    TableView<S> tableView = getTableView();
                    if (tableView == null) {
                        return;
                    }
                    int index = getIndex();
                    if (index >= 0 && index < tableView.getItems().size()) {
                        tableView.getSelectionModel().clearSelection();
                        tableView.getSelectionModel().select(index, column);
                        tableView.requestFocus();
                    }
                    if (event.getClickCount() >= 2) {
                        event.consume();
                    }
                });

                // 右键：合并调用方行菜单并添加"复制"
                textField.setOnContextMenuRequested(event -> {
                    TableRow<?> row = getTableRow();
                    ContextMenu rowMenu = row != null ? row.getContextMenu() : null;

                    if (rowMenu != null) {
                        if (copyItem == null) {
                            copyItem = new MenuItem("复制");
                            copyItem.setOnAction(_ -> copyColumnAllValues(getTableView(), column));
                        }

                        if (!rowMenu.getItems().contains(copyItem)) {
                            rowMenu.getItems().addFirst(copyItem);
                            rowMenu.setOnHidden(_ -> rowMenu.getItems().remove(copyItem));
                        }

                        rowMenu.show(textField, event.getScreenX(), event.getScreenY());
                    }

                    event.consume();
                });
            }

            @Override
            protected void updateItem(V item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    textField.setText("");
                    setText(null);
                    setGraphic(null);
                } else {
                    textField.setText(String.valueOf(item));
                    setText(null);
                    setGraphic(textField);
                }
            }
        };
    }

    // ===== internal helpers =====

    private static <S> void installSelectableFactory(TableColumn<S, ?> column) {
        Callback<TableColumn<S, Object>, TableCell<S, Object>> factory = selectableTextCellFactory();
        @SuppressWarnings("unchecked")
        TableColumn<S, Object> typedColumn = (TableColumn<S, Object>) column;
        typedColumn.setCellFactory(factory);
    }

    private static TextField createSelectableTextField() {
        TextField textField = new TextField();
        textField.setEditable(false);
        textField.setFocusTraversable(true);
        // 禁用 TextField 默认右键菜单（避免显示"全选"等编辑菜单项）
        textField.setContextMenu(new ContextMenu());
        textField.setStyle(
                "-fx-background-color: transparent;"
                        + " -fx-background-insets: 0;"
                        + " -fx-background-radius: 0;"
                        + " -fx-border-color: transparent;"
                        + " -fx-padding: 0;"
        );
        textField.getStyleClass().add(STYLE_CLASS_TEXT_FIELD);
        return textField;
    }

    static String buildTsv(TableView<?> tableView,
                           ObservableList<TablePosition<?, ?>> selectedCells) {
        // Sort by row then column to maintain reading order
        var sorted = selectedCells.stream()
                .sorted((a, b) -> {
                    int cmp = Integer.compare(a.getRow(), b.getRow());
                    if (cmp != 0) {
                        return cmp;
                    }
                    var colA = a.getTableColumn();
                    var colB = b.getTableColumn();
                    int idxA = colA == null ? -1 : tableView.getColumns().indexOf(colA);
                    int idxB = colB == null ? -1 : tableView.getColumns().indexOf(colB);
                    return Integer.compare(idxA, idxB);
                })
                .toList();

        StringBuilder sb = new StringBuilder();
        int currentRow = -1;

        for (var pos : sorted) {
            int row = pos.getRow();
            if (row != currentRow) {
                if (currentRow != -1) {
                    sb.append(System.lineSeparator());
                }
                currentRow = row;
            } else {
                sb.append('\t');
            }

            Object value = getCellValue(tableView, pos);
            String text = value == null ? "" : String.valueOf(value);
            sb.append(sanitizeForTsv(text));
        }

        return sb.toString();
    }

    /**
     * 复制指定列的全部值到系统剪贴板，每行一个值。
     *
     * @param tableView 目标表格
     * @param column    目标列
     */
    public static void copyColumnAllValues(TableView<?> tableView, TableColumn<?, ?> column) {
        if (tableView == null || column == null) {
            return;
        }
        var items = tableView.getItems();
        if (items == null || items.isEmpty()) {
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            Object rowData = items.get(i);
            String text;
            if (rowData == null) {
                text = "";
            } else {
                @SuppressWarnings("unchecked")
                TableColumn<Object, ?> typedColumn = (TableColumn<Object, ?>) column;
                Object value = typedColumn.getCellData(rowData);
                text = value == null ? "" : String.valueOf(value);
            }
            if (i > 0) {
                sb.append(System.lineSeparator());
            }
            sb.append(sanitizeForTsv(text));
        }

        String result = sb.toString();
        if (!result.isEmpty()) {
            ClipboardContent content = new ClipboardContent();
            content.putString(result);
            Clipboard.getSystemClipboard().setContent(content);
        }
    }

    private static Object getCellValue(TableView<?> tableView, TablePosition<?, ?> pos) {
        if (pos.getRow() < 0 || pos.getRow() >= tableView.getItems().size()) {
            return null;
        }
        var column = pos.getTableColumn();
        if (column == null) {
            return null;
        }
        @SuppressWarnings("unchecked")
        TableColumn<Object, ?> typedColumn = (TableColumn<Object, ?>) column;
        Object rowData = tableView.getItems().get(pos.getRow());
        if (rowData == null) {
            return null;
        }
        return typedColumn.getCellData(rowData);
    }

    private static String sanitizeForTsv(String text) {
        return text.replace('\t', ' ')
                .replace('\r', ' ')
                .replace('\n', ' ');
    }

    static <S> List<TableColumn<S, ?>> flattenColumns(Collection<TableColumn<S, ?>> columns) {
        List<TableColumn<S, ?>> result = new ArrayList<>();
        for (TableColumn<S, ?> column : columns) {
            if (column.getColumns().isEmpty()) {
                result.add(column);
            } else {
                result.addAll(flattenColumns(column.getColumns()));
            }
        }
        return result;
    }

    static String resolveFieldId(TableColumn<?, ?> column) {
        String id = column.getId();
        if (id != null && !id.isBlank()) {
            return id.trim();
        }
        Object userData = column.getUserData();
        if (userData instanceof String str && !str.isBlank()) {
            return str.trim();
        }
        return null;
    }

    static Set<String> normalizeFieldIds(Collection<String> fieldIds) {
        if (fieldIds == null || fieldIds.isEmpty()) {
            return Set.of();
        }
        Set<String> result = new LinkedHashSet<>();
        for (String id : fieldIds) {
            if (id == null || id.isBlank()) {
                continue;
            }
            result.add(id.trim());
        }
        return Set.copyOf(result);
    }
}
