package io.geewit.utils.javafx.spring.weaver;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("FxControllerAndView Tests")
class FxControllerAndViewTest {

    @Nested
    @DisplayName("FxControllerAndView interface")
    class InterfaceTests {

        @Test
        @DisplayName("should define controller() method")
        void shouldDefineControllerMethod() {
            FxControllerAndView<String, javafx.scene.Node> cav = SimpleFxControllerAndView.ofController("test");

            assertThat(cav.controller()).isEqualTo("test");
        }

        @Test
        @DisplayName("should define getView() method returning Optional")
        void shouldDefineGetViewMethodReturningOptional() {
            FxControllerAndView<String, javafx.scene.Node> cav = SimpleFxControllerAndView.ofController("test");

            assertThat(cav.getView()).isEmpty();
        }
    }
}