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
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.ResourceBundle;

/**
 * FxWeaverAutoConfiguration.
 *
 * @author Rene Gielen
 */
@Slf4j
@Configuration
@ConditionalOnClass({
        Node.class,
        FXMLLoader.class,
        SpringFxWeaver.class
})
public class FxWeaverAutoConfiguration {

    public FxWeaverAutoConfiguration() {
        log.info(I18nSupport.message("log.fxWeaverAutoConfiguration.init"));
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
