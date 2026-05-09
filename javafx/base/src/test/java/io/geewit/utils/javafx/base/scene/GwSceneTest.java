package io.geewit.utils.javafx.base.scene;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(ApplicationExtension.class)
public class GwSceneTest {

    @Nested
    @Disabled("GwScene constructors require JavaFX thread via Monocle which is incompatible with Java 25")
    class ConstructorTests {
        @Test
        void constructor_shouldAcceptRoot() {
            javafx.scene.Parent root = new javafx.scene.layout.StackPane();

            GwScene scene = new GwScene(root);

            assertThat(scene.getRoot()).isEqualTo(root);
        }

        @Test
        void constructor_shouldAcceptRootWithWidthAndHeight() {
            javafx.scene.Parent root = new javafx.scene.layout.StackPane();

            GwScene scene = new GwScene(root, 400, 300);

            assertThat(scene.getRoot()).isEqualTo(root);
            assertThat(scene.getWidth()).isEqualTo(400);
            assertThat(scene.getHeight()).isEqualTo(300);
        }

        @Test
        void constructor_shouldAcceptRootWithWidthHeightAndFill() {
            javafx.scene.Parent root = new javafx.scene.layout.StackPane();
            javafx.scene.paint.Color fill = javafx.scene.paint.Color.BLUE;

            GwScene scene = new GwScene(root, 400, 300, fill);

            assertThat(scene.getRoot()).isEqualTo(root);
            assertThat(scene.getWidth()).isEqualTo(400);
            assertThat(scene.getHeight()).isEqualTo(300);
        }

        @Test
        void constructor_shouldApplyFontSmoothingStyle() {
            javafx.scene.Parent root = new javafx.scene.layout.StackPane();

            GwScene scene = new GwScene(root);

            String style = root.getStyle();
            assertThat(style).contains("-fx-font-smoothing-type");
            assertThat(style).contains("gray");
        }

        @Test
        void constructor_shouldApplyFontFamilyStyle() {
            javafx.scene.Parent root = new javafx.scene.layout.StackPane();

            GwScene scene = new GwScene(root);

            String style = root.getStyle();
            assertThat(style).contains("-fx-font-family");
            assertThat(style).contains("Noto Sans SC");
        }
    }

    @Test
    void compilePropertyPattern_shouldReturnPatternForValidInput() throws Exception {
        var method = GwScene.class.getDeclaredMethod("compilePropertyPattern", String.class);
        method.setAccessible(true);

        java.util.regex.Pattern result = (java.util.regex.Pattern) method.invoke(null, "-fx-font-family");

        assertThat(result).isNotNull();
        assertThat(result.pattern()).contains("-fx-font-family");
    }

    @Test
    void compilePropertyPattern_shouldReturnNullForBlankInput() throws Exception {
        var method = GwScene.class.getDeclaredMethod("compilePropertyPattern", String.class);
        method.setAccessible(true);

        java.util.regex.Pattern result = (java.util.regex.Pattern) method.invoke(null, "   ");

        assertThat(result).isNull();
    }

    @Test
    void compilePropertyPattern_shouldReturnNullForNullInput() throws Exception {
        var method = GwScene.class.getDeclaredMethod("compilePropertyPattern", String.class);
        method.setAccessible(true);

        java.util.regex.Pattern result = (java.util.regex.Pattern) method.invoke(null, (Object) null);

        assertThat(result).isNull();
    }
}
