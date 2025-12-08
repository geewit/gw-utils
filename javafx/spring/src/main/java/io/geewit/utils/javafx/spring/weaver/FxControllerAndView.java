package io.geewit.utils.javafx.spring.weaver;

import javafx.scene.Node;

import java.util.Optional;

/**
 * FxControllerAndView is a container class for Controller beans and their corresponding Views.
 *
 * @author Rene Gielen
 */
public interface FxControllerAndView<C, V extends Node> {

    C controller();

    Optional<V> getView();

}
