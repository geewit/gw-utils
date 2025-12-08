package io.geewit.utils.javafx.base;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;

/**
 * Utility methods for applying {@link Tooltip}s to all {@link Button}s in a scene graph
 * and adjusting their show delay.
 */
public final class TooltipUtils {

    private static final Duration DEFAULT_DELAY = Duration.millis(100);

    private TooltipUtils() {
        // utility class
    }

    /**
     * Recursively walk the node tree and ensure that every {@link Button} has a {@link Tooltip}
     * with its show delay set to {@link #DEFAULT_DELAY}.
     *
     * @param node the root node to start applying tooltips from
     */
    public static void apply(Node node) {
        if (node instanceof Button button) {
            Tooltip tooltip = button.getTooltip();
            if (tooltip == null) {
                tooltip = new Tooltip(button.getText());
                button.setTooltip(tooltip);
            }
            tooltip.setShowDelay(DEFAULT_DELAY);
        }
        if (node instanceof Parent parent) {
            parent.getChildrenUnmodifiable().forEach(TooltipUtils::apply);
        }
    }
}

