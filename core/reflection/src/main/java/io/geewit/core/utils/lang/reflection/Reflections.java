package io.geewit.core.utils.lang.reflection;


import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;

/**
 * 反射工具类.
 *
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

    /**
     * 调用Getter方法.
     */
    public static Object invokeGetter(Object obj, String propertyName) {
        String getterMethodName = GETTER_PREFIX + org.apache.commons.lang3.StringUtils.capitalize(propertyName);
        return invokeMethod(obj, getterMethodName, new Class[] {}, new Object[] {});
    }

    /**
     * 调用Setter方法, 仅匹配方法名。
     */
    public static void invokeSetter(Object obj, String propertyName, Object value) {
        String setterMethodName = SETTER_PREFIX + org.apache.commons.lang3.StringUtils.capitalize(propertyName);
        invokeMethodByName(obj, setterMethodName, new Object[] { value });
    }

    /**
     * 直接读取对象属性值, 无视private/protected修饰符, 不经过getter函数.
     */
    public static Object getFieldValue(final Object obj, final String fieldName) {
        Field field = getAccessibleField(obj, fieldName);

        if (field == null) {
            throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + obj + "]");
        }

        Object result = null;
        try {
            result = field.get(obj);
        } catch (IllegalAccessException e) {
            logger.error("不可能抛出的异常{}", e.getMessage());
        }
        return result;
    }

    /**
     * 直接设置对象属性值, 无视private/protected修饰符, 不经过setter函数.
     */
    public static void setFieldValue(final Object obj, final String fieldName, final Object value) {
        Field field = getAccessibleField(obj, fieldName);

        if (field == null) {
            throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + obj + "]");
        }

        try {
            field.set(obj, value);
        } catch (IllegalAccessException e) {
            logger.error("不可能抛出的异常:{}", e.getMessage());
        }
    }

    /**
     * 直接调用对象方法, 无视private/protected修饰符.
     * 用于一次性调用的情况，否则应使用getAccessibleMethod()函数获得Method后反复调用.
     * 同时匹配方法名+参数类型，
     * @param obj            目标对象
     * @param methodName     目标方法
     * @param parameterTypes 参数类型
     * @param args           参数
     */
    public static Object invokeMethod(final Object obj, final String methodName, final Class<?>[] parameterTypes,
                                      final Object[] args) {
        Method method = getAccessibleMethod(obj, methodName, parameterTypes);
        if (method == null) {
            throw new IllegalArgumentException("Could not find method [" + methodName + "] on target [" + obj + "]");
        }

        try {
            return method.invoke(obj, args);
        } catch (Exception e) {
            throw convertReflectionExceptionToUnchecked(e);
        }
    }

    /**
     * 直接调用对象方法, 无视private/protected修饰符，
     * 用于一次性调用的情况，否则应使用getAccessibleMethodByName()函数获得Method后反复调用.
     * 只匹配函数名，如果有多个同名函数调用第一个。
     * @param obj            目标对象
     * @param methodName     目标方法
     * @param args           参数
     */
    public static Object invokeMethodByName(final Object obj, final String methodName, final Object[] args) {
        Method method = getAccessibleMethodByName(obj, methodName);
        if (method == null) {
            throw new IllegalArgumentException("Could not find method [" + methodName + "] on target [" + obj + "]");
        }

        try {
            return method.invoke(obj, args);
        } catch (Exception e) {
            throw convertReflectionExceptionToUnchecked(e);
        }
    }

    /**
     * 循环向上转型, 获取对象的DeclaredField, 并强制设置为可访问.
     *
     * 如向上转型到Object仍无法找到, 返回null.
     * @param obj            目标对象
     * @param fieldName      目标属性
     */
    public static Field getAccessibleField(final Object obj, final String fieldName) {
        Validate.notNull(obj, "object can't be null");
        Validate.notBlank(fieldName, "fieldName can't be blank");
        for (Class<?> superClass = obj.getClass(); superClass != Object.class; superClass = superClass.getSuperclass()) {
            try {
                Field field = superClass.getDeclaredField(fieldName);
                makeAccessible(field);
                return field;
            } catch (NoSuchFieldException ignore) {
                // Field不在当前类定义,继续向上转型
            }
        }
        return null;
    }

    /**
     * 循环向上转型, 获取对象的DeclaredMethod,并强制设置为可访问.
     * 如向上转型到Object仍无法找到, 返回null.
     * 匹配函数名+参数类型。
     *
     * 用于方法需要被多次调用的情况. 先使用本函数先取得Method,然后调用Method.invoke(Object obj, Object... args)
     * @param obj            目标对象
     * @param methodName     目标方法
     * @param parameterTypes 参数类型
     */
    public static Method getAccessibleMethod(final Object obj, final String methodName,
                                             final Class<?>... parameterTypes) {
        Validate.notNull(obj, "object can't be null");
        Validate.notBlank(methodName, "methodName can't be blank");

        for (Class<?> searchType = obj.getClass(); searchType != Object.class; searchType = searchType.getSuperclass()) {
            try {
                Method method = searchType.getDeclaredMethod(methodName, parameterTypes);
                makeAccessible(method);
                return method;
            } catch (NoSuchMethodException e) {
                // Method不在当前类定义,继续向上转型
            }
        }
        return null;
    }

    /**
     * 循环向上转型, 获取对象的DeclaredMethod,并强制设置为可访问.
     * 如向上转型到Object仍无法找到, 返回null.
     * 只匹配函数名。
     *
     * 用于方法需要被多次调用的情况. 先使用本函数先取得Method,然后调用Method.invoke(Object obj, Object... args)
     */
    public static Method getAccessibleMethodByName(final Object obj, final String methodName) {
        Validate.notNull(obj, "object can't be null");
        Validate.notBlank(methodName, "methodName can't be blank");

        for (Class<?> searchType = obj.getClass(); searchType != Object.class; searchType = searchType.getSuperclass()) {
            Method[] methods = searchType.getDeclaredMethods();
            for (Method method : methods) {
                if (method.getName().equals(methodName)) {
                    makeAccessible(method);
                    return method;
                }
            }
        }
        return null;
    }

    /**
     * 改变private/protected的方法为public，尽量不调用实际改动的语句，避免JDK的SecurityManager抱怨。
     */
    public static void makeAccessible(Method method) {
        if ((!Modifier.isPublic(method.getModifiers()) || !Modifier.isPublic(method.getDeclaringClass().getModifiers()))
                && !method.isAccessible()) {
            method.setAccessible(true);
        }
    }

    /**
     * 改变private/protected的成员变量为public，尽量不调用实际改动的语句，避免JDK的SecurityManager抱怨。
     */
    public static void makeAccessible(Field field) {
        if ((!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers()) || Modifier
                .isFinal(field.getModifiers())) && !field.isAccessible()) {
            field.setAccessible(true);
        }
    }

    /**
     * 通过反射, 获得Class定义中声明的泛型参数的类型, 注意泛型必须定义在父类处
     * 如无法找到, 返回Object.class.
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
        Method[] result = {};
        for (Method method : methods) {
            if (Modifier.isPublic(method.getModifiers())) {
                result = org.apache.commons.lang3.ArrayUtils.add(result, method);
            }
        }
        return result;
    }

    /**
    *  @return 获取clazz的有对应getter方法的属性
     */
    public static <T> Field[] getPublicGetters(Class<T> clazz) {
        Field[] fields = clazz.getFields();
        Field[] result = {};
        for (Field field : fields) {
            if (!Modifier.isStatic(field.getModifiers())) {
                result = org.apache.commons.lang3.ArrayUtils.add(result, field);
            }
        }
        Method[] methods = clazz.getDeclaredMethods();
        Field[] getterFields = {};
        for (Method method : methods) {
            if (Modifier.isPublic(method.getModifiers())) {
                for (Field field : result) {
                    if((GETTER_PREFIX + org.apache.commons.lang3.StringUtils.capitalize(field.getName())).equals(method.getName()) && field.getType().getName().equals(method.getReturnType().getName())) {
                        getterFields = org.apache.commons.lang3.ArrayUtils.add(getterFields, field);
                    }
                }
            }
        }
        return getterFields;
    }

    /**
    *  @return 获取clazz的有对应getter方法的属性
     */
    @SuppressWarnings("unchecked")
    public static <T> Field[] getPublicGetters(Class<T> clazz, Class<? extends Annotation>... excluedAnnotations) {
        Field[] fields = clazz.getFields();
        Field[] result = {};
        for (Field field : fields) {
            if (!Modifier.isStatic(field.getModifiers())) {
                result = org.apache.commons.lang3.ArrayUtils.add(result, field);
            }
        }
        Method[] methods = clazz.getDeclaredMethods();
        Field[] getterFields = {};
        outer: for (Method method : methods) {
            if(org.apache.commons.lang3.ArrayUtils.isNotEmpty(excluedAnnotations)) {
                for(Class<? extends Annotation> excluedAnnotation : excluedAnnotations) {
                    if(method.isAnnotationPresent(excluedAnnotation)) {
                        continue outer;
                    }
                }
            }
            if (Modifier.isPublic(method.getModifiers())) {
                for (Field field : result) {
                    if((GETTER_PREFIX + org.apache.commons.lang3.StringUtils.capitalize(field.getName())).equals(method.getName()) && field.getType().getName().equals(method.getReturnType().getName())) {
                        getterFields = org.apache.commons.lang3.ArrayUtils.add(getterFields, field);
                    }
                }
            }
        }
        return getterFields;
    }

    public static Collection<Class<?>> getClassesByPackageName(final Path classpath) {
        final ArrayList<Class<?>> classes = new ArrayList<>();
        try {
            Files.walkFileTree(classpath, new SimpleFileVisitor<Path>() {
                // 访问文件时候触发该方法
                @Override
                public FileVisitResult visitFile(Path classFile, BasicFileAttributes attrs) {
                    String filename = classFile.getFileName().toString();
                    //logger.debug("filename = " + filename);
                    // 忽略文件
                    if (org.apache.commons.lang3.StringUtils.endsWith(filename.toLowerCase(), ".class")) {
                        String classPath = classFile.toAbsolutePath().toString();
                        //logger.debug("classPath = " + classPath + " , classpath = " + classpath);
                        String classname = org.apache.commons.lang3.StringUtils.substringAfterLast(classPath, classpath.toString()).substring(1);
                        //logger.debug("classname = " + classname);
                        classname = org.apache.commons.lang3.StringUtils.substringBeforeLast(classname, ".class");
                        //logger.debug("classname = " + classname);
                        classname = org.apache.commons.lang3.StringUtils.replace(classname, File.separator, ".");
                        //logger.debug("classname = " + classname);
                        Class<?> clazz = null;
                        try {
                            clazz = Class.forName(classname);
                        } catch (ClassNotFoundException e) {
                            logger.warn(e.getMessage(), e);
                        }
                        if (clazz != null) {
                            classes.add(clazz);
                        }
                    }

                    return FileVisitResult.CONTINUE;
                }

                // 开始访问目录时触发该方法
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    //logger.debug("dir : " + dir.toString());
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            logger.warn(e.getMessage(), e);
        }

        return classes;
    }

    public static Collection<Class<?>> getClassesByPackageName(final String classpath) {
        return getClassesByPackageName(Paths.get(classpath));
    }
}
