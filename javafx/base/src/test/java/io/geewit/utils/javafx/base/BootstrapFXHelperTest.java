package io.geewit.utils.javafx.base;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(ApplicationExtension.class)
public class BootstrapFXHelperTest {

    @Test
    void applyBootstrapFX_shouldHandleNullRoot() {
        // Should not throw
        BootstrapFXHelper.applyBootstrapFX(null);
    }

    @Test
    void applyBootstrapFX_shouldNotThrowWithValidRoot() {
        // Testing with a simple node
        javafx.scene.Parent root = new javafx.scene.layout.StackPane();

        // Should not throw when scene is not yet attached
        BootstrapFXHelper.applyBootstrapFX(root);
    }
}
