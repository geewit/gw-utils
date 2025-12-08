package io.geewit.utils.javafx.spring;

import io.geewit.utils.javafx.spring.weaver.FxControllerAndView;
import io.geewit.utils.javafx.spring.weaver.FxWeaver;
import io.geewit.utils.javafx.spring.weaver.LazyFxControllerAndView;
import javafx.scene.Node;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.core.ResolvableType;

import java.util.Optional;
import java.util.ResourceBundle;

/**
 * This class helps to create a generic {@link FxControllerAndView} bean factory that can be used for direct injection
 * of {@link FxControllerAndView} instances into Spring components, based on generic type inspection.
 *
 * <p>Usage example:</p>
 * <pre>
 * &#64;Bean
 * public InjectionPointLazyFxControllerAndViewResolver controllerAndViewResolver(FxWeaver fxWeaver, ResourceBundle bundle) {
 *     return new InjectionPointLazyFxControllerAndViewResolver(fxWeaver, bundle);
 * }
 *
 * &#64;Bean
 * &#64;Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
 * public &lt;C, V extends Node&gt; FxControllerAndView&lt;C, V&gt; controllerAndView(
 *         InjectionPointLazyFxControllerAndViewResolver resolver,
 *         InjectionPoint injectionPoint) {
 *     return resolver.resolve(injectionPoint);
 * }
 * </pre>
 *
 * <p>Example injection:</p>
 * <pre>
 * &#64;Component
 * class ArbitraryComponent {
 *     private final FxControllerAndView&lt;SomeController, VBox&gt; someController;
 *
 *     public ArbitraryComponent(FxControllerAndView&lt;SomeController, VBox&gt; someController) {
 *         this.someController = someController;
 *     }
 * }
 * </pre>
 *
 * @author geewit
 */
public record InjectionPointLazyFxControllerAndViewResolver(FxWeaver fxWeaver, ResourceBundle resourceBundle) {

    /**
     * Resolve generic type classes of a {@link FxControllerAndView} {@link InjectionPoint} and return a
     * {@link LazyFxControllerAndView} embedding the {@link FxWeaver#load(Class)} method for instance creation.
     *
     * @param injectionPoint the actual injection point for the {@link FxControllerAndView} to inject
     * @param <C> the controller type
     * @param <V> the view type (must extend {@link Node})
     * @return a lazily loaded {@link FxControllerAndView}
     * @throws IllegalArgumentException when types could not be resolved from the given injection point
     */
    public <C, V extends Node> FxControllerAndView<C, V> resolve(InjectionPoint injectionPoint) {
        ResolvableType resolvableType = this.findResolvableType(injectionPoint);
        if (resolvableType == null) {
            throw new IllegalArgumentException("No ResolvableType found for injection point: " + injectionPoint);
        }

        // 解析泛型类型
        Class<?> controllerRawClass = resolvableType.getGenerics()[0].resolve();
        Class<C> controllerClass = this.getControllerClass(injectionPoint, controllerRawClass);

        return new LazyFxControllerAndView<>(() -> fxWeaver.load(controllerClass, resourceBundle));
    }

    private <C> @NonNull Class<C> getControllerClass(InjectionPoint injectionPoint, Class<?> controllerRawClass) {
        if (controllerRawClass == null) {
            throw new IllegalArgumentException("Controller generic type could not be resolved for injection point: " + injectionPoint);
        }

        // 检查类型安全（非必须，但提供额外验证）
        if (!Object.class.isAssignableFrom(controllerRawClass)) {
            throw new IllegalArgumentException("Resolved controller type is invalid: " + controllerRawClass.getName());
        }

        // 安全转换（确保不触发 unchecked 警告）
        @SuppressWarnings("unchecked")
        Class<C> controllerClass = (Class<C>) controllerRawClass;
        return controllerClass;
    }

    /**
     * Find the {@link ResolvableType} for a given {@link InjectionPoint}.
     * Supports both field and method parameter injection.
     *
     * @param injectionPoint the injection point to inspect
     * @return a {@link ResolvableType} or {@code null} if none could be determined
     */
    private ResolvableType findResolvableType(InjectionPoint injectionPoint) {
        return Optional.ofNullable(injectionPoint.getMethodParameter())
                .map(ResolvableType::forMethodParameter)
                .orElseGet(() ->
                        Optional.ofNullable(injectionPoint.getField())
                                .map(ResolvableType::forField)
                                .orElse(null)
                );
    }
}
