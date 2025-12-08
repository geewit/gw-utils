package io.geewit.utils.javafx.spring;

import io.geewit.utils.javafx.spring.weaver.FxWeaver;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * SpringFxmlLoader is a Spring-powered version of FxWeaver.
 *
 * @author Rene Gielen
 */
public class SpringFxWeaver extends FxWeaver {

    public SpringFxWeaver(ConfigurableApplicationContext context) {
        super(context::getBean, context::close, context);
    }

}
