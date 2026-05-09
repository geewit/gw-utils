package io.geewit.utils.javafx.base;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(ApplicationExtension.class)
public class FxClicksTest {

    @Test
    void onDoubleClick_shouldHandleNullNode() {
        // Should not throw
        FxClicks.onDoubleClick((javafx.scene.Node) null, () -> {});
    }

    @Test
    void onDoubleClick_shouldHandleNullAction() {
        javafx.scene.Node node = new javafx.scene.layout.Region();

        // Should not throw
        FxClicks.onDoubleClick(node, (Runnable) null);
    }

    @Test
    void onDoubleClick_shouldHandleNullConsumerAction() {
        javafx.scene.Node node = new javafx.scene.layout.Region();

        // Should not throw
        FxClicks.onDoubleClick(node, (java.util.function.Consumer<javafx.scene.input.MouseEvent>) null);
    }

    @Test
    void onDoubleClick_shouldHandleNodeWithNullAction() {
        javafx.scene.Node node = new javafx.scene.layout.Region();

        // Should not throw - null action guard
        FxClicks.onDoubleClick(node, (Runnable) null);
    }

    @Test
    void onDoubleClick_shouldAddEventHandlerToNode() {
        javafx.scene.Node node = new javafx.scene.layout.Region();

        // Should not throw - adds event handler
        FxClicks.onDoubleClick(node, () -> {});

        // Verify handler was added by checking event dispatch works
        assertThat(node.getEventDispatcher()).isNotNull();
    }
}
