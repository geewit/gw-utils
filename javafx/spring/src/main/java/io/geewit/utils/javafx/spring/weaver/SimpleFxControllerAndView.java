package io.geewit.utils.javafx.spring.weaver;

import javafx.scene.Node;
import org.jspecify.annotations.NullUnmarked;

import java.util.Optional;

/**
 * SimpleFxControllerAndView is a container class for Controller beans and their corresponding Views.
 *
 * @author Rene Gielen
 */
public record SimpleFxControllerAndView<C, V extends Node>(C controller, V view) implements FxControllerAndView<C, V> {

    public static <C, V extends Node> FxControllerAndView<C, V> ofController(C controller) {
        return new SimpleFxControllerAndView<>(controller, null);
    }

    public static <C, V extends Node> FxControllerAndView<C, V> of(C controller,
                                                                   V view) {
        return new SimpleFxControllerAndView<>(controller, view);
    }

    @Override
    public Optional<V> getView() {
        return Optional.ofNullable(view);
    }

    @NullUnmarked
    @Override
    public String toString() {
        return "SimpleFxControllerAndView {" +
                "controller=" + controller +
                ", view=" + view +
                '}';
    }
}
