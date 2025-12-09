package io.geewit.utils.javafx.base.scene;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Paint;

import java.util.regex.Pattern;

/**
 * Application-wide {@link Scene} that enforces grayscale font smoothing and
 * the default font family for every root node attached to the scene.
 */
public class GwScene extends Scene {

    private static final String FONT_SMOOTHING_PROPERTY = "-fx-font-smoothing-type";
    private static final String FONT_SMOOTHING_VALUE = "gray";
    private static final String FONT_FAMILY_PROPERTY = "-fx-font-family";
    private static final String DEFAULT_FONT_FAMILY = "'Noto Sans SC'";

    private static final Pattern FONT_SMOOTHING_PATTERN =
            compilePropertyPattern(FONT_SMOOTHING_PROPERTY);
    private static final Pattern FONT_FAMILY_PATTERN =
            compilePropertyPattern(FONT_FAMILY_PROPERTY);

    public GwScene(Parent root) {
        super(root);
        applyGlobalFontStyling(root);
    }

    public GwScene(Parent root, double width, double height) {
        super(root, width, height);
        applyGlobalFontStyling(root);
    }

    public GwScene(Parent root, double width, double height, Paint fill) {
        super(root, width, height, fill);
        applyGlobalFontStyling(root);
    }

    private static void applyGlobalFontStyling(Parent root) {
        if (root == null) {
            return;
        }
        ensureStyleProperty(root, FONT_SMOOTHING_PROPERTY, FONT_SMOOTHING_VALUE, FONT_SMOOTHING_PATTERN);
        ensureStyleProperty(root, FONT_FAMILY_PROPERTY, DEFAULT_FONT_FAMILY, FONT_FAMILY_PATTERN);
    }

    private static void ensureStyleProperty(Parent node,
                                            String property,
                                            String value,
                                            Pattern pattern) {
        if (node == null || property == null || value == null) {
            return;
        }
        String currentStyle = node.getStyle();
        if (currentStyle != null && pattern != null && pattern.matcher(currentStyle).find()) {
            return;
        }
        StringBuilder builder = new StringBuilder(currentStyle == null ? "" : currentStyle.trim());
        if (!builder.isEmpty() && builder.charAt(builder.length() - 1) != ';') {
            builder.append(';');
        }
        if (!builder.isEmpty()) {
            builder.append(' ');
        }
        builder.append(property).append(": ").append(value).append(';');
        node.setStyle(builder.toString());
    }

    private static Pattern compilePropertyPattern(String property) {
        if (property == null || property.isBlank()) {
            return null;
        }
        return Pattern.compile("(^|;)\\s*" + Pattern.quote(property.trim()) + "\\s*:", Pattern.CASE_INSENSITIVE);
    }
}
