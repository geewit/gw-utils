package io.geewit.web.utils;

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
        return applicationContext.getBean(name, clazz);
    }

    public static <T> T getBean(Class<T> clazz) {
        return getBean(clazz, applicationContext);
    }

    public static <T> T getBean(Class<T> clazz, ApplicationContext ctx) {
        Map<String, T> map = ctx.getBeansOfType(clazz);
        if (map.size() == 0) {
            return null;
        } else if (map.size() > 1) {
            new IllegalArgumentException("bean is not unique.").printStackTrace();
        }
        return map.values().iterator().next();
    }
}
