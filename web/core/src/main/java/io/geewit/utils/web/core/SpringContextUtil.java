package io.geewit.utils.web.core;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * 以静态变量保存Spring ApplicationContext.
 * @author geewit
 */
@Configuration
public class SpringContextUtil implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextUtil.applicationContext = applicationContext;
    }

    public static <T> T getBean(String name, Class<T> clazz) throws BeansException {
        return getBean(name, clazz, applicationContext);
    }

    public static <T> T getBean(Class<T> clazz) {
        return getBean(null, clazz, applicationContext);
    }

    public static <T> T getBean(String name, Class<T> clazz, ApplicationContext ctx) {
        Map<String, T> map = ctx.getBeansOfType(clazz);
        if (map.isEmpty()) {
            return null;
        }
        if (name != null) {
            return map.get(name);
        }
        return map.values().iterator().next();
    }
}
