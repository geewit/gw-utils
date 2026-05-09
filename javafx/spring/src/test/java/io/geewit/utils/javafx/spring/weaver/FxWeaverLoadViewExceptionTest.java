package io.geewit.utils.javafx.spring.weaver;

import javafx.util.Callback;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.util.ResourceBundle;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for FxWeaver loadView throwing FxLoadException when view is missing.
 */
@DisplayName("FxWeaver LoadView Exception Tests")
class FxWeaverLoadViewExceptionTest {

    private Callback<Class<?>, Object> mockBeanFactory = mock(Callback.class);
    private Runnable mockCloseCommand = mock(Runnable.class);

    @Nested
    @DisplayName("loadView() throws FxLoadException")
    class LoadViewExceptions {

        @Test
        @DisplayName("should throw FxLoadException when view cannot be loaded")
        void shouldThrowFxLoadExceptionWhenViewCannotBeLoaded() throws Exception {
            FxWeaver fxWeaver = new FxWeaver(mockBeanFactory, mockCloseCommand);

            // Setup bean factory to return a controller
            TestController controller = new TestController();
            when(mockBeanFactory.call(TestController.class)).thenReturn(controller);

            // loadView without FXML will throw FxLoadException because getView() is empty
            assertThatThrownBy(() -> fxWeaver.loadView(TestController.class))
                    .isInstanceOf(FxLoadException.class)
                    .hasMessageContaining("Unable to resolve FXML view");
        }

        @Test
        @DisplayName("should throw FxLoadException for loadView with location")
        void shouldThrowFxLoadExceptionForLoadViewWithLocation() throws Exception {
            FxWeaver fxWeaver = new FxWeaver(mockBeanFactory, mockCloseCommand);

            TestController controller = new TestController();
            when(mockBeanFactory.call(TestController.class)).thenReturn(controller);

            // Non-existent FXML file location
            assertThatThrownBy(() -> fxWeaver.loadView(TestController.class, "/nonexistent.fxml"))
                    .isInstanceOf(FxLoadException.class);
        }

        @Test
        @DisplayName("should throw FxLoadException for loadView with location and bundle")
        void shouldThrowFxLoadExceptionForLoadViewWithLocationAndBundle() throws Exception {
            FxWeaver fxWeaver = new FxWeaver(mockBeanFactory, mockCloseCommand);
            ResourceBundle mockBundle = mock(ResourceBundle.class);

            TestController controller = new TestController();
            when(mockBeanFactory.call(TestController.class)).thenReturn(controller);

            assertThatThrownBy(() -> fxWeaver.loadView(TestController.class, "/nonexistent.fxml", mockBundle))
                    .isInstanceOf(FxLoadException.class);
        }
    }

    // Helper class
    static class TestController {
    }
}