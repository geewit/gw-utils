package io.geewit.web.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

import java.util.Map;

/**
 * 以静态变量保存Spring ApplicationContext.
 * @author geewit
 */
@SuppressWarnings({"unused"})
public class SpringContextUtil {

    private static ApplicationContext getContext() {
        return ContextLoader.getCurrentWebApplicationContext();
    }

    public static <T> T getBean(String name, Class<T> clazz) throws BeansException {
        ApplicationContext context = getContext();
        return context.getBean(name, clazz);
    }

    public static <T> T getBean(Class<T> clazz) {
        ApplicationContext context = getContext();
        return getBean(clazz, context);
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
