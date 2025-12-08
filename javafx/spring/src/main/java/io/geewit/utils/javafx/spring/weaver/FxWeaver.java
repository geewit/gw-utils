package io.geewit.utils.javafx.spring.weaver;

import io.geewit.utils.core.exceptions.ExceptionUtils;
import io.geewit.utils.i18n.I18nSupport;
import io.geewit.utils.javafx.base.TooltipUtils;
import io.geewit.utils.javafx.spring.constants.FxConstants;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.util.Callback;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * FxWeaver is the core weaving facility, enabling Controllers and Views to be instantiated by a dependency injection
 * framework such as (but not limited to) Spring.
 * <p/>
 * The following example requires a Spring ConfigurableApplicationContext to be instantiated. If now MainController is
 * declared as Spring managed bean, it will get created and injected by Spring.
 * <p>
 * If a managed Controller class contains an {@link FxmlView} annotation, attached FXML views are injected as well.
 *
 * <pre>
 *     ConfigurableApplicationContext applicationContext = ...
 *     FxWeaver fxWeaver = new FxWeaver(applicationContext::getBean, applicationContext::close);
 *     Scene scene = new GwScene(fxWeaver.loadView(MainController.class), 400, 300);
 *     ...
 *     &#64;FxmlView
 *     public class MainController {
 *        ...
 *     }
 * </pre>
 *
 * @author Rene Gielen
 * @noinspection unused, WeakerAccess
 * @see FxmlView
 */
@Slf4j
public class FxWeaver {

    private final Callback<Class<?>, Object> beanFactory;
    private final Runnable closeCommand;
    private final ResourceLoader resourceLoader;

    /**
     * Create a FxWeaver instance.
     * <p/>
     * Example:
     * <pre>
     *     ConfigurableApplicationContext applicationContext = ...
     *     FxWeaver fxWeaver = new FxWeaver(applicationContext::getBean, applicationContext::close);
     * </pre>
     *
     * @param beanFactory  The beanFactory callback to be called for requesting a bean of given class when e.g. {@link
     *                     #loadView(Class)} is called.
     * @param closeCommand The function to close a bean factory attached to FxWeaver
     * @see #loadView(Class)
     * @see #loadView(Class, ResourceBundle)
     */
    public FxWeaver(Callback<Class<?>, Object> beanFactory,
                    Runnable closeCommand) {
        this(beanFactory, closeCommand, new DefaultResourceLoader());
    }

    public FxWeaver(Callback<Class<?>, Object> beanFactory,
                    Runnable closeCommand,
                    ResourceLoader resourceLoader) {
        this.beanFactory = beanFactory;
        this.closeCommand = closeCommand;
        this.resourceLoader = resourceLoader;
    }

    /**
     * Load FXML-defined view instance, weaved with its controller declared in fx:controller as a bean produced by the
     * bean factory provided in {@link #FxWeaver(Callback, Runnable)}.
     * <p/>
     * The possible FXML resource is inferred from a {@link FxmlView} annotation at the controller class or the simple
     * classname and package of said class if it was not annotated like this. If the FXML file is resolvable, the
     * defined view within will be loaded by {@link FXMLLoader}. The controller will then be instantiated based on the
     * fx:controller attribute, using the bean factory from {@link #FxWeaver(Callback, Runnable)}. If the bean factory
     * is based on a dependency management framework such as Spring, Guice or CDI, this means that the instance will be
     * fully managed and injected as declared.
     * <p/>
     * If you are interested in the controller instance, you might instead use the <tt>loadController</tt> methods, e.g.
     * {@link #loadController(Class)}
     *
     * @param controllerClass The controller class of which a weaved instance should be provided
     * @param <C>             The controller type
     * @param <V>             The view type
     * @return An instance of the requested view, weaved with its managed controller as defined in {@link
     * FXMLLoader#getController()}.
     * @see #loadController(Class)
     * @see #loadController(Class, ResourceBundle)
     * @see #loadController(Class, String)
     * @see #loadController(Class, String, ResourceBundle)
     * @see #FxWeaver(Callback, Runnable)
     * @see FXMLLoader
     */
    public <C, V extends Node> V loadView(Class<C> controllerClass) {
        return this.loadView(controllerClass, (ResourceBundle) null);
    }

    /**
     * Load FXML-defined view instance, weaved with its controller declared in fx:controller as a bean produced by the
     * bean factory provided in {@link #FxWeaver(Callback, Runnable)}.
     * <p/>
     * The possible FXML resource is inferred from a {@link FxmlView} annotation at the controller class or the simple
     * classname and package of said class if it was not annotated like this. If the FXML file is resolvable, the
     * defined view within will be loaded by {@link FXMLLoader}. The controller will then be instantiated based on the
     * fx:controller attribute, using the bean factory from {@link #FxWeaver(Callback, Runnable)}. If the bean factory
     * is based on a dependency management framework such as Spring, Guice or CDI, this means that the instance will be
     * fully managed and injected as declared.
     * <p/>
     * If you are interested in the controller instance, you might instead use the <tt>loadController</tt> methods, e.g.
     * {@link #loadController(Class)}
     *
     * @param controllerClass The controller class of which a weaved instance should be provided
     * @param resourceBundle  The optional {@link ResourceBundle} to use for view creation. May be <tt>null</tt>
     * @param <C>             The controller type
     * @param <V>             The view type
     * @return An instance of the requested view, weaved with its managed controller as defined in {@link
     * FXMLLoader#getController()}.
     * @see #loadController(Class)
     * @see #loadController(Class, ResourceBundle)
     * @see #loadController(Class, String)
     * @see #loadController(Class, String, ResourceBundle)
     * @see #FxWeaver(Callback, Runnable)
     * @see FXMLLoader
     */
    public <C, V extends Node> V loadView(Class<C> controllerClass,
                                          ResourceBundle resourceBundle) {
        return this.loadView(controllerClass, this.buildFxmlReference(controllerClass), resourceBundle);
    }

    /**
     * Load FXML-defined view instance, weaved with its controller declared in fx:controller as a bean produced by the
     * bean factory provided in {@link #FxWeaver(Callback, Runnable)}.
     * <p/>
     * The possible FXML resource may be given as a location in the classpath. If the FXML file is resolvable, the
     * defined view within will be loaded by {@link FXMLLoader}. The controller will then be instantiated based on the
     * fx:controller attribute, using the bean factory from {@link #FxWeaver(Callback, Runnable)}. If the bean factory
     * is based on a dependency management framework such as Spring, Guice or CDI, this means that the instance will be
     * fully managed and injected as declared.
     * <p/>
     * If you are interested in the controller instance, you might instead use the <tt>loadController</tt> methods, e.g.
     * {@link #loadController(Class)}
     *
     * @param controllerClass The controller class of which a weaved instance should be provided
     * @param location        The location of the FXML view to load as a classloader resource.
     * @param <C>             The controller type
     * @param <V>             The view type
     * @return An instance of the requested view, weaved with its managed controller as defined in {@link
     * FXMLLoader#getController()}.
     * @see #load(Class)
     * @see #load(Class, ResourceBundle)
     * @see #loadController(Class)
     * @see #loadController(Class, ResourceBundle)
     * @see #FxWeaver(Callback, Runnable)
     * @see FXMLLoader
     */
    public <C, V extends Node> V loadView(Class<C> controllerClass, String location) {
        return this.loadView(controllerClass, location, null);
    }

    /**
     * Load FXML-defined view instance, weaved with its controller declared in fx:controller as a bean produced by the
     * bean factory provided in {@link #FxWeaver(Callback, Runnable)}.
     * <p/>
     * The possible FXML resource may be given as a location in the classpath. If the FXML file is resolvable, the
     * defined view within will be loaded by {@link FXMLLoader}. The controller will then be instantiated based on the
     * fx:controller attribute, using the bean factory from {@link #FxWeaver(Callback, Runnable)}. If the bean factory
     * is based on a dependency management framework such as Spring, Guice or CDI, this means that the instance will be
     * fully managed and injected as declared.
     * <p/>
     * If you are interested in the controller instance, you might instead use the <tt>loadController</tt> methods, e.g.
     * {@link #loadController(Class)}
     *
     * @param controllerClass The controller class of which a weaved instance should be provided
     * @param location        The location of the FXML view to load as a classloader resource.
     * @param resourceBundle  The optional {@link ResourceBundle} to use for view creation. May be <tt>null</tt>
     * @param <C>             The controller type
     * @param <V>             The view type
     * @return An instance of the requested view, weaved with its managed controller as defined in {@link
     * FXMLLoader#getController()}.
     * @see #loadController(Class)
     * @see #loadController(Class, ResourceBundle)
     * @see #FxWeaver(Callback, Runnable)
     * @see FXMLLoader
     */
    public <C, V extends Node> V loadView(Class<C> controllerClass,
                                          String location,
                                          ResourceBundle resourceBundle) {
        FxControllerAndView<C, V> controllerAndView = this.load(controllerClass, location, resourceBundle);
        return controllerAndView.getView()
                .orElseThrow(() -> new FxLoadException(this.buildMissingViewMessage(controllerClass, location)));
    }

    /**
     * Load controller instance, potentially weaved with a FXML view declaring the given class as fx:controller.
     * <p/>
     * The possible FXML resource may be given as a location in the classpath. If the FXML file is resolvable, the
     * defined view within will be loaded by {@link FXMLLoader}. The controller will then be instantiated based on the
     * fx:controller attribute, using the bean factory from {@link #FxWeaver(Callback, Runnable)}. If the bean factory
     * is based on a dependency management framework such as Spring, Guice or CDI, this means that the instance will be
     * fully managed and injected as declared.
     * <p/>
     * If the controller class does not come with a resolvable FXML view resource, the controller will be instantiated
     * by the given bean factory directly.
     *
     * @param controllerClass The controller class of which a weaved instance should be provided
     * @param location        The location of the FXML view to load as a classloader resource. May be <tt>null</tt> or
     *                        not resolvable, in which case the controller will be directly instantiated by the given
     *                        bean factory.
     * @param <C>             The controller type
     * @return A managed instance of the requested controller, potentially weaved with its view
     * @see #FxWeaver(Callback, Runnable)
     * @see FXMLLoader
     */
    public <C> C loadController(Class<C> controllerClass,
                                String location) {
        return this.loadController(controllerClass, location, null);
    }

    /**
     * Load controller instance, potentially weaved with a FXML view declaring the given class as fx:controller.
     * <p/>
     * The possible FXML resource may be given as a location in the classpath. If the FXML file is resolvable, the
     * defined view within will be loaded by {@link FXMLLoader}. The controller will then be instantiated based on the
     * fx:controller attribute, using the bean factory from {@link #FxWeaver(Callback, Runnable)}. If the bean factory
     * is based on a dependency management framework such as Spring, Guice or CDI, this means that the instance will be
     * fully managed and injected as declared.
     * <p/>
     * If the controller class does not come with a resolvable FXML view resource, the controller will be instantiated
     * by the given bean factory directly.
     *
     * @param controllerClass The controller class of which a weaved instance should be provided
     * @param location        The location of the FXML view to load as a classloader resource. May be <tt>null</tt> or
     *                        not resolvable, in which case the controller will be directly instantiated by the given
     *                        bean factory.
     * @param resourceBundle  The optional {@link ResourceBundle} to use for view creation. May be <tt>null</tt>
     * @param <C>             The controller type
     * @return A managed instance of the requested controller, potentially weaved with its view
     * @see #FxWeaver(Callback, Runnable)
     * @see FXMLLoader
     */
    public <C> C loadController(Class<C> controllerClass,
                                String location,
                                ResourceBundle resourceBundle) {
        return this.load(controllerClass, location, resourceBundle).controller();
    }

    /**
     * Load controller instance, potentially weaved with a FXML view declaring the given class as fx:controller.
     * <p/>
     * The possible FXML resource is inferred from a {@link FxmlView} annotation at the controller class or the simple
     * classname and package of said class if it was not annotated like this. If the FXML file is resolvable, the
     * defined view within will be loaded by {@link FXMLLoader}. The controller will then be instantiated based on the
     * fx:controller attribute, using the bean factory from {@link #FxWeaver(Callback, Runnable)}. If the bean factory
     * is based on a dependency management framework such as Spring, Guice or CDI, this means that the instance will be
     * fully managed and injected as declared.
     * <p/>
     * If the controller class does not come with a resolvable FXML view resource, the controller will be instantiated
     * by the given bean factory directly.
     *
     * @param controllerClass The controller class of which a weaved instance should be provided
     * @param <C>             The controller type
     * @return A managed instance of the requested controller, potentially weaved with its view
     * @see #FxWeaver(Callback, Runnable)
     * @see FXMLLoader
     */
    public <C> C loadController(Class<C> controllerClass) {
        return this.loadController(controllerClass, (ResourceBundle) null);
    }

    /**
     * Load controller instance, potentially weaved with a FXML view declaring the given class as fx:controller.
     * <p/>
     * The possible FXML resource is inferred from a {@link FxmlView} annotation at the controller class or the simple
     * classname and package of said class if it was not annotated like this. If the FXML file is resolvable, the
     * defined view within will be loaded by {@link FXMLLoader}. The controller will then be instantiated based on the
     * fx:controller attribute, using the bean factory from {@link #FxWeaver(Callback, Runnable)}. If the bean factory
     * is based on a dependency management framework such as Spring, Guice or CDI, this means that the instance will be
     * fully managed and injected as declared.
     * <p/>
     * If the controller class does not come with a resolvable FXML view resource, the controller will be instantiated
     * by the given bean factory directly.
     *
     * @param controllerClass The controller class of which a weaved instance should be provided
     * @param resourceBundle  The optional {@link ResourceBundle} to use for view creation. May be <tt>null</tt>
     * @param <C>             The controller type
     * @return A managed instance of the requested controller, potentially weaved with its view
     * @see #FxWeaver(Callback, Runnable)
     * @see FXMLLoader
     */
    public <C> C loadController(Class<C> controllerClass,
                                ResourceBundle resourceBundle) {
        return this.load(controllerClass, resourceBundle).controller();
    }

    /**
     * Get managed bean instance from bean factory provided in {@link #FxWeaver(Callback, Runnable)}.
     *
     * @param beanType The type of the bean to be instantiated.
     * @param <C>      The bean type.
     * @return The bean as defined in and returned from the bean factory
     */
    public <C> C getBean(Class<C> beanType) {
        return beanType.cast(beanFactory.call(beanType));
    }

    /**
     * Load controller instance, potentially weaved with a FXML view declaring the given class as fx:controller.
     * <p/>
     * The possible FXML resource is inferred from a {@link FxmlView} annotation at the controller class or the simple
     * classname and package of said class if it was not annotated like this. If the FXML file is resolvable, the
     * defined view within will be loaded by {@link FXMLLoader}. The controller will then be instantiated based on the
     * fx:controller attribute, using the bean factory from {@link #FxWeaver(Callback, Runnable)}. If the bean factory
     * is based on a dependency management framework such as Spring, Guice or CDI, this means that the instance will be
     * fully managed and injected as declared.
     * <p/>
     * If the controller class does not come with a resolvable FXML view resource, the controller will be instantiated
     * by the given bean factory directly.
     *
     * @param controllerClass The controller class of which a weaved instance should be provided
     * @param <V>             The view type
     * @param <C>             The controller type
     * @return A {@link SimpleFxControllerAndView} container with the managed instance of the requested controller and the
     * corresponding view, if applicable
     * @see #FxWeaver(Callback, Runnable)
     * @see FXMLLoader
     */
    public <C, V extends Node> FxControllerAndView<C, V> load(Class<C> controllerClass) {
        return this.load(controllerClass, null);
    }

    /**
     * Load controller instance, potentially weaved with a FXML view declaring the given class as fx:controller.
     * <p/>
     * The possible FXML resource is inferred from a {@link FxmlView} annotation at the controller class or the simple
     * classname and package of said class if it was not annotated like this. If the FXML file is resolvable, the
     * defined view within will be loaded by {@link FXMLLoader}. The controller will then be instantiated based on the
     * fx:controller attribute, using the bean factory from {@link #FxWeaver(Callback, Runnable)}. If the bean factory
     * is based on a dependency management framework such as Spring, Guice or CDI, this means that the instance will be
     * fully managed and injected as declared.
     * <p/>
     * If the controller class does not come with a resolvable FXML view resource, the controller will be instantiated
     * by the given bean factory directly.
     *
     * @param controllerClass The controller class of which a weaved instance should be provided
     * @param resourceBundle  The optional {@link ResourceBundle} to use for view creation. May be <tt>null</tt>
     * @param <V>             The view type
     * @param <C>             The controller type
     * @return A {@link SimpleFxControllerAndView} container with the managed instance of the requested controller and the
     * corresponding view, if applicable
     * @see #FxWeaver(Callback, Runnable)
     * @see FXMLLoader
     */
    public <C, V extends Node> FxControllerAndView<C, V> load(Class<C> controllerClass,
                                                              ResourceBundle resourceBundle) {
        return this.load(controllerClass, this.buildFxmlReference(controllerClass), resourceBundle);
    }

    /**
     * Load controller instance, potentially weaved with a FXML view declaring the given class as fx:controller.
     * <p/>
     * The possible FXML resource is inferred from a {@link FxmlView} annotation at the controller class or the simple
     * classname and package of said class if it was not annotated like this. If the FXML file is resolvable, the
     * defined view within will be loaded by {@link FXMLLoader}. The controller will then be instantiated based on the
     * fx:controller attribute, using the bean factory from {@link #FxWeaver(Callback, Runnable)}. If the bean factory
     * is based on a dependency management framework such as Spring, Guice or CDI, this means that the instance will be
     * fully managed and injected as declared.
     * <p/>
     * If the controller class does not come with a resolvable FXML view resource, the controller will be instantiated
     * by the given bean factory directly.
     *
     * @param controllerClass The controller class of which a weaved instance should be provided
     * @param location        The location of the FXML view to load as a classloader resource. May be <tt>null</tt> or
     *                        not resolvable, in which case the controller will be directly instantiated by the given
     *                        bean factory.
     * @param resourceBundle  The optional {@link ResourceBundle} to use for view creation. May be <tt>null</tt>
     * @param <V>             The view type
     * @param <C>             The controller type
     * @return A {@link SimpleFxControllerAndView} container with the managed instance of the requested controller and the
     * corresponding view, if applicable
     * @see #FxWeaver(Callback, Runnable)
     * @see FXMLLoader
     */
    protected <C, V extends Node> FxControllerAndView<C, V> load(Class<C> controllerClass,
                                                                 String location,
                                                                 ResourceBundle resourceBundle) {
        return Optional.ofNullable(location)
                .map(loc -> this.resolveResourcePath(controllerClass, loc))
                .map(resourceLoader::getResource)
                .filter(Resource::exists)
                .map(resource -> {
                    try {
                        return this.<C, V>loadByView(resource.getURL(), resourceBundle);
                    } catch (IOException e) {
                        throw new FxLoadException("Unable to load FXML file " + resource.getDescription(), e);
                    }
                })
                .orElseGet(() -> SimpleFxControllerAndView.ofController(this.getBean(controllerClass)));
    }

    private <C, V extends Node> FxControllerAndView<C, V> loadByView(URL url,
                                                                     ResourceBundle resourceBundle) {
        return this.loadByViewUsingFxmlLoader(
                new FXMLLoader(url),
                resourceBundle
        );
    }

    <C, V extends Node> FxControllerAndView<C, V> loadByViewUsingFxmlLoader(FXMLLoader loader,
                                                                            ResourceBundle resourceBundle) {
        URL fxmlUrl;
        if (loader.getLocation() != null) {
            fxmlUrl = loader.getLocation();
        } else {
            throw new FxLoadException("Unable to load FXML file, url is null");
        }
        try (InputStream fxmlStream = fxmlUrl.openStream()) {
            log.debug(I18nSupport.message("log.fxWeaver.load.fxml", fxmlUrl));
            loader.setLocation(fxmlUrl);
            loader.setControllerFactory(beanFactory);
            if (resourceBundle != null) {
                loader.setResources(resourceBundle);
            }
            V view = loader.load(fxmlStream);
            TooltipUtils.apply(view);
            return SimpleFxControllerAndView.of(loader.getController(), view);
        } catch (IOException e) {
            throw new FxLoadException("Unable to load FXML file " + fxmlUrl, e);
        }
    }

    /**
     * Build a FXML view location reference for controller classes, based on {@link FxmlView} annotation or simple
     * classname.
     *
     * @param c The class to build a FXML location for. If it does not contain a {@link FxmlView} annotation to specify
     *          resource to load, it is assumed that the view resides in the same package, named
     *          {c.getSimpleName()}.fxml
     * @return a resource location suitable for loading by {@link Class#getResource(String)}
     */
    protected String buildFxmlReference(Class<?> c) {
        return Optional.ofNullable(
                c.getAnnotation(FxmlView.class))
                .map(FxmlView::value)
                .map(value -> value.isEmpty() ? null : value)
                .orElse(c.getSimpleName() + FxConstants.FXML_EXTENSION);
    }

    private String buildMissingViewMessage(Class<?> controllerClass, String location) {
        String requestedLocation = location != null ? location : this.buildFxmlReference(controllerClass);
        if (requestedLocation == null || requestedLocation.isBlank()) {
            return "Unable to resolve FXML view for controller %s. No view location could be inferred.".formatted(
                    controllerClass.getName());
        }
        String resolvedLocation = this.resolveResourcePath(controllerClass, requestedLocation);
        return "Unable to resolve FXML view for controller %s. Checked location '%s' (resolved: %s).".formatted(
                controllerClass.getName(),
                requestedLocation,
                resolvedLocation);
    }

    private String resolveResourcePath(Class<?> controllerClass, String location) {
        if (location.startsWith(FxConstants.FXML_RESOURCE_LOCATION) || location.startsWith("file:") ||
                location.startsWith("http:") || location.startsWith("https:")) {
            return location;
        }
        if (location.startsWith("/")) {
            return FxConstants.FXML_RESOURCE_LOCATION + location;
        }
        String basePath = ClassUtils.convertClassNameToResourcePath(controllerClass.getPackageName());
        return FxConstants.FXML_RESOURCE_LOCATION + basePath + "/" + location;
    }

    /**
     * Perform the provided close method and call {@link Platform#exit()}.
     */
    public void shutdown() {
        log.debug(I18nSupport.message("log.fxWeaver.shutdown.start"));
        try {
            closeCommand.run();
            log.info(I18nSupport.message("log.fxWeaver.springClosed"));
            Platform.exit();
            log.info(I18nSupport.message("log.fxWeaver.platformExited"));
        } catch (Exception e) {
            log.warn(I18nSupport.message("log.fxWeaver.shutdown.error", ExceptionUtils.getSimpleStackTrace(e)));
        } finally {
            log.debug(I18nSupport.message("log.fxWeaver.shutdown.complete"));
        }
    }
}
