package io.geewit.core.utils.reflection;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;

import java.util.Map;

/**
 反射实现的bean和bean之间的属性copy
 @author geewit
 @since  2015-05-18
 */
@SuppressWarnings({"unchecked", "unused"})
public class BeanUtils {
    public static void copyProperties(Object source, Object target) throws BeansException {
        copyProperties(source, target, null);
    }

    public static void copyProperties(Object source, Object target, String[] ignoreProperties) throws BeansException {
        copyProperties(source, target, null, ignoreProperties, null);
    }

    public static void copyProperties(Object source, Object target, String[] ignoreProperties, String[] nullProperties) throws BeansException {
        copyProperties(source, target, null, ignoreProperties, nullProperties);
    }

    /**
     * 2个javabean 之间copy 属性
     *
     * @param source Origin bean whose properties are retrieved
     * @param target Destination bean whose properties are modified
     *
     * @exception FatalBeanException
     */
    public static void copyProperties(Object source, Object target, final Class<?> editable, String[] ignoreProperties, String[] nullProperties) throws BeansException {
        boolean ignoreNullValue = nullProperties == null || nullProperties.length == 0;
        Class<?> actualEditable;
        if (null == editable) {
            actualEditable = target.getClass();
        } else {
            if (!editable.isInstance(target) && !editable.isInstance(source)) {
                throw new IllegalArgumentException("Target class[" + target.getClass().getName() + "] not assignable to Editable class [" + editable.getName() + "]");
            }
            actualEditable = editable;
        }
        CopyOptions copyOptions = CopyOptions.create(actualEditable, ignoreNullValue, ignoreProperties);

        if(target instanceof Map) {
            if(source instanceof Map) {
                throw new IllegalArgumentException("不支持Map拷贝到Map");
            } else {
                BeanUtil.beanToMap(source, (Map<String, Object>) target, false, ignoreNullValue);
            }
        } else {
            if (source instanceof Map) {
                BeanUtil.fillBeanWithMap((Map<?, ?>) source, target, copyOptions);
            } else {
                BeanUtil.beanToMap(source, (Map<String, Object>) target, false, ignoreNullValue);
            }
        }
    }

    public static void copyProperties(Map<String, Object> source, Object target) throws BeansException {
        copyProperties(source, target, null);
    }

    public static void copyProperties(Map<String, Object> source, Object target, boolean ignoreNull) throws BeansException {
        copyProperties(source, target, null, ignoreNull);
    }

    public static void copyProperties(Map<String, Object> source, Object target, String[] ignoreProperties) throws BeansException {
        copyProperties(source, target, null, ignoreProperties, null);
    }

    public static void copyProperties(Map<String, Object> source, Object target, String[] ignoreProperties, boolean ignoreNull) throws BeansException {
        copyProperties(source, target, null, ignoreProperties, ignoreNull);
    }

    public static void copyProperties(Object source, Map<String, Object> target, boolean ignoreNull) throws BeansException {
        copyProperties(source, target, null, null, ignoreNull);
    }

    public static void copyProperties(Object source, Map<String, Object> target, final Class<?> editable, boolean ignoreNull) throws BeansException {
        copyProperties(source, target, editable, null, ignoreNull);
    }

    /**
     * 将 bean的内容 copy 到 Map里
     *
     * @param source Origin bean whose properties are retrieved
     * @param target Destination bean whose properties are modified
     *
     * @exception FatalBeanException 找不到bean异常
     */
    public static void copyProperties(Object source, Object target, final Class<?> editable, String[] ignoreProperties, boolean ignoreNullValue) throws BeansException {
        Class<?> actualEditable;
        if (null == editable) {
            actualEditable = target.getClass();
        } else {
            if (!editable.isInstance(target) && !editable.isInstance(source)) {
                throw new IllegalArgumentException("Target class[" + target.getClass().getName() + "] not assignable to Editable class [" + editable.getName() + "]");
            }
            actualEditable = editable;
        }
        CopyOptions copyOptions = CopyOptions.create(actualEditable, ignoreNullValue, ignoreProperties);

        if(target instanceof Map) {
            if(source instanceof Map) {
                throw new IllegalArgumentException("不支持Map拷贝到Map");
            } else {
                BeanUtil.beanToMap(source, (Map<String, Object>) target, false, ignoreNullValue);
            }
        } else {
            if (source instanceof Map) {
                BeanUtil.fillBeanWithMap((Map<?, ?>) source, target, copyOptions);
            } else {
                BeanUtil.beanToMap(source, (Map<String, Object>) target, false, ignoreNullValue);
            }
        }
    }
}
