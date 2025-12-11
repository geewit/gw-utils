package io.geewit.utils.javafx.spring.config;

import io.geewit.utils.i18n.I18nSupport;
import io.geewit.utils.javafx.spring.weaver.FxControllerAndView;
import io.geewit.utils.javafx.spring.weaver.FxWeaver;
import io.geewit.utils.javafx.spring.InjectionPointLazyFxControllerAndViewResolver;
import io.geewit.utils.javafx.spring.SpringFxWeaver;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.MessageSourceResourceBundle;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.ResourceLoader;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * FxWeaverAutoConfiguration.
 *
 * @author Rene Gielen
 */
@Slf4j
@AutoConfiguration
@ConditionalOnClass({
        Node.class,
        FXMLLoader.class,
        SpringFxWeaver.class
})
public class FxWeaverAutoConfiguration {

    private final static String I18N_BASENAME = "i18n/messages";

    public FxWeaverAutoConfiguration() {
        log.info(I18nSupport.message("log.fxWeaverAutoConfiguration.init"));
    }

    @Bean
    @ConditionalOnMissingBean(MessageSource.class)
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename(ResourceLoader.CLASSPATH_URL_PREFIX + I18N_BASENAME);
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.displayName());
        messageSource.setDefaultLocale(Locale.SIMPLIFIED_CHINESE);
        return messageSource;
    }

    @Bean
    @ConditionalOnMissingBean(ResourceBundle.class)
    public ResourceBundle resourceBundle(MessageSource messageSource) {
        return new MessageSourceResourceBundle(messageSource, Locale.getDefault());
    }

    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean(FxWeaver.class)
    public FxWeaver fxWeaver(ConfigurableApplicationContext applicationContext) {
        return new SpringFxWeaver(applicationContext);
    }

    @Bean
    @ConditionalOnMissingBean(InjectionPointLazyFxControllerAndViewResolver.class)
    public InjectionPointLazyFxControllerAndViewResolver injectionPointLazyFxControllerAndViewResolver(
            FxWeaver fxWeaver,
            ResourceBundle resourceBundle) {
        return new InjectionPointLazyFxControllerAndViewResolver(fxWeaver, resourceBundle);
    }

    @Bean
    @ConditionalOnMissingBean(FxControllerAndView.class)
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public <C, V extends Node> FxControllerAndView<C, V> fxControllerAndView (
            InjectionPointLazyFxControllerAndViewResolver injectionPointLazyFxControllerAndViewResolver,
            InjectionPoint injectionPoint) {
        return injectionPointLazyFxControllerAndViewResolver.resolve(injectionPoint);
    }
}
