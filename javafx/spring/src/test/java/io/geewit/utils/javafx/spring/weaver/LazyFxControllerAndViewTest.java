package io.geewit.utils.javafx.spring.weaver;

import javafx.scene.layout.VBox;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

class LazyFxControllerAndViewTest {

    @Test
    void controller_lazyLoad() {
        TestController controller = new TestController();
        VBox view = new VBox();
        Supplier<FxControllerAndView<TestController, VBox>> supplier = () ->
                SimpleFxControllerAndView.of(controller, view);

        LazyFxControllerAndView<TestController, VBox> lazy = new LazyFxControllerAndView<>(supplier);
        assertNotNull(lazy.controller());
        assertSame(controller, lazy.controller());
    }

    @Test
    void getView_lazyLoad() {
        TestController controller = new TestController();
        VBox view = new VBox();
        Supplier<FxControllerAndView<TestController, VBox>> supplier = () ->
                SimpleFxControllerAndView.of(controller, view);

        LazyFxControllerAndView<TestController, VBox> lazy = new LazyFxControllerAndView<>(supplier);
        Optional<VBox> result = lazy.getView();
        assertTrue(result.isPresent());
        assertSame(view, result.get());
    }

    @Test
    void toString_beforeInit() {
        Supplier<FxControllerAndView<TestController, VBox>> supplier = () ->
                SimpleFxControllerAndView.of(new TestController(), new VBox());

        LazyFxControllerAndView<TestController, VBox> lazy = new LazyFxControllerAndView<>(supplier);
        String str = lazy.toString();
        assertTrue(str.contains("Not initialized"));
    }

    @Test
    void toString_afterInit() {
        TestController controller = new TestController();
        VBox view = new VBox();
        Supplier<FxControllerAndView<TestController, VBox>> supplier = () ->
                SimpleFxControllerAndView.of(controller, view);

        LazyFxControllerAndView<TestController, VBox> lazy = new LazyFxControllerAndView<>(supplier);
        lazy.controller(); // trigger init
        String str = lazy.toString();
        assertTrue(str.contains("SimpleFxControllerAndView"));
    }

    @Test
    void initOrGet_returnsSameInstance() {
        TestController controller = new TestController();
        VBox view = new VBox();
        Supplier<FxControllerAndView<TestController, VBox>> supplier = () ->
                SimpleFxControllerAndView.of(controller, view);

        LazyFxControllerAndView<TestController, VBox> lazy = new LazyFxControllerAndView<>(supplier);
        FxControllerAndView<TestController, VBox> first = lazy.initOrGet();
        FxControllerAndView<TestController, VBox> second = lazy.initOrGet();
        assertSame(first, second);
    }

    public static class TestController {
    }
}
