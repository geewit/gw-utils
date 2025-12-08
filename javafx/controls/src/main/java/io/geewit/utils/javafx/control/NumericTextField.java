package io.geewit.utils.javafx.control;

import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import lombok.Getter;

import java.util.Objects;
import java.util.function.UnaryOperator;

/**
 * NumericTextField
 * 一个只允许输入数字的 JavaFX TextField
 * 支持：
 *   1. 是否允许小数
 *   2. 是否允许负数
 */
@Getter
public class NumericTextField extends TextField {

    private boolean allowDecimal;
    private boolean allowNegative;
    private Integer maxFractionDigits;

    public NumericTextField() {
        this(false, false);
    }

    public NumericTextField(boolean allowDecimal, boolean allowNegative) {
        super();
        this.allowDecimal = allowDecimal;
        this.allowNegative = allowNegative;
        this.maxFractionDigits = null;
        this.configureFormatter();
    }

    public void setAllowDecimal(boolean allowDecimal) {
        if (this.allowDecimal != allowDecimal) {
            this.allowDecimal = allowDecimal;
            this.configureFormatter();
        }
    }

    public void setAllowNegative(boolean allowNegative) {
        if (this.allowNegative != allowNegative) {
            this.allowNegative = allowNegative;
            this.configureFormatter();
        }
    }

    public void setMaxFractionDigits(Integer maxFractionDigits) {
        Integer sanitized = maxFractionDigits != null && maxFractionDigits < 0 ? null : maxFractionDigits;
        if (!Objects.equals(this.maxFractionDigits, sanitized)) {
            this.maxFractionDigits = sanitized;
            this.configureFormatter();
        }
    }

    private void configureFormatter() {
        String currentText = super.getText();
        UnaryOperator<TextFormatter.Change> filter = change -> {
            if (allowDecimal) {
                String text = change.getText();
                if (text != null && !text.isEmpty()) {
                    String normalized = text
                            .replace(',', '.')
                            .replace('，', '.')
                            .replace('。', '.')
                            .replace('．', '.');
                    if (!normalized.equals(text)) {
                        change.setText(normalized);
                    }
                }
            }
            String newText = change.getControlNewText();

            String regex = this.buildRegex(); // 调用新提取的方法

            return newText.matches(regex) ? change : null;
        };

        TextFormatter<String> formatter = new TextFormatter<>(filter);
        super.setTextFormatter(formatter);
        if (currentText != null && !currentText.isEmpty()) {
            super.setText(currentText);
        }
    }

    // 新增方法：用于构建正则表达式
    private String buildRegex() {
        String regex;
        if (allowDecimal) {
            String decimalPattern;
            if (maxFractionDigits != null) {
                decimalPattern = String.format("(\\.\\d{0,%d})?", maxFractionDigits);
            } else {
                decimalPattern = "(\\.\\d*)?";
            }
            if (allowNegative) {
                regex = "-?\\d*" + decimalPattern;
            } else {
                regex = "\\d*" + decimalPattern;
            }
        } else if (allowNegative) {
            regex = "-?\\d*";
        } else {
            regex = "\\d*";
        }
        return regex;
    }

    /**
     * 获取整数值，如果为空返回 0
     */
    public int getIntValue() {
        String text = getText();
        return this.isBlankNumeric(text) ? 0 : Integer.parseInt(text);
    }

    /**
     * 获取小数值，如果为空返回 0.0
     */
    public double getDoubleValue() {
        String text = getText();
        return this.isBlankNumeric(text) ? 0.0 : Double.parseDouble(text);
    }

    private boolean isBlankNumeric(String text) {
        if (text == null || text.isEmpty()) {
            return true;
        }
        String trimmed = text.trim();
        return ".".equals(trimmed) || "-".equals(trimmed) || "-.".equals(trimmed);
    }
}

