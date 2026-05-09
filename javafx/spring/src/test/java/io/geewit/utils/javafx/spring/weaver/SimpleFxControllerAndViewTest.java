package io.geewit.utils.javafx.spring.weaver;

import javafx.scene.layout.VBox;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class SimpleFxControllerAndViewTest {

    @Test
    void ofController_withController() {
        TestController controller = new TestController();
        FxControllerAndView<TestController, VBox> result = SimpleFxControllerAndView.ofController(controller);
        assertNotNull(result);
        assertSame(controller, result.controller());
        assertTrue(result.getView().isEmpty());
    }

    @Test
    void of_withControllerAndView() {
        TestController controller = new TestController();
        VBox view = new VBox();
        FxControllerAndView<TestController, VBox> result = SimpleFxControllerAndView.of(controller, view);
        assertNotNull(result);
        assertSame(controller, result.controller());
        assertTrue(result.getView().isPresent());
        assertSame(view, result.getView().get());
    }

    @Test
    void getView_withNullView() {
        TestController controller = new TestController();
        FxControllerAndView<TestController, VBox> result = SimpleFxControllerAndView.ofController(controller);
        Optional<VBox> view = result.getView();
        assertTrue(view.isEmpty());
    }

    @Test
    void toString_withView() {
        TestController controller = new TestController();
        VBox view = new VBox();
        FxControllerAndView<TestController, VBox> result = SimpleFxControllerAndView.of(controller, view);
        String str = result.toString();
        assertTrue(str.contains("SimpleFxControllerAndView"));
        assertTrue(str.contains("controller="));
    }

    @Test
    void toString_withoutView() {
        TestController controller = new TestController();
        FxControllerAndView<TestController, VBox> result = SimpleFxControllerAndView.ofController(controller);
        String str = result.toString();
        assertTrue(str.contains("SimpleFxControllerAndView"));
        assertTrue(str.contains("controller="));
    }

    @Test
    void record_constructor() {
        TestController controller = new TestController();
        VBox view = new VBox();
        SimpleFxControllerAndView<TestController, VBox> result = new SimpleFxControllerAndView<>(controller, view);
        assertSame(controller, result.controller());
        assertSame(view, result.view());
    }

    public static class TestController {
    }
}
