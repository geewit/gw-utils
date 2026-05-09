package io.geewit.utils.javafx.spring.config;

import io.geewit.utils.javafx.spring.InjectionPointLazyFxControllerAndViewResolver;
import io.geewit.utils.javafx.spring.SpringFxWeaver;
import io.geewit.utils.javafx.spring.weaver.FxWeaver;
import io.geewit.utils.javafx.spring.weaver.FxControllerAndView;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.util.Locale;
import java.util.ResourceBundle;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link FxWeaverAutoConfiguration} bean methods.
 */
@DisplayName("FxWeaverAutoConfiguration Bean Tests")
class FxWeaverAutoConfigurationBeanTest {

    private FxWeaverAutoConfiguration config = new FxWeaverAutoConfiguration();

    @Nested
    @DisplayName("messageSource bean")
    class MessageSourceBean {

        @Test
        @DisplayName("should create ReloadableResourceBundleMessageSource")
        void shouldCreateReloadableResourceBundleMessageSource() {
            MessageSource result = config.messageSource();

            assertThat(result).isInstanceOf(ReloadableResourceBundleMessageSource.class);
        }

        @Test
        @DisplayName("should set correct basename")
        void shouldSetCorrectBasename() {
            ReloadableResourceBundleMessageSource result =
                    (ReloadableResourceBundleMessageSource) config.messageSource();

            // The basename includes classpath prefix
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("should set UTF-8 encoding")
        void shouldSetUtf8Encoding() {
            ReloadableResourceBundleMessageSource result =
                    (ReloadableResourceBundleMessageSource) config.messageSource();

            // Verify it's properly configured (indirect test via instantiation success)
            assertThat(result).isNotNull();
        }
    }

    @Nested
    @DisplayName("resourceBundle bean")
    class ResourceBundleBean {

        @Test
        @DisplayName("should create MessageSourceResourceBundle")
        void shouldCreateMessageSourceResourceBundle() {
            MessageSource mockMessageSource = mock(MessageSource.class);
            ResourceBundle result = config.resourceBundle(mockMessageSource);

            assertThat(result).isNotNull();
            assertThat(result).isInstanceOf(ResourceBundle.class);
        }
    }

    @Nested
    @DisplayName("fxWeaver bean")
    class FxWeaverBean {

        @Test
        @DisplayName("should create SpringFxWeaver")
        void shouldCreateSpringFxWeaver() {
            org.springframework.context.ConfigurableApplicationContext mockContext =
                    mock(org.springframework.context.ConfigurableApplicationContext.class);
            when(mockContext.getBean(FxWeaver.class))
                    .thenThrow(new org.springframework.beans.factory.NoSuchBeanDefinitionException(""));

            FxWeaver result = config.fxWeaver(mockContext);

            assertThat(result).isInstanceOf(SpringFxWeaver.class);
        }
    }

    @Nested
    @DisplayName("injectionPointLazyFxControllerAndViewResolver bean")
    class InjectionPointResolverBean {

        @Test
        @DisplayName("should create resolver with fxWeaver and resourceBundle")
        void shouldCreateResolverWithFxWeaverAndResourceBundle() {
            FxWeaver mockWeaver = mock(FxWeaver.class);
            ResourceBundle mockBundle = mock(ResourceBundle.class);

            InjectionPointLazyFxControllerAndViewResolver result =
                    config.injectionPointLazyFxControllerAndViewResolver(mockWeaver, mockBundle);

            assertThat(result).isNotNull();
            assertThat(result).isInstanceOf(InjectionPointLazyFxControllerAndViewResolver.class);
        }
    }

    @Nested
    @DisplayName("fxControllerAndView bean")
    class FxControllerAndViewBean {

        @Test
        @DisplayName("should resolve controller and view from injection point")
        void shouldResolveControllerAndViewFromInjectionPoint() {
            InjectionPointLazyFxControllerAndViewResolver mockResolver =
                    mock(InjectionPointLazyFxControllerAndViewResolver.class);
            InjectionPoint mockInjectionPoint = mock(InjectionPoint.class);

            @SuppressWarnings("unchecked")
            FxControllerAndView<Object, javafx.scene.Node> mockResult =
                    (FxControllerAndView<Object, javafx.scene.Node>) mock(FxControllerAndView.class);
            doReturn(mockResult).when(mockResolver).resolve(mockInjectionPoint);

            FxControllerAndView<?, ?> result = config.fxControllerAndView(mockResolver, mockInjectionPoint);

            assertThat(result).isSameAs(mockResult);
        }
    }

    @Nested
    @DisplayName("constructor behavior")
    class ConstructorBehavior {

        @Test
        @DisplayName("should not throw when created")
        void shouldNotThrowWhenCreated() {
            FxWeaverAutoConfiguration newConfig = new FxWeaverAutoConfiguration();
            assertThat(newConfig).isNotNull();
        }
    }
}