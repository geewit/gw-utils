package io.geewit.utils.javafx.spring.weaver;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ResourceLoader;

import java.lang.reflect.Method;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FxWeaverMockTest {

    @Mock
    private javafx.util.Callback<Class<?>, Object> beanFactory;

    @Mock
    private Runnable closeCommand;

    @Mock
    private ResourceLoader resourceLoader;

    private FxWeaver fxWeaver;

    @BeforeEach
    void setUp() {
        fxWeaver = new FxWeaver(beanFactory, closeCommand, resourceLoader);
    }

    @Test
    void constructor_withTwoArgs() {
        FxWeaver weaver = new FxWeaver(beanFactory, closeCommand);
        assertNotNull(weaver);
    }

    @Test
    void constructor_withThreeArgs() {
        assertNotNull(fxWeaver);
    }

    @Test
    void getBean() {
        TestController controller = new TestController();
        when(beanFactory.call(TestController.class)).thenReturn(controller);

        TestController result = fxWeaver.getBean(TestController.class);
        assertSame(controller, result);
    }

    @Test
    void buildFxmlReference_withAnnotation() {
        String ref = fxWeaver.buildFxmlReference(AnnotatedTestController.class);
        assertEquals("custom/TestView.fxml", ref);
    }

    @Test
    void buildFxmlReference_withoutAnnotation() {
        String ref = fxWeaver.buildFxmlReference(TestController.class);
        assertEquals("TestController.fxml", ref);
    }

    @Test
    void buildFxmlReference_withEmptyAnnotation() {
        String ref = fxWeaver.buildFxmlReference(EmptyAnnotatedController.class);
        assertEquals("EmptyAnnotatedController.fxml", ref);
    }

    @Test
    void resolveResourcePath_withFxmlPrefix() {
        String path = invokeResolveResourcePath(TestController.class, "fxml/test.fxml");
        assertEquals("classpath:fxml/io/geewit/utils/javafx/spring/weaver/fxml/test.fxml", path);
    }

    @Test
    void resolveResourcePath_withAbsolutePath() {
        String path = invokeResolveResourcePath(TestController.class, "/test.fxml");
        assertEquals("classpath:fxml//test.fxml", path);
    }

    @Test
    void resolveResourcePath_withRelativePath() {
        String path = invokeResolveResourcePath(TestController.class, "test.fxml");
        assertEquals("classpath:fxml/io/geewit/utils/javafx/spring/weaver/test.fxml", path);
    }

    @Test
    void resolveResourcePath_withHttpUrl() {
        String path = invokeResolveResourcePath(TestController.class, "http://example.com/test.fxml");
        assertEquals("http://example.com/test.fxml", path);
    }

    @Test
    void resolveResourcePath_withHttpsUrl() {
        String path = invokeResolveResourcePath(TestController.class, "https://example.com/test.fxml");
        assertEquals("https://example.com/test.fxml", path);
    }

    @Test
    void resolveResourcePath_withFileUrl() {
        String path = invokeResolveResourcePath(TestController.class, "file:/test.fxml");
        assertEquals("file:/test.fxml", path);
    }

    @Test
    void loadView_withNonExistentResource() {
        org.springframework.core.io.Resource resource = mock(org.springframework.core.io.Resource.class);
        when(resourceLoader.getResource(anyString())).thenReturn(resource);
        when(resource.exists()).thenReturn(false);
        when(beanFactory.call(TestController.class)).thenReturn(new TestController());

        // When resource doesn't exist and no view is created, loadView throws
        assertThrows(FxLoadException.class, () -> fxWeaver.loadView(TestController.class));
    }

    @Test
    void loadView_withLocation_nonExistentResource() {
        org.springframework.core.io.Resource resource = mock(org.springframework.core.io.Resource.class);
        when(resourceLoader.getResource(anyString())).thenReturn(resource);
        when(resource.exists()).thenReturn(false);
        when(beanFactory.call(TestController.class)).thenReturn(new TestController());

        assertThrows(FxLoadException.class, () -> fxWeaver.loadView(TestController.class, "TestController.fxml"));
    }

    @Test
    void loadView_withResourceBundle_nonExistentResource() {
        org.springframework.core.io.Resource resource = mock(org.springframework.core.io.Resource.class);
        when(resourceLoader.getResource(anyString())).thenReturn(resource);
        when(resource.exists()).thenReturn(false);
        when(beanFactory.call(TestController.class)).thenReturn(new TestController());

        assertThrows(FxLoadException.class, () -> fxWeaver.loadView(TestController.class, ResourceBundle.getBundle("i18n/messages")));
    }

    @Test
    void loadController_withNonExistentResource() {
        org.springframework.core.io.Resource resource = mock(org.springframework.core.io.Resource.class);
        when(resourceLoader.getResource(anyString())).thenReturn(resource);
        when(resource.exists()).thenReturn(false);
        when(beanFactory.call(TestController.class)).thenReturn(new TestController());

        TestController controller = fxWeaver.loadController(TestController.class);
        assertNotNull(controller);
    }

    @Test
    void loadController_withLocation_nonExistentResource() {
        org.springframework.core.io.Resource resource = mock(org.springframework.core.io.Resource.class);
        when(resourceLoader.getResource(anyString())).thenReturn(resource);
        when(resource.exists()).thenReturn(false);
        when(beanFactory.call(TestController.class)).thenReturn(new TestController());

        TestController controller = fxWeaver.loadController(TestController.class, "TestController.fxml");
        assertNotNull(controller);
    }

    @Test
    void loadController_withResourceBundle_nonExistentResource() {
        org.springframework.core.io.Resource resource = mock(org.springframework.core.io.Resource.class);
        when(resourceLoader.getResource(anyString())).thenReturn(resource);
        when(resource.exists()).thenReturn(false);
        when(beanFactory.call(TestController.class)).thenReturn(new TestController());

        TestController controller = fxWeaver.loadController(TestController.class, ResourceBundle.getBundle("i18n/messages"));
        assertNotNull(controller);
    }

    @Test
    void load_withNullResourceBundle_nonExistentResource() {
        org.springframework.core.io.Resource resource = mock(org.springframework.core.io.Resource.class);
        when(resourceLoader.getResource(anyString())).thenReturn(resource);
        when(resource.exists()).thenReturn(false);
        when(beanFactory.call(TestController.class)).thenReturn(new TestController());

        FxControllerAndView<TestController, javafx.scene.layout.VBox> result = fxWeaver.load(TestController.class);
        assertNotNull(result);
        assertNotNull(result.controller());
    }

    @Test
    void load_withResourceBundle_nonExistentResource() {
        org.springframework.core.io.Resource resource = mock(org.springframework.core.io.Resource.class);
        when(resourceLoader.getResource(anyString())).thenReturn(resource);
        when(resource.exists()).thenReturn(false);
        when(beanFactory.call(TestController.class)).thenReturn(new TestController());

        FxControllerAndView<TestController, javafx.scene.layout.VBox> result = fxWeaver.load(TestController.class, ResourceBundle.getBundle("i18n/messages"));
        assertNotNull(result);
    }

    @Test
    void shutdown() {
        doNothing().when(closeCommand).run();
        assertDoesNotThrow(() -> fxWeaver.shutdown());
    }

    @Test
    void shutdown_withException() {
        doThrow(new RuntimeException("test")).when(closeCommand).run();
        assertDoesNotThrow(() -> fxWeaver.shutdown());
    }

    @Test
    void loadView_withNullLocation_nonExistentResource() {
        org.springframework.core.io.Resource resource = mock(org.springframework.core.io.Resource.class);
        when(resourceLoader.getResource(anyString())).thenReturn(resource);
        when(resource.exists()).thenReturn(false);
        when(beanFactory.call(TestController.class)).thenReturn(new TestController());

        FxControllerAndView<TestController, javafx.scene.layout.VBox> result = fxWeaver.load(TestController.class);
        assertNotNull(result);
    }

    @Test
    void buildMissingViewMessage_withNullLocation() {
        String message = invokeBuildMissingViewMessage(TestController.class, null);
        assertTrue(message.contains("TestController"));
        assertTrue(message.contains("Checked location"));
    }

    @Test
    void buildMissingViewMessage_withLocation() {
        String message = invokeBuildMissingViewMessage(TestController.class, "TestController.fxml");
        assertTrue(message.contains("TestController"));
        assertTrue(message.contains("TestController.fxml"));
    }

    @Test
    void buildMissingViewMessage_withBlankLocation() {
        String message = invokeBuildMissingViewMessage(TestController.class, "   ");
        assertTrue(message.contains("TestController"));
        assertTrue(message.contains("No view location could be inferred"));
    }

    @Test
    void loadView_withExistingResource() throws Exception {
        TestController controller = new TestController();
        javafx.scene.layout.VBox view = new javafx.scene.layout.VBox();
        org.springframework.core.io.Resource resource = mock(org.springframework.core.io.Resource.class);
        when(resourceLoader.getResource(anyString())).thenReturn(resource);
        when(resource.exists()).thenReturn(true);
        java.net.URL url = new java.net.URL("file:///tmp/test.fxml");
        when(resource.getURL()).thenReturn(url);

        FxWeaver spyWeaver = spy(fxWeaver);
        doReturn(SimpleFxControllerAndView.of(controller, view))
                .when(spyWeaver).loadByViewUsingFxmlLoader(any(javafx.fxml.FXMLLoader.class), any());

        javafx.scene.layout.VBox result = spyWeaver.loadView(TestController.class);
        assertNotNull(result);
        assertSame(view, result);
    }

    @Test
    void loadView_withResourceBundle_existingResource() throws Exception {
        TestController controller = new TestController();
        javafx.scene.layout.VBox view = new javafx.scene.layout.VBox();
        org.springframework.core.io.Resource resource = mock(org.springframework.core.io.Resource.class);
        when(resourceLoader.getResource(anyString())).thenReturn(resource);
        when(resource.exists()).thenReturn(true);
        java.net.URL url = new java.net.URL("file:///tmp/test.fxml");
        when(resource.getURL()).thenReturn(url);

        FxWeaver spyWeaver = spy(fxWeaver);
        doReturn(SimpleFxControllerAndView.of(controller, view))
                .when(spyWeaver).loadByViewUsingFxmlLoader(any(javafx.fxml.FXMLLoader.class), any());

        javafx.scene.layout.VBox result = spyWeaver.loadView(TestController.class, ResourceBundle.getBundle("i18n/messages"));
        assertNotNull(result);
    }

    @Test
    void loadView_withLocation_existingResource() throws Exception {
        TestController controller = new TestController();
        javafx.scene.layout.VBox view = new javafx.scene.layout.VBox();
        org.springframework.core.io.Resource resource = mock(org.springframework.core.io.Resource.class);
        when(resourceLoader.getResource(anyString())).thenReturn(resource);
        when(resource.exists()).thenReturn(true);
        java.net.URL url = new java.net.URL("file:///tmp/test.fxml");
        when(resource.getURL()).thenReturn(url);

        FxWeaver spyWeaver = spy(fxWeaver);
        doReturn(SimpleFxControllerAndView.of(controller, view))
                .when(spyWeaver).loadByViewUsingFxmlLoader(any(javafx.fxml.FXMLLoader.class), any());

        javafx.scene.layout.VBox result = spyWeaver.loadView(TestController.class, "TestController.fxml");
        assertNotNull(result);
    }

    @Test
    void loadByViewUsingFxmlLoader_withNullLocation() {
        javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader();
        assertThrows(FxLoadException.class, () -> fxWeaver.loadByViewUsingFxmlLoader(loader, null));
    }

    @Test
    void loadByViewUsingFxmlLoader_withIoException() throws Exception {
        java.net.URL url = new java.net.URL("file:///nonexistent.fxml");
        javafx.fxml.FXMLLoader loader = mock(javafx.fxml.FXMLLoader.class);
        when(loader.getLocation()).thenReturn(url);

        FxLoadException exception = assertThrows(FxLoadException.class, () -> fxWeaver.loadByViewUsingFxmlLoader(loader, null));
        assertTrue(exception.getMessage().contains("Unable to load FXML file"));
    }

    @Test
    void load_withIOExceptionFromResource() throws Exception {
        org.springframework.core.io.Resource resource = mock(org.springframework.core.io.Resource.class);
        when(resourceLoader.getResource(anyString())).thenReturn(resource);
        when(resource.exists()).thenReturn(true);
        when(resource.getDescription()).thenReturn("test resource");
        when(resource.getURL()).thenThrow(new java.io.IOException("test io error"));

        FxLoadException exception = assertThrows(FxLoadException.class, () -> fxWeaver.loadView(TestController.class));
        assertTrue(exception.getMessage().contains("Unable to load FXML file"));
    }

    // Helper methods to access protected/private methods
    private String invokeResolveResourcePath(Class<?> controllerClass, String location) {
        try {
            Method method = FxWeaver.class.getDeclaredMethod("resolveResourcePath", Class.class, String.class);
            method.setAccessible(true);
            return (String) method.invoke(fxWeaver, controllerClass, location);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String invokeBuildMissingViewMessage(Class<?> controllerClass, String location) {
        try {
            Method method = FxWeaver.class.getDeclaredMethod("buildMissingViewMessage", Class.class, String.class);
            method.setAccessible(true);
            return (String) method.invoke(fxWeaver, controllerClass, location);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Test helper classes
    public static class TestController {
    }

    @FxmlView("custom/TestView.fxml")
    public static class AnnotatedTestController {
    }

    @FxmlView("")
    public static class EmptyAnnotatedController {
    }
}
