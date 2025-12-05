package io.geewit.utils.core.reflection;

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
@SuppressWarnings({"unused"})
public class Reflections {
    private static final Logger logger = LoggerFactory.getLogger(Reflections.class);

    private Reflections() {
    }

    private static final String SETTER_PREFIX = "set";

    private static final String GETTER_PREFIX = "get";

    private static final String CGLIB_CLASS_SEPARATOR = "$$";

    private static final String RESOURCE_CLASS_PATTERN = "/**/*.class";

    /**
     * 调用Getter方法.
     *
     * @param obj 要调用getter方法的对象实例
     * @param propertyName 属性名称，将被转换为对应的getter方法名
     * @return 调用getter方法后的返回值
     */
    public static Object invokeGetter(Object obj, String propertyName) {
        // 构造getter方法名：GETTER_PREFIX + 属性名首字母大写
        String getterMethodName = GETTER_PREFIX + org.apache.commons.lang3.StringUtils.capitalize(propertyName);
        // 调用无参方法获取属性值
        return invokeMethod(obj, getterMethodName, new Class[]{}, new Object[]{});
    }

    /**
     * 调用Setter方法, 仅匹配方法名。
     *
     * @param obj 要调用setter方法的对象实例
     * @param propertyName 属性名称，将被转换为对应的setter方法名
     * @param value 要设置给属性的值
     */
    public static void invokeSetter(Object obj, String propertyName, Object value) {
        // 构造setter方法名：prefix + 首字母大写的属性名
        String setterMethodName = SETTER_PREFIX + org.apache.commons.lang3.StringUtils.capitalize(propertyName);
        // 通过方法名调用对应的setter方法
        invokeMethodByName(obj, setterMethodName, new Object[]{value});
    }

    /**
     * 直接读取对象属性值, 无视private/protected修饰符, 不经过getter函数.
     *
     * @param obj 对象实例
     * @param fieldName 属性名称
     * @return 属性值
     * @throws IllegalArgumentException 当找不到指定属性时抛出
     */
    public static Object getFieldValue(final Object obj, final String fieldName) {
        Field field = getField(obj, fieldName);

        if (field == null) {
            throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + obj + "]");
        }

        Object result = null;
        try {
            // 临时设置字段可访问性以获取私有字段值
            boolean accessible = field.canAccess(obj);
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
     *
     * @param obj 要设置属性值的对象实例
     * @param fieldName 属性名称
     * @param value 要设置的属性值
     */
    public static void setFieldValue(final Object obj, final String fieldName, final Object value) {
        setFieldValue(obj, fieldName, value, true);
    }

    /**
     * 直接设置对象属性值, 无视private/protected修饰符, 不经过setter函数.
     *
     * @param obj 要设置属性值的对象
     * @param fieldName 属性名称
     * @param value 要设置的属性值
     * @param silent 是否静默模式，true表示找不到字段时不抛出异常，false表示找不到字段时抛出异常
     */
    public static void setFieldValue(final Object obj, final String fieldName, final Object value, boolean silent) {
        // 获取对象的字段
        Field field = getField(obj, fieldName);

        if (field == null) {
            // 根据静默模式决定是否抛出异常
            if (silent) {
                return;
            } else {
                throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + obj + "]");
            }
        }

        // 设置字段值
        try {
            boolean accessible = field.canAccess(obj);
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
     * @return 方法调用结果
     */
    public static Object invokeMethod(final Object obj, final String methodName, final Class<?>[] parameterTypes,
                                      final Object[] args) {
        // 获取目标方法
        Method method = getMethod(obj, methodName, parameterTypes);
        if (method == null) {
            throw new IllegalArgumentException("Could not find method [" + methodName + "] on target [" + obj + "]");
        }

        // 执行方法调用并返回结果
        return getAccessibleResult(obj, args, method);
    }

    /**
     * 通过反射调用方法并获取结果，自动处理访问权限问题
     *
     * @param obj 调用方法的对象实例，如果方法是静态的则传入null
     * @param args 方法调用时需要的参数数组，如果没有参数则传入null
     * @param method 要调用的方法对象
     * @return 方法调用的返回结果
     * @throws RuntimeException 如果方法调用过程中发生异常，则转换为非受检异常抛出
     */
    private static Object getAccessibleResult(Object obj, Object[] args, Method method) {
        try {
            // 检查并临时设置方法的访问权限，确保可以调用私有或受保护的方法
            boolean accessible = method.canAccess(obj);
            if (!accessible) {
                method.setAccessible(true);
            }

            // 执行方法调用
            Object value = method.invoke(obj, args);

            // 如果之前修改了访问权限，则恢复原来的访问权限状态
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
     * @param methodName 目标方法名称
     * @param args       方法参数数组
     * @return           方法调用结果
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
     * @param silent     找不到方法时是否静默返回 null
     * @return 方法调用结果
     */
    public static Object invokeMethodByName(final Object obj, final String methodName,
                                            final Object[] args, boolean silent) {
        // 获取目标方法
        Method method = getMethodByName(obj, methodName);
        if (method == null) {
            // 根据silent参数决定是返回null还是抛出异常
            if (silent) {
                return null;
            } else {
                throw new IllegalArgumentException("Could not find method [" + methodName + "] on target [" + obj + "]");
            }
        }

        // 执行方法调用并返回结果
        return getAccessibleResult(obj, args, method);
    }

    /**
     * 循环向上转型, 获取对象的DeclaredField, 并强制设置为可访问.
     * <p>
     * 如向上转型到Object仍无法找到, 返回null.
     *
     * @param obj       目标对象
     * @param fieldName 目标属性
     * @return 找到的Field对象，如果未找到则返回null
     */
    public static Field getField(final Object obj, final String fieldName) {
        Validate.notNull(obj, "object can't be null");
        Validate.notBlank(fieldName, "fieldName can't be blank");
        // 循环向上转型查找字段
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
     * @return 找到的方法对象，如果未找到则返回null
     */
    public static Method getMethod(final Object obj, final String methodName,
                                   final Class<?>... parameterTypes) {
        Validate.notNull(obj, "object can't be null");
        Validate.notBlank(methodName, "methodName can't be blank");

        // 从当前类开始向上遍历继承链查找方法
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
     *
     * @param obj 对象实例，不能为null
     * @param methodName 方法名称，不能为空
     * @return 找到的方法对象，如果未找到则返回null
     */
    public static Method getMethodByName(final Object obj, final String methodName) {
        Validate.notNull(obj, "object can't be null");
        Validate.notBlank(methodName, "methodName can't be blank");

        // 从当前类开始向上遍历继承链，查找指定名称的方法
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
            logger.warn("{}'s superclass not ParameterizedType", clazz.getSimpleName());
            return Object.class;
        }

        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

        if ((index >= params.length) || (index < 0)) {
            logger.warn("Index: {}, Size of {}'s Parameterized Type: {}", index, clazz.getSimpleName(), params.length);
            return Object.class;
        }
        if (!(params[index] instanceof Class<?>)) {
            logger.warn("{} not set the actual class on superclass generic parameter", clazz.getSimpleName());
            return Object.class;
        }

        return (Class<?>) params[index];
    }


    /**
     * 获取被 CGLIB 代理之前的用户类
     * <p>
     * 当一个类被 CGLIB 代理后，会生成一个继承自原类的子类，此类用于获取原始的用户类。
     * 如果实例不是 CGLIB 代理类，则直接返回实例的类。
     *
     * @param instance 需要获取用户类的实例对象，不能为null
     * @return 返回CGLIB代理前的原始用户类，如果不存在则返回实例本身的类
     */
    public static Class<?> getUserClass(Object instance) {
        Validate.notNull(instance, "Instance must not be null");
        Class<?> clazz = instance.getClass();
        // 检查是否为CGLIB代理类
        if (clazz.getName().contains(CGLIB_CLASS_SEPARATOR)) {
            Class<?> superClass = clazz.getSuperclass();
            // 确保父类存在且不是Object类
            if ((superClass != null) && !Object.class.equals(superClass)) {
                return superClass;
            }
        }
        return clazz;
    }


    /**
     * 将反射时的checked exception转换为unchecked exception.
     *
     * @param e 需要转换的异常对象
     * @return 转换后的运行时异常
     */
    public static RuntimeException convertReflectionExceptionToUnchecked(Exception e) {
        // 处理反射相关的检查异常，转换为非法参数异常
        if ((e instanceof IllegalAccessException) ||
                (e instanceof IllegalArgumentException) ||
                (e instanceof NoSuchMethodException)) {
            return new IllegalArgumentException(e);
            // 处理调用目标异常，提取其目标异常并包装为运行时异常
        } else if (e instanceof InvocationTargetException) {
            return new RuntimeException(((InvocationTargetException) e).getTargetException());
            // 如果已经是运行时异常，直接返回
        } else if (e instanceof RuntimeException) {
            return (RuntimeException) e;
        }
        // 其他未预期的检查异常，包装为运行时异常
        return new RuntimeException("Unexpected Checked Exception.", e);
    }


    /**
     * 获取指定类的所有公共方法
     * @param <T> 泛型类型参数
     * @param clazz 要获取方法的类对象
     * @return 包含所有公共方法的数组
     */
    public static <T> Method[] getPublicMethods(Class<T> clazz) {
        Method[] methods = clazz.getDeclaredMethods();
        // 过滤出所有公共方法并转换为数组
        return Arrays.stream(methods)
                .filter(method -> Modifier.isPublic(method.getModifiers()))
                .toArray(Method[]::new);
    }


    /**
     * 获取指定类中具有对应getter方法的公共属性字段
     *
     * @param <T> 泛型类型参数
     * @param clazz 要分析的类对象
     * @return 返回具有对应getter方法的公共字段数组
     */
    public static <T> Field[] getPublicGetters(Class<T> clazz) {
        // 获取类的所有公共字段
        Field[] fields = clazz.getFields();
        // 过滤掉静态字段，只保留实例字段
        Field[] result = Arrays.stream(fields)
                .filter(field -> !Modifier.isStatic(field.getModifiers()))
                .toArray(Field[]::new);

        // 获取类声明的所有方法
        Method[] methods = clazz.getDeclaredMethods();
        Field[] getterFields = {};

        // 遍历所有公共方法，查找与字段匹配的getter方法
        for (Method method : methods) {
            if (Modifier.isPublic(method.getModifiers())) {
                for (Field field : result) {
                    // 检查方法名是否符合getter命名规范且返回类型匹配
                    if ((GETTER_PREFIX + org.apache.commons.lang3.StringUtils.capitalize(field.getName()))
                            .equals(method.getName())
                            && field.getType().getName().equals(method.getReturnType().getName())) {
                        getterFields = org.apache.commons.lang3.ArrayUtils.add(getterFields, field);
                    }
                }
            }
        }
        return getterFields;
    }


    /**
     * 获取clazz的有对应getter方法的属性（可排除带某些注解的方法）
     *
     * @param <T> 泛型类型
     * @param clazz 要获取属性的类
     * @param excluedAnnotations 要排除的注解类型数组
     * @return 包含getter方法的字段数组
     */
    @SafeVarargs
    public static <T> Field[] getPublicGetters(Class<T> clazz, Class<? extends Annotation>... excluedAnnotations) {
        // 获取类的所有公共字段
        Field[] fields = clazz.getFields();
        Field[] result = Arrays.stream(fields)
                .filter(field -> !Modifier.isStatic(field.getModifiers()))
                .toArray(Field[]::new);

        // 获取类的所有声明方法
        Method[] methods = clazz.getDeclaredMethods();
        Field[] getterFields = {};

        // 遍历所有方法，查找匹配的getter方法
        outer:
        for (Method method : methods) {
            // 检查方法是否带有要排除的注解
            if (org.apache.commons.lang3.ArrayUtils.isNotEmpty(excluedAnnotations)) {
                for (Class<? extends Annotation> excluedAnnotation : excluedAnnotations) {
                    if (method.isAnnotationPresent(excluedAnnotation)) {
                        continue outer;
                    }
                }
            }

            // 检查公共方法是否与字段匹配形成getter
            if (Modifier.isPublic(method.getModifiers())) {
                for (Field field : result) {
                    if ((GETTER_PREFIX + org.apache.commons.lang3.StringUtils.capitalize(field.getName()))
                            .equals(method.getName())
                            && field.getType().getName().equals(method.getReturnType().getName())) {
                        getterFields = org.apache.commons.lang3.ArrayUtils.add(getterFields, field);
                    }
                }
            }
        }
        return getterFields;
    }


    /**
     * 根据包名扫描并加载所有类
     *
     * @param packageName 包名，用于指定要扫描的包路径
     * @return 返回指定包路径下的所有类的集合，如果发生异常则返回空集合
     */
    public static Collection<Class<?>> getClassesByPackageName(final String packageName) {

        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        try {
            // 构造类路径匹配模式，用于查找指定包下的所有class文件
            String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                    ClassUtils.convertClassNameToResourcePath(packageName) + RESOURCE_CLASS_PATTERN;
            Resource[] resources = resourcePatternResolver.getResources(pattern);
            // MetadataReader 的工厂类
            MetadataReaderFactory readerfactory = new CachingMetadataReaderFactory(resourcePatternResolver);
            // 遍历所有找到的资源文件，读取类元数据并加载对应的Class对象
            return Arrays.stream(resources).map(resource -> {
                try {
                    MetadataReader reader = readerfactory.getMetadataReader(resource);
                    String classname = reader.getClassMetadata().getClassName();
                    return Class.forName(classname);
                } catch (IOException | ClassNotFoundException e) {
                    logger.info(e.getMessage(), e);
                    return null;
                }
            }).filter(Objects::nonNull).collect(Collectors.toList());
        } catch (IOException e) {
            logger.warn(e.getMessage(), e);
            return Collections.emptyList();
        }
    }
}
