package io.geewit.utils.javafx.spring.config;

import io.geewit.utils.javafx.spring.weaver.FxWeaver;
import javafx.scene.Node;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FxWeaverAutoConfigurationTest {

    @Mock
    private FxWeaver fxWeaver;

    @Mock
    private ResourceBundle resourceBundle;

    @Mock
    private InjectionPoint injectionPoint;

    @Test
    void messageSource() {
        FxWeaverAutoConfiguration config = new FxWeaverAutoConfiguration();
        MessageSource messageSource = config.messageSource();
        assertNotNull(messageSource);
        assertTrue(messageSource instanceof ReloadableResourceBundleMessageSource);
    }

    @Test
    void resourceBundle() {
        FxWeaverAutoConfiguration config = new FxWeaverAutoConfiguration();
        MessageSource messageSource = config.messageSource();
        ResourceBundle bundle = config.resourceBundle(messageSource);
        assertNotNull(bundle);
    }

    @Test
    void fxWeaver() {
        org.springframework.context.ApplicationContext context = mock(org.springframework.context.ConfigurableApplicationContext.class);
        FxWeaverAutoConfiguration config = new FxWeaverAutoConfiguration();
        FxWeaver weaver = config.fxWeaver((org.springframework.context.ConfigurableApplicationContext) context);
        assertNotNull(weaver);
    }

    @Test
    void injectionPointLazyFxControllerAndViewResolver() {
        FxWeaverAutoConfiguration config = new FxWeaverAutoConfiguration();
        io.geewit.utils.javafx.spring.InjectionPointLazyFxControllerAndViewResolver resolver =
                config.injectionPointLazyFxControllerAndViewResolver(fxWeaver, resourceBundle);
        assertNotNull(resolver);
    }

    @Test
    void fxControllerAndView() {
        FxWeaverAutoConfiguration config = new FxWeaverAutoConfiguration();
        io.geewit.utils.javafx.spring.InjectionPointLazyFxControllerAndViewResolver resolver =
                config.injectionPointLazyFxControllerAndViewResolver(fxWeaver, resourceBundle);

        assertThrows(Exception.class, () -> config.fxControllerAndView(resolver, injectionPoint));
    }
}
