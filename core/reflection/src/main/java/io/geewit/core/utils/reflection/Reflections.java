package io.geewit.core.utils.reflection;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 反射工具类.
 * 提供调用getter/setter方法, 访问私有变量, 调用私有方法, 获取泛型类型Class, 被AOP过的真实类等工具函数.
 *
 * @author geewit
 */
@SuppressWarnings({"unchecked", "unused"})
public class Reflections {
    private static final Logger logger = LoggerFactory.getLogger(Reflections.class);

    private static final String SETTER_PREFIX = "set";

    private static final String GETTER_PREFIX = "get";

    private static final String CGLIB_CLASS_SEPARATOR = "$$";

    private final static String RESOURCE_CLASS_PATTERN = "/**/*.class";

    /**
     * 调用Getter方法.
     */
    public static Object invokeGetter(Object obj, String propertyName) {
        String getterMethodName = GETTER_PREFIX + org.apache.commons.lang3.StringUtils.capitalize(propertyName);
        return invokeMethod(obj, getterMethodName, new Class[]{}, new Object[]{});
    }

    /**
     * 调用Setter方法, 仅匹配方法名。
     */
    public static void invokeSetter(Object obj, String propertyName, Object value) {
        String setterMethodName = SETTER_PREFIX + org.apache.commons.lang3.StringUtils.capitalize(propertyName);
        invokeMethodByName(obj, setterMethodName, new Object[]{value});
    }

    /**
     * 直接读取对象属性值, 无视private/protected修饰符, 不经过getter函数.
     */
    public static Object getFieldValue(final Object obj, final String fieldName) {
        Field field = getField(obj, fieldName);

        if (field == null) {
            throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + obj + "]");
        }

        Object result = null;
        try {
            boolean accessible = field.isAccessible();
            if (!accessible) {
                field.setAccessible(true);
            }
            result = field.get(obj);
            if (!accessible) {
                field.setAccessible(false);
            }
        } catch (IllegalAccessException e) {
            logger.error("不可能抛出的异常{}", e.getMessage());
        }
        return result;
    }

    /**
     * 直接设置对象属性值, 无视private/protected修饰符, 不经过setter函数.
     */
    public static void setFieldValue(final Object obj, final String fieldName, final Object value) {
        setFieldValue(obj, fieldName, value, true);
    }

    /**
     * 直接设置对象属性值, 无视private/protected修饰符, 不经过setter函数.
     */
    public static void setFieldValue(final Object obj, final String fieldName, final Object value, boolean silent) {
        Field field = getField(obj, fieldName);

        if (field == null) {
            if (silent) {
                return;
            } else {
                throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + obj + "]");
            }
        }

        try {
            boolean accessible = field.isAccessible();
            if (!accessible) {
                field.setAccessible(true);
            }
            field.set(obj, value);
            if (!accessible) {
                field.setAccessible(false);
            }
        } catch (IllegalAccessException e) {
            logger.error("不可能抛出的异常:{}", e.getMessage());
        }
    }

    /**
     * 直接调用对象方法, 无视private/protected修饰符.
     * 用于一次性调用的情况，否则应使用getAccessibleMethod()函数获得Method后反复调用.
     * 同时匹配方法名+参数类型，
     *
     * @param obj            目标对象
     * @param methodName     目标方法
     * @param parameterTypes 参数类型
     * @param args           参数
     */
    public static Object invokeMethod(final Object obj, final String methodName, final Class<?>[] parameterTypes,
                                      final Object[] args) {
        Method method = getMethod(obj, methodName, parameterTypes);
        if (method == null) {
            throw new IllegalArgumentException("Could not find method [" + methodName + "] on target [" + obj + "]");
        }

        try {
            boolean accessible = method.isAccessible();
            if (!accessible) {
                method.setAccessible(true);
            }
            Object value = method.invoke(obj, args);
            if (!accessible) {
                method.setAccessible(false);
            }
            return value;
        } catch (Exception e) {
            throw convertReflectionExceptionToUnchecked(e);
        }
    }

    /**
     * 直接调用对象方法, 无视private/protected修饰符，
     * 用于一次性调用的情况，否则应使用getAccessibleMethodByName()函数获得Method后反复调用.
     * 只匹配函数名，如果有多个同名函数调用第一个。
     *
     * @param obj        目标对象
     * @param methodName 目标方法
     * @param args       参数
     */
    public static Object invokeMethodByName(final Object obj, final String methodName, final Object[] args) {
        return invokeMethodByName(obj, methodName, args, true);
    }

    /**
     * 直接调用对象方法, 无视private/protected修饰符，
     * 用于一次性调用的情况，否则应使用getAccessibleMethodByName()函数获得Method后反复调用.
     * 只匹配函数名，如果有多个同名函数调用第一个。
     *
     * @param obj        目标对象
     * @param methodName 目标方法
     * @param args       参数
     */
    public static Object invokeMethodByName(final Object obj, final String methodName, final Object[] args, boolean silent) {
        Method method = getMethodByName(obj, methodName);
        if (method == null) {
            if (silent) {
                return null;
            } else {
                throw new IllegalArgumentException("Could not find method [" + methodName + "] on target [" + obj + "]");
            }
        }

        try {
            boolean accessible = method.isAccessible();
            if (!accessible) {
                method.setAccessible(true);
            }
            Object value = method.invoke(obj, args);
            if (!accessible) {
                method.setAccessible(false);
            }
            return value;
        } catch (Exception e) {
            throw convertReflectionExceptionToUnchecked(e);
        }
    }

    /**
     * 循环向上转型, 获取对象的DeclaredField, 并强制设置为可访问.
     * <p>
     * 如向上转型到Object仍无法找到, 返回null.
     *
     * @param obj       目标对象
     * @param fieldName 目标属性
     */
    public static Field getField(final Object obj, final String fieldName) {
        Validate.notNull(obj, "object can't be null");
        Validate.notBlank(fieldName, "fieldName can't be blank");
        Class<?> superClass = obj.getClass();
        while (superClass != Object.class) {
            try {
                return superClass.getDeclaredField(fieldName);
            } catch (NoSuchFieldException ignore) {
                // Field不在当前类定义,继续向上转型
            }
            superClass = superClass.getSuperclass();
        }
        return null;
    }

    /**
     * 循环向上转型, 获取对象的DeclaredMethod,并强制设置为可访问.
     * 如向上转型到Object仍无法找到, 返回null.
     * 匹配函数名+参数类型。
     * <p>
     * 用于方法需要被多次调用的情况. 先使用本函数先取得Method,然后调用Method.invoke(Object obj, Object... args)
     *
     * @param obj            目标对象
     * @param methodName     目标方法
     * @param parameterTypes 参数类型
     */
    public static Method getMethod(final Object obj, final String methodName,
                                   final Class<?>... parameterTypes) {
        Validate.notNull(obj, "object can't be null");
        Validate.notBlank(methodName, "methodName can't be blank");

        Class<?> searchType = obj.getClass();
        while (searchType != Object.class) {
            try {
                return searchType.getDeclaredMethod(methodName, parameterTypes);
            } catch (NoSuchMethodException ignore) {
                // Method不在当前类定义,继续向上转型
            }
            searchType = searchType.getSuperclass();
        }
        return null;
    }

    /**
     * 循环向上转型, 获取对象的DeclaredMethod,并强制设置为可访问.
     * 如向上转型到Object仍无法找到, 返回null.
     * 只匹配函数名。
     * <p>
     * 用于方法需要被多次调用的情况. 先使用本函数先取得Method,然后调用Method.invoke(Object obj, Object... args)
     */
    public static Method getMethodByName(final Object obj, final String methodName) {
        Validate.notNull(obj, "object can't be null");
        Validate.notBlank(methodName, "methodName can't be blank");

        Class<?> searchType = obj.getClass();
        while (searchType != Object.class) {
            Method[] methods = searchType.getDeclaredMethods();
            for (Method method : methods) {
                if (method.getName().equals(methodName)) {
                    return method;
                }
            }
            searchType = searchType.getSuperclass();
        }
        return null;
    }

    /**
     * 通过反射, 获得Class定义中声明的泛型参数的类型, 注意泛型必须定义在父类处
     * 如无法找到, 返回Object.class.
     *
     * @param clazz The class to introspect
     * @return the first generic declaration, or Object.class if cannot be determined
     */
    @SuppressWarnings({"unused"})
    public static Class<?> getClassGenricType(final Class<?> clazz) {
        return getClassGenricType(clazz, 0);
    }

    /**
     * 通过反射, 获得Class定义中声明的父类的泛型参数的类型.
     * 如无法找到, 返回Object.class.
     *
     * @param clazz clazz The class to introspect
     * @param index the Index of the generic ddeclaration,start from 0.
     * @return the index generic declaration, or Object.class if cannot be determined
     */
    public static Class<?> getClassGenricType(final Class<?> clazz, final int index) {

        Type genType = clazz.getGenericSuperclass();

        if (!(genType instanceof ParameterizedType)) {
            logger.warn(clazz.getSimpleName() + "'s superclass not ParameterizedType");
            return Object.class;
        }

        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

        if ((index >= params.length) || (index < 0)) {
            logger.warn("Index: " + index + ", Size of " + clazz.getSimpleName() + "'s Parameterized Type: "
                    + params.length);
            return Object.class;
        }
        if (!(params[index] instanceof Class<?>)) {
            logger.warn(clazz.getSimpleName() + " not set the actual class on superclass generic parameter");
            return Object.class;
        }

        return (Class<?>) params[index];
    }

    @SuppressWarnings({"unused"})
    public static Class<?> getUserClass(Object instance) {
        Validate.notNull(instance, "Instance must not be null");
        Class<?> clazz = instance.getClass();
        if ((clazz != null) && clazz.getName().contains(CGLIB_CLASS_SEPARATOR)) {
            Class<?> superClass = clazz.getSuperclass();
            if ((superClass != null) && !Object.class.equals(superClass)) {
                return superClass;
            }
        }
        return clazz;

    }

    /**
     * @return 将反射时的checked exception转换为unchecked exception.
     */
    public static RuntimeException convertReflectionExceptionToUnchecked(Exception e) {
        if ((e instanceof IllegalAccessException) || (e instanceof IllegalArgumentException) || (e instanceof NoSuchMethodException)) {
            return new IllegalArgumentException(e);
        } else if (e instanceof InvocationTargetException) {
            return new RuntimeException(((InvocationTargetException) e).getTargetException());
        } else if (e instanceof RuntimeException) {
            return (RuntimeException) e;
        }
        return new RuntimeException("Unexpected Checked Exception.", e);
    }

    /**
     * @return 获取所有public方法
     */
    public static <T> Method[] getPublicMethods(Class<T> clazz) {
        Method[] methods = clazz.getDeclaredMethods();
        Method[] result = Arrays.stream(methods).filter(method -> Modifier.isPublic(method.getModifiers())).toArray(Method[]::new);
        return result;
    }

    /**
     * @return 获取clazz的有对应getter方法的属性
     */
    public static <T> Field[] getPublicGetters(Class<T> clazz) {
        Field[] fields = clazz.getFields();
        Field[] result = Arrays.stream(fields).filter(field -> !Modifier.isStatic(field.getModifiers())).toArray(Field[]::new);
        Method[] methods = clazz.getDeclaredMethods();
        Field[] getterFields = {};
        for (Method method : methods) {
            if (Modifier.isPublic(method.getModifiers())) {
                for (Field field : result) {
                    if ((GETTER_PREFIX + org.apache.commons.lang3.StringUtils.capitalize(field.getName())).equals(method.getName()) && field.getType().getName().equals(method.getReturnType().getName())) {
                        getterFields = org.apache.commons.lang3.ArrayUtils.add(getterFields, field);
                    }
                }
            }
        }
        return getterFields;
    }

    /**
     * @return 获取clazz的有对应getter方法的属性
     */
    @SuppressWarnings("unchecked")
    public static <T> Field[] getPublicGetters(Class<T> clazz, Class<? extends Annotation>... excluedAnnotations) {
        Field[] fields = clazz.getFields();
        Field[] result = Arrays.stream(fields).filter(field -> !Modifier.isStatic(field.getModifiers())).toArray(Field[]::new);
        Method[] methods = clazz.getDeclaredMethods();
        Field[] getterFields = {};
        outer:
        for (Method method : methods) {
            if (org.apache.commons.lang3.ArrayUtils.isNotEmpty(excluedAnnotations)) {
                for (Class<? extends Annotation> excluedAnnotation : excluedAnnotations) {
                    if (method.isAnnotationPresent(excluedAnnotation)) {
                        continue outer;
                    }
                }
            }
            if (Modifier.isPublic(method.getModifiers())) {
                for (Field field : result) {
                    if ((GETTER_PREFIX + org.apache.commons.lang3.StringUtils.capitalize(field.getName())).equals(method.getName()) && field.getType().getName().equals(method.getReturnType().getName())) {
                        getterFields = org.apache.commons.lang3.ArrayUtils.add(getterFields, field);
                    }
                }
            }
        }
        return getterFields;
    }

    public static Collection<Class<?>> getClassesByPackageName(final String packageName) {

        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        try {
            String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                    ClassUtils.convertClassNameToResourcePath(packageName) + RESOURCE_CLASS_PATTERN;
            Resource[] resources = resourcePatternResolver.getResources(pattern);
            //MetadataReader 的工厂类
            MetadataReaderFactory readerfactory = new CachingMetadataReaderFactory(resourcePatternResolver);
            final List<Class<?>> classes = Arrays.stream(resources).map(resource -> {
                try {
                    MetadataReader reader = readerfactory.getMetadataReader(resource);
                    String classname = reader.getClassMetadata().getClassName();
                    return Class.forName(classname);
                } catch (IOException | ClassNotFoundException e) {
                    logger.info(e.getMessage(), e);
                    return null;
                }
            }).filter(Objects::nonNull).collect(Collectors.toList());
            return classes;
        } catch (IOException e) {
            logger.warn(e.getMessage(), e);
            return Collections.emptyList();
        }
    }
}
