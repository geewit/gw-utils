package io.geewit.utils.javafx.base;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@ExtendWith(ApplicationExtension.class)
public class TooltipUtilsTest {

    @Test
    void apply_shouldHandleNullNode() {
        // Should not throw
        TooltipUtils.apply(null);
    }

    @Test
    @Disabled("Tooltip creation requires JavaFX thread via Monocle which is incompatible with Java 25")
    void apply_shouldSetTooltipOnButtonWithNullTooltip() {
        javafx.scene.control.Button button = new javafx.scene.control.Button("Test Button");

        TooltipUtils.apply(button);

        assertThat(button.getTooltip()).isNotNull();
        assertThat(button.getTooltip().getText()).isEqualTo("Test Button");
    }

    @Test
    @Disabled("Tooltip creation requires JavaFX thread via Monocle which is incompatible with Java 25")
    void apply_shouldNotOverwriteExistingTooltip() {
        javafx.scene.control.Button button = new javafx.scene.control.Button("Test Button");
        javafx.scene.control.Tooltip existingTooltip = new javafx.scene.control.Tooltip("Custom Tooltip");
        button.setTooltip(existingTooltip);

        TooltipUtils.apply(button);

        assertThat(button.getTooltip()).isSameAs(existingTooltip);
        assertThat(button.getTooltip().getText()).isEqualTo("Custom Tooltip");
    }

    @Test
    @Disabled("Tooltip creation requires JavaFX thread via Monocle which is incompatible with Java 25")
    void apply_shouldSetShowDelayOnTooltip() {
        javafx.scene.control.Button button = new javafx.scene.control.Button("Test Button");

        TooltipUtils.apply(button);

        assertThat(button.getTooltip()).isNotNull();
        assertThat(button.getTooltip().getShowDelay()).isNotNull();
        // Default delay is 100ms
        assertThat(button.getTooltip().getShowDelay().toMillis()).isEqualTo(100);
    }

    @Test
    @Disabled("Tooltip creation requires JavaFX thread via Monocle which is incompatible with Java 25")
    void apply_shouldNotTraverseNonParentNodes() {
        javafx.scene.control.Button button = new javafx.scene.control.Button("Test Button");

        // Should not throw and should only set tooltip on the button itself
        TooltipUtils.apply(button);

        assertThat(button.getTooltip()).isNotNull();
    }

    @Test
    @Disabled("Tooltip creation requires JavaFX thread via Monocle which is incompatible with Java 25")
    void apply_shouldSkipNonButtonNodes() {
        javafx.scene.Parent parent = new javafx.scene.layout.VBox();
        javafx.scene.control.Label label = new javafx.scene.control.Label("Label");
        javafx.scene.control.TextField textField = new javafx.scene.control.TextField();
        parent.getChildrenUnmodifiable().addAll(label, textField);

        TooltipUtils.apply(parent);

        assertThat(label.getTooltip()).isNull();
        assertThat(textField.getTooltip()).isNull();
    }
}
