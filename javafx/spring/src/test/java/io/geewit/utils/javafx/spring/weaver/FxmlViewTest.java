package io.geewit.utils.javafx.spring.weaver;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("FxmlView Tests")
class FxmlViewTest {

    @Nested
    @DisplayName("@FxmlView annotation")
    class AnnotationProperties {

        @Test
        @DisplayName("should have empty default value")
        void shouldHaveEmptyDefaultValue() {
            FxmlView annotation = TestController.class.getAnnotation(FxmlView.class);

            assertThat(annotation).isNotNull();
            assertThat(annotation.value()).isEmpty();
        }

        @Test
        @DisplayName("should have custom value when specified")
        void shouldHaveCustomValueWhenSpecified() {
            FxmlView annotation = CustomViewController.class.getAnnotation(FxmlView.class);

            assertThat(annotation).isNotNull();
            assertThat(annotation.value()).isEqualTo("custom.fxml");
        }
    }

    @Nested
    @DisplayName("annotation retention and target")
    class AnnotationMeta {

        @Test
        @DisplayName("should be retained at runtime")
        void shouldBeRetainedAtRuntime() {
            Retention retention = FxmlView.class.getAnnotation(Retention.class);

            assertThat(retention).isNotNull();
            assertThat(retention.value()).isEqualTo(RetentionPolicy.RUNTIME);
        }

        @Test
        @DisplayName("should target type only")
        void shouldTargetTypeOnly() {
            Target target = FxmlView.class.getAnnotation(Target.class);

            assertThat(target).isNotNull();
            assertThat(target.value()).containsExactly(ElementType.TYPE);
        }

        @Test
        @DisplayName("should be documented")
        void shouldBeDocumented() {
            java.lang.annotation.Documented documented = FxmlView.class.getAnnotation(java.lang.annotation.Documented.class);

            assertThat(documented).isNotNull();
        }
    }

    // Test helper classes
    @FxmlView
    static class TestController {
    }

    @FxmlView("custom.fxml")
    static class CustomViewController {
    }
}