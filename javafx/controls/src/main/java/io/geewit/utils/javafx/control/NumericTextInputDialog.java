package io.geewit.utils.javafx.control;

import javafx.application.Platform;
import javafx.beans.NamedArg;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

/**
 * NumericTextInputDialog
 * 一个只允许输入整数的输入对话框，基于 {@link NumericTextField} 实现。
 * 说明 / Notes:
 * 1. 不再使用 com.sun.javafx.scene.control.skin.resources.ControlResources
 * 2. 不再使用 DialogPane.createContentLabel，完全用标准 JavaFX API
 */
public class NumericTextInputDialog extends Dialog<Integer> {

    private final GridPane grid;
    private final Label label;
    private final NumericTextField textField;

    public NumericTextInputDialog() {
        this(0);
    }

    /**
     * Creates a new TextInputDialog with the default value entered into the
     * dialog {@link TextField}.
     * 创建一个带默认值的数字输入对话框。
     * @param defaultValue the default value entered into the dialog
     *                     对话框中初始展示的默认整数值
     */
    public NumericTextInputDialog(@NamedArg("defaultValue") Integer defaultValue) {
        final DialogPane dialogPane = super.getDialogPane();

        // -- textfield
        this.textField = new NumericTextField();
        this.textField.setMaxWidth(Double.MAX_VALUE);
        // 默认只允许整数，和 Dialog<Integer> 的类型保持一致
        this.textField.setAllowDecimal(false);
        this.textField.setAllowNegative(false);

        // 设置默认值文本
        if (defaultValue != null) {
            this.textField.setText(String.valueOf(defaultValue));
        }

        GridPane.setHgrow(textField, Priority.ALWAYS);
        GridPane.setFillWidth(textField, true);

        // -- label（不再使用 DialogPane.createContentLabel）
        this.label = new Label();
        this.label.setPrefWidth(Region.USE_COMPUTED_SIZE);
        this.label.setWrapText(true);
        // 绑定对话框 contentText 属性
        this.label.textProperty().bind(dialogPane.contentTextProperty());

        // -- grid 布局
        this.grid = new GridPane();
        this.grid.setHgap(10);
        this.grid.setMaxWidth(Double.MAX_VALUE);
        this.grid.setAlignment(Pos.CENTER_LEFT);

        // 当 contentText 变化时更新布局（例如外部调用 setContentText）
        dialogPane.contentTextProperty().addListener(_ -> this.updateGrid());

        // ==== 标题与头部文本 ====
        // 不再从 ControlResources 里读取，直接给出简单默认值，
        // 调用方可以按需要自行覆盖 setTitle / setHeaderText。
        super.setTitle("Confirm");
        dialogPane.setHeaderText("Please enter a value:");
        dialogPane.getStyleClass().add("text-input-dialog");
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        this.updateGrid();

        // ==== 结果转换 ====
        super.setResultConverter((dialogButton) -> {
            ButtonBar.ButtonData data = dialogButton == null ? null : dialogButton.getButtonData();
            return data == ButtonBar.ButtonData.OK_DONE ? textField.getIntValue() : null;
        });
    }

    /**
     * Returns the {@link NumericTextField} used within this dialog.
     * 获取内部使用的 {@link NumericTextField} 实例，方便外部进一步配置。
     */
    public final NumericTextField getEditor() {
        return textField;
    }

    /* ************************************************************************
     *
     * Private Implementation
     *
     **************************************************************************/

    private void updateGrid() {
        grid.getChildren().clear();

        grid.add(label, 0, 0);
        grid.add(textField, 1, 0);
        super.getDialogPane().setContent(grid);

        // 异步请求焦点，避免布局尚未完成时抢焦点失败
        Platform.runLater(textField::requestFocus);
    }
}
