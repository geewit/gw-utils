package io.geewit.utils.javafx.spring;

import io.geewit.utils.javafx.spring.weaver.FxControllerAndView;
import io.geewit.utils.javafx.spring.weaver.FxWeaver;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.core.ResolvableType;
import org.springframework.core.MethodParameter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InjectionPointLazyFxControllerAndViewResolverTest {

    @Mock
    private FxWeaver fxWeaver;

    @Mock
    private InjectionPoint injectionPoint;

    @Test
    void resolve_withMethodParameter() throws NoSuchMethodException {
        Method method = TestComponent.class.getMethod("setControllerAndView", FxControllerAndView.class);
        MethodParameter methodParameter = new MethodParameter(method, 0);

        when(injectionPoint.getMethodParameter()).thenReturn(methodParameter);

        TestController controller = new TestController();
        VBox view = new VBox();
        when(fxWeaver.load(TestController.class, (ResourceBundle) null))
                .thenReturn(new io.geewit.utils.javafx.spring.weaver.SimpleFxControllerAndView<>(controller, view));

        InjectionPointLazyFxControllerAndViewResolver resolver =
                new InjectionPointLazyFxControllerAndViewResolver(fxWeaver, null);

        FxControllerAndView<TestController, VBox> result = resolver.resolve(injectionPoint);
        assertNotNull(result);
        assertNotNull(result.controller());
        assertNotNull(result.getView().orElse(null));
    }

    @Test
    void resolve_withField() throws NoSuchFieldException {
        Field field = TestComponent.class.getDeclaredField("controllerAndView");

        when(injectionPoint.getField()).thenReturn(field);

        TestController controller = new TestController();
        VBox view = new VBox();
        when(fxWeaver.load(TestController.class, (ResourceBundle) null))
                .thenReturn(new io.geewit.utils.javafx.spring.weaver.SimpleFxControllerAndView<>(controller, view));

        InjectionPointLazyFxControllerAndViewResolver resolver =
                new InjectionPointLazyFxControllerAndViewResolver(fxWeaver, null);

        FxControllerAndView<TestController, VBox> result = resolver.resolve(injectionPoint);
        assertNotNull(result);
        assertNotNull(result.controller());
    }

    @Test
    void resolve_withNullResolvableType() {
        InjectionPointLazyFxControllerAndViewResolver resolver =
                new InjectionPointLazyFxControllerAndViewResolver(fxWeaver, null);

        assertThrows(IllegalArgumentException.class, () -> resolver.resolve(injectionPoint));
    }

    @Test
    void resolve_withNullControllerClass() throws NoSuchMethodException {
        Method method = TestComponent.class.getMethod("setRawControllerAndView", FxControllerAndView.class);
        MethodParameter methodParameter = new MethodParameter(method, 0);

        when(injectionPoint.getMethodParameter()).thenReturn(methodParameter);

        InjectionPointLazyFxControllerAndViewResolver resolver =
                new InjectionPointLazyFxControllerAndViewResolver(fxWeaver, null);

        assertThrows(IllegalArgumentException.class, () -> resolver.resolve(injectionPoint));
    }

    @Test
    void resolve_withResourceBundle() throws NoSuchFieldException {
        Field field = TestComponent.class.getDeclaredField("controllerAndView");
        ResourceBundle bundle = ResourceBundle.getBundle("i18n/messages");

        when(injectionPoint.getField()).thenReturn(field);

        TestController controller = new TestController();
        VBox view = new VBox();
        when(fxWeaver.load(TestController.class, bundle))
                .thenReturn(new io.geewit.utils.javafx.spring.weaver.SimpleFxControllerAndView<>(controller, view));

        InjectionPointLazyFxControllerAndViewResolver resolver =
                new InjectionPointLazyFxControllerAndViewResolver(fxWeaver, bundle);

        FxControllerAndView<TestController, VBox> result = resolver.resolve(injectionPoint);
        assertNotNull(result);
        assertNotNull(result.controller());
    }

    // Test component classes
    public static class TestController {
    }

    public static class TestComponent {
        private FxControllerAndView<TestController, VBox> controllerAndView;

        public void setControllerAndView(FxControllerAndView<TestController, VBox> controllerAndView) {
            this.controllerAndView = controllerAndView;
        }

        public void setRawControllerAndView(FxControllerAndView<?, ?> controllerAndView) {
            this.controllerAndView = (FxControllerAndView<TestController, VBox>) controllerAndView;
        }
    }
}
