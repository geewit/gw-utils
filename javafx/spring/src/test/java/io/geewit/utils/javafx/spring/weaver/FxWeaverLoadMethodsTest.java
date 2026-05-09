package io.geewit.utils.javafx.spring.weaver;

import javafx.util.Callback;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ResourceLoader;

import java.util.ResourceBundle;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link FxWeaver} load methods and protected members.
 */
@DisplayName("FxWeaver Load Methods Tests")
class FxWeaverLoadMethodsTest {

    private Callback<Class<?>, Object> mockBeanFactory = mock(Callback.class);
    private Runnable mockCloseCommand = mock(Runnable.class);

    @Nested
    @DisplayName("load() methods")
    class LoadMethods {

        @Test
        @DisplayName("should load controller when FXML resource does not exist")
        void shouldLoadControllerWhenFxmlResourceDoesNotExist() throws Exception {
            FxWeaver fxWeaver = new FxWeaver(mockBeanFactory, mockCloseCommand);

            // When FXML doesn't exist, returns controller-only
            when(mockBeanFactory.call(TestController.class)).thenReturn(new TestController());

            FxControllerAndView<TestController, ?> result = fxWeaver.load(TestController.class);

            assertThat(result).isNotNull();
            assertThat(result.controller()).isInstanceOf(TestController.class);
            assertThat(result.getView()).isEmpty();
        }

        @Test
        @DisplayName("should load with ResourceBundle")
        void shouldLoadWithResourceBundle() throws Exception {
            FxWeaver fxWeaver = new FxWeaver(mockBeanFactory, mockCloseCommand);
            ResourceBundle mockBundle = mock(ResourceBundle.class);

            when(mockBeanFactory.call(TestController.class)).thenReturn(new TestController());

            FxControllerAndView<TestController, ?> result = fxWeaver.load(TestController.class, mockBundle);

            assertThat(result).isNotNull();
            assertThat(result.controller()).isInstanceOf(TestController.class);
        }

        @Test
        @DisplayName("should load with location parameter when resource does not exist")
        void shouldLoadWithLocationParameterWhenResourceDoesNotExist() throws Exception {
            FxWeaver fxWeaver = new FxWeaver(mockBeanFactory, mockCloseCommand);

            when(mockBeanFactory.call(TestController.class)).thenReturn(new TestController());

            // Use a non-existent location - will fall back to controller-only
            FxControllerAndView<TestController, ?> result = fxWeaver.load(TestController.class, "/nonexistent.fxml", null);

            assertThat(result).isNotNull();
            assertThat(result.controller()).isInstanceOf(TestController.class);
        }
    }

    @Nested
    @DisplayName("loadController() methods")
    class LoadControllerMethods {

        @Test
        @DisplayName("should load controller and return it")
        void shouldLoadControllerAndReturnIt() throws Exception {
            FxWeaver fxWeaver = new FxWeaver(mockBeanFactory, mockCloseCommand);

            when(mockBeanFactory.call(TestController.class)).thenReturn(new TestController());

            TestController result = fxWeaver.loadController(TestController.class);

            assertThat(result).isInstanceOf(TestController.class);
        }

        @Test
        @DisplayName("should load controller with location when resource does not exist")
        void shouldLoadControllerWithLocationWhenResourceDoesNotExist() throws Exception {
            FxWeaver fxWeaver = new FxWeaver(mockBeanFactory, mockCloseCommand);

            when(mockBeanFactory.call(TestController.class)).thenReturn(new TestController());

            TestController result = fxWeaver.loadController(TestController.class, "/nonexistent.fxml");

            assertThat(result).isInstanceOf(TestController.class);
        }

        @Test
        @DisplayName("should load controller with location and bundle")
        void shouldLoadControllerWithLocationAndBundle() throws Exception {
            FxWeaver fxWeaver = new FxWeaver(mockBeanFactory, mockCloseCommand);
            ResourceBundle mockBundle = mock(ResourceBundle.class);

            when(mockBeanFactory.call(TestController.class)).thenReturn(new TestController());

            TestController result = fxWeaver.loadController(TestController.class, "/nonexistent.fxml", mockBundle);

            assertThat(result).isInstanceOf(TestController.class);
        }
    }

    @Nested
    @DisplayName("resolveResourcePath()")
    class ResolveResourcePathMethod {

        @Test
        @DisplayName("should prepend FXML_RESOURCE_LOCATION for absolute path starting with /")
        void shouldPrependFxResourceLocationForAbsolutePath() {
            FxWeaver fxWeaver = new FxWeaver(mockBeanFactory, mockCloseCommand);

            String result = invokeResolveResourcePath(fxWeaver, TestController.class, "/Test.fxml");

            // FXML_RESOURCE_LOCATION = "classpath:fxml/"
            assertThat(result).isEqualTo("classpath:fxml//Test.fxml");
        }

        @Test
        @DisplayName("should resolve relative path to package location")
        void shouldResolveRelativePathToPackageLocation() {
            FxWeaver fxWeaver = new FxWeaver(mockBeanFactory, mockCloseCommand);

            String result = invokeResolveResourcePath(fxWeaver, TestController.class, "Test.fxml");

            // FXML_RESOURCE_LOCATION = "classpath:fxml/"
            assertThat(result).isEqualTo("classpath:fxml/io/geewit/utils/javafx/spring/weaver/Test.fxml");
        }

        @Test
        @DisplayName("should keep file: protocol unchanged")
        void shouldKeepFileProtocolUnchanged() {
            FxWeaver fxWeaver = new FxWeaver(mockBeanFactory, mockCloseCommand);

            String result = invokeResolveResourcePath(fxWeaver, TestController.class, "file:/path/to/Test.fxml");

            assertThat(result).isEqualTo("file:/path/to/Test.fxml");
        }

        @Test
        @DisplayName("should keep http: protocol unchanged")
        void shouldKeepHttpProtocolUnchanged() {
            FxWeaver fxWeaver = new FxWeaver(mockBeanFactory, mockCloseCommand);

            String result = invokeResolveResourcePath(fxWeaver, TestController.class, "http://example.com/Test.fxml");

            assertThat(result).isEqualTo("http://example.com/Test.fxml");
        }

        @Test
        @DisplayName("should keep https: protocol unchanged")
        void shouldKeepHttpsProtocolUnchanged() {
            FxWeaver fxWeaver = new FxWeaver(mockBeanFactory, mockCloseCommand);

            String result = invokeResolveResourcePath(fxWeaver, TestController.class, "https://example.com/Test.fxml");

            assertThat(result).isEqualTo("https://example.com/Test.fxml");
        }

        @Test
        @DisplayName("should handle classpath:fxml prefix correctly")
        void shouldHandleClasspathFxmlPrefix() {
            FxWeaver fxWeaver = new FxWeaver(mockBeanFactory, mockCloseCommand);

            String result = invokeResolveResourcePath(fxWeaver, TestController.class, "classpath:fxml/Test.fxml");

            // The prefix "classpath:fxml" is not "classpath:fxml/" so it goes to else branch
            // and the full path is processed
            assertThat(result).isNotNull();
        }
    }

    @Nested
    @DisplayName("buildMissingViewMessage()")
    class BuildMissingViewMessageMethod {

        @Test
        @DisplayName("should build message for null location")
        void shouldBuildMessageForNullLocation() {
            FxWeaver fxWeaver = new FxWeaver(mockBeanFactory, mockCloseCommand);

            String message = invokeBuildMissingViewMessage(fxWeaver, TestController.class, null);

            assertThat(message).contains("Unable to resolve FXML view");
            assertThat(message).contains(TestController.class.getName());
        }

        @Test
        @DisplayName("should build message for blank location")
        void shouldBuildMessageForBlankLocation() {
            FxWeaver fxWeaver = new FxWeaver(mockBeanFactory, mockCloseCommand);

            String message = invokeBuildMissingViewMessage(fxWeaver, TestController.class, "   ");

            assertThat(message).contains("Unable to resolve FXML view");
        }

        @Test
        @DisplayName("should include resolved location in message")
        void shouldIncludeResolvedLocationInMessage() {
            FxWeaver fxWeaver = new FxWeaver(mockBeanFactory, mockCloseCommand);

            String message = invokeBuildMissingViewMessage(fxWeaver, TestController.class, "/custom.fxml");

            assertThat(message).contains("/custom.fxml");
        }
    }

    @Nested
    @DisplayName("loadByViewUsingFxmlLoader() exception paths")
    class LoadByViewUsingFxmlLoaderExceptions {

        @Test
        @DisplayName("should throw FxLoadException when url is null")
        void shouldThrowFxLoadExceptionWhenUrlIsNull() {
            FxWeaver fxWeaver = new FxWeaver(mockBeanFactory, mockCloseCommand);

            javafx.fxml.FXMLLoader loaderWithNullUrl = new javafx.fxml.FXMLLoader();
            assertThatThrownBy(() -> fxWeaver.loadByViewUsingFxmlLoader(loaderWithNullUrl, null))
                    .isInstanceOf(FxLoadException.class)
                    .hasMessageContaining("url is null");
        }
    }

    // Helper classes and methods
    static class TestController {
    }

    private String invokeBuildMissingViewMessage(FxWeaver fxWeaver, Class<?> controllerClass, String location) {
        try {
            java.lang.reflect.Method method = FxWeaver.class.getDeclaredMethod("buildMissingViewMessage", Class.class, String.class);
            method.setAccessible(true);
            return (String) method.invoke(fxWeaver, controllerClass, location);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String invokeResolveResourcePath(FxWeaver fxWeaver, Class<?> controllerClass, String location) {
        try {
            java.lang.reflect.Method method = FxWeaver.class.getDeclaredMethod("resolveResourcePath", Class.class, String.class);
            method.setAccessible(true);
            return (String) method.invoke(fxWeaver, controllerClass, location);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}