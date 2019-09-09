package io.geewit.core.utils.lang.reflection;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
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
    private static void copyProperties(Object source, Object target, final Class<?> editable, String[] ignoreProperties, String[] nullProperties) throws BeansException {
        Class<?> actualEditable = target.getClass();
        if (null != editable) {
            if (!editable.isInstance(target)) {
                throw new IllegalArgumentException("Target class[" + target.getClass().getName() + "] not assignable to Editable class [" + editable.getName() + "]");
            }
            actualEditable = editable;
        }
        if(target instanceof Map) {
            final PropertyDescriptor[] sourcePds = org.springframework.beans.BeanUtils.getPropertyDescriptors(source.getClass());
            for (final PropertyDescriptor sourcePd : sourcePds) {
                final String propertyName = sourcePd.getName();
                if("class".equals(propertyName)) {
                    continue;
                }
                if(ArrayUtils.contains(ignoreProperties, propertyName)) {
                    continue;
                }
                final Method readMethod = sourcePd.getReadMethod();
                if(readMethod == null) {
                    continue;
                }
                if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
                    readMethod.setAccessible(true);
                }
                try {
                    final Object value = readMethod.invoke(source);
                    if(value != null || ArrayUtils.contains(nullProperties, propertyName)) {
                        ((Map<String, Object>)target).put(propertyName, value);
                    }
                } catch (Throwable throwable) {
                    throw new FatalBeanException("Could not copy properties from source bean to target bean!", throwable);
                }
            }
        } else {
            final PropertyDescriptor[] targetPds = org.springframework.beans.BeanUtils.getPropertyDescriptors(actualEditable);
            for (final PropertyDescriptor targetPd : targetPds) {
                final String propertyName = targetPd.getName();
                if("class".equals(propertyName)) {
                    continue;
                }
                if(ArrayUtils.contains(ignoreProperties, propertyName)) {
                    continue;
                }
                final Method writeMethod = targetPd.getWriteMethod();
                if(writeMethod == null) {
                    continue;
                }
                if(ArrayUtils.contains(ignoreProperties, propertyName)) {
                    continue;
                }

                final PropertyDescriptor sourcePd = org.springframework.beans.BeanUtils.getPropertyDescriptor(source.getClass(), propertyName);
                if(sourcePd == null) {
                    continue;
                }
                final Method readMethod = sourcePd.getReadMethod();
                if(readMethod == null) {
                    continue;
                }
                try {
                    if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
                        readMethod.setAccessible(true);
                    }
                    final Object value = readMethod.invoke(source);
                    if(value == null && !ArrayUtils.contains(nullProperties, propertyName)) {
                        continue;
                    }
                    if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                        writeMethod.setAccessible(true);
                    }
                    writeMethod.invoke(target, value);
                } catch (SecurityException e) {
                    throw new FatalBeanException("Could not copy properties from source bean to target bean!", e);
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ignore) {
                }

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
        copyProperties(source, target, null, ignoreProperties, true);
    }

    public static void copyProperties(Map<String, Object> source, Object target, String[] ignoreProperties, boolean ignoreNull) throws BeansException {
        copyProperties(source, target, null, ignoreProperties, ignoreNull);
    }

    /**
     * 将 Map的内容 copy 到 bean里
     *
     * @param source Origin bean whose properties are retrieved
     * @param target Destination bean whose properties are modified
     *
     * @exception FatalBeanException
     */
    private static void copyProperties(Map<String, Object> source, Object target, final Class<?> editable, String[] ignoreProperties, boolean ignoreNull) throws BeansException {
        Class<?> actualEditable = target.getClass();
        if (null != editable) {
            if (!editable.isInstance(target)) {
                throw new IllegalArgumentException("Target class[" + target.getClass().getName() + "] not assignable to Editable class [" + editable.getName() + "]");
            }
            actualEditable = editable;
        }
        final PropertyDescriptor[] targetPds = org.springframework.beans.BeanUtils.getPropertyDescriptors(actualEditable);
        for (final PropertyDescriptor targetPd : targetPds) {
            String propertyName = targetPd.getName();
            if("class".equals(propertyName)) {
                continue;
            }
            if(ArrayUtils.contains(ignoreProperties, propertyName)) {
                continue;
            }
            final Method writeMethod = targetPd.getWriteMethod();
            if(writeMethod == null) {
                continue;
            }
            final Object value = source.get(propertyName);
            if(!ignoreNull || null != value) {
                try {
                    writeMethod.invoke(target, value);
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ignore) {
                }
            }
        }
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
    public static void copyProperties(Object source, Map<String, Object> target, final Class<?> editable, String[] ignoreProperties, boolean ignoreNull) throws BeansException {
        Class<?> actualEditable = source.getClass();
        if (null != editable) {
            if (!editable.isInstance(source)) {
                throw new IllegalArgumentException("Target class[" + source.getClass().getName() + "] not assignable to Editable class [" + editable.getName() + "]");
            }
            actualEditable = editable;
        }

        final PropertyDescriptor[] sourcePds = org.springframework.beans.BeanUtils.getPropertyDescriptors(actualEditable);
        for (final PropertyDescriptor sourcePd : sourcePds) {
            final String propertyName = sourcePd.getName();
            if("class".equals(propertyName)) {
                continue;
            }
            if(ArrayUtils.contains(ignoreProperties, propertyName)) {
                continue;
            }
            final Method readMethod = sourcePd.getReadMethod();
            if(readMethod == null) {
                continue;
            }
            try {
                final Object value = readMethod.invoke(source);
                if(!ignoreNull || value != null) {
                    target.put(propertyName, value);
                }
            } catch (IllegalAccessException | InvocationTargetException ignore) {
            }
        }
    }
}
