package io.geewit.utils.javafx.spring.weaver;

import javafx.util.Callback;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ResourceLoader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link FxWeaver}.
 */
@DisplayName("FxWeaver Tests")
class FxWeaverTest {

    private Callback<Class<?>, Object> mockBeanFactory = mock(Callback.class);
    private Runnable mockCloseCommand = mock(Runnable.class);
    private ResourceLoader mockResourceLoader = mock(ResourceLoader.class);

    @Nested
    @DisplayName("constructors")
    class Constructors {

        @Test
        @DisplayName("should create with beanFactory and closeCommand only")
        void shouldCreateWithBeanFactoryAndCloseCommandOnly() {
            FxWeaver result = new FxWeaver(mockBeanFactory, mockCloseCommand);

            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("should create with all three parameters")
        void shouldCreateWithAllThreeParameters() {
            FxWeaver result = new FxWeaver(mockBeanFactory, mockCloseCommand, mockResourceLoader);

            assertThat(result).isNotNull();
        }
    }

    @Nested
    @DisplayName("getBean()")
    class GetBeanMethod {

        @Test
        @DisplayName("should return bean from factory")
        void shouldReturnBeanFromFactory() {
            FxWeaver fxWeaver = new FxWeaver(mockBeanFactory, mockCloseCommand);
            String expectedBean = "TestBean";
            when(mockBeanFactory.call(String.class)).thenReturn(expectedBean);

            String result = fxWeaver.getBean(String.class);

            assertThat(result).isEqualTo(expectedBean);
            verify(mockBeanFactory).call(String.class);
        }

        @Test
        @DisplayName("should cast bean to requested type")
        void shouldCastBeanToRequestedType() {
            FxWeaver fxWeaver = new FxWeaver(mockBeanFactory, mockCloseCommand);
            Integer expectedBean = 42;
            when(mockBeanFactory.call(Integer.class)).thenReturn(expectedBean);

            Integer result = fxWeaver.getBean(Integer.class);

            assertThat(result).isEqualTo(expectedBean);
        }
    }

    @Nested
    @DisplayName("buildFxmlReference()")
    class BuildFxmlReferenceMethod {

        @Test
        @DisplayName("should return annotation value when present and not empty")
        void shouldReturnAnnotationValueWhenPresentAndNotEmpty() {
            FxWeaver fxWeaver = new FxWeaver(mockBeanFactory, mockCloseCommand);

            String result = fxWeaver.buildFxmlReference(AnnotatedController.class);

            assertThat(result).isEqualTo("custom.fxml");
        }

        @Test
        @DisplayName("should return simple class name plus extension when no annotation")
        void shouldReturnSimpleClassNamePlusExtensionWhenNoAnnotation() {
            FxWeaver fxWeaver = new FxWeaver(mockBeanFactory, mockCloseCommand);

            String result = fxWeaver.buildFxmlReference(UnannotatedController.class);

            assertThat(result).isEqualTo("UnannotatedController.fxml");
        }

        @Test
        @DisplayName("should return simple class name plus extension when annotation value is empty")
        void shouldReturnSimpleClassNamePlusExtensionWhenAnnotationValueIsEmpty() {
            FxWeaver fxWeaver = new FxWeaver(mockBeanFactory, mockCloseCommand);

            String result = fxWeaver.buildFxmlReference(EmptyAnnotationController.class);

            assertThat(result).isEqualTo("EmptyAnnotationController.fxml");
        }
    }

    @Nested
    @DisplayName("shutdown()")
    class ShutdownMethod {

        @Test
        @DisplayName("should call closeCommand during shutdown")
        void shouldCallCloseCommandDuringShutdown() {
            Runnable mockClose = mock(Runnable.class);
            FxWeaver fxWeaver = new FxWeaver(mockBeanFactory, mockClose);

            Exception thrownByPlatformExit = null;
            // In headless environment, Platform.exit() may throw
            try {
                fxWeaver.shutdown();
            } catch (Exception e) {
                thrownByPlatformExit = e;
            }

            // Verify that close was called (even if Platform.exit threw)
            verify(mockClose).run();

            // If an exception was thrown, it should be related to Platform.exit not closeCommand
            if (thrownByPlatformExit != null) {
                // The exception is expected in headless environment
                // closeCommand was already verified above
            }
        }
    }

    // Test helper classes
    @FxmlView("custom.fxml")
    static class AnnotatedController {
    }

    static class UnannotatedController {
    }

    @FxmlView("")
    static class EmptyAnnotationController {
    }
}