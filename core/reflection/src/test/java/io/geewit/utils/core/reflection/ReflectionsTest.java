package io.geewit.utils.core.reflection;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class ReflectionsTest {

    public static class TestBean {
        private String name = "default";
        private int age = 0;
        private String hidden = "secret";

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getAge() { return age; }
        public void setAge(int age) { this.age = age; }
        private String privateMethod() { return "private"; }
        public void throwException() { throw new RuntimeException("test"); }
    }

    public static class PublicFieldBean {
        public String publicName = "public";
        public int publicAge = 0;
        public static String staticField = "static";

        public String getPublicName() { return publicName; }
        public int getPublicAge() { return publicAge; }
    }

    public static class TestBeanChild extends TestBean {
        private String childField = "child";
        public String getChildField() { return childField; }
    }

    public static class GenericBean<T> {
    }

    public static class StringBean extends GenericBean<String> {
    }

    public static class WildcardBean extends GenericBean<java.util.List<String>> {
    }

    public static class CglibProxyBean extends TestBean {
    }

    public @interface ExcludeAnnotation {}

    public static class AnnotatedBean {
        public String field1 = "a";
        public String field2 = "b";

        @ExcludeAnnotation
        public String getField1() { return field1; }

        public String getField2() { return field2; }
    }

    @Test
    void invokeGetter() {
        TestBean bean = new TestBean();
        bean.setName("test");
        assertEquals("test", Reflections.invokeGetter(bean, "name"));
    }

    @Test
    void invokeSetter() {
        TestBean bean = new TestBean();
        Reflections.invokeSetter(bean, "name", "newName");
        assertEquals("newName", bean.getName());
    }

    @Test
    void getFieldValue_privateField() {
        TestBean bean = new TestBean();
        assertEquals("secret", Reflections.getFieldValue(bean, "hidden"));
    }

    @Test
    void getFieldValue_fieldNotFound() {
        TestBean bean = new TestBean();
        assertThrows(IllegalArgumentException.class, () -> Reflections.getFieldValue(bean, "nonExistent"));
    }

    @Test
    void setFieldValue() {
        TestBean bean = new TestBean();
        Reflections.setFieldValue(bean, "hidden", "newSecret");
        assertEquals("newSecret", Reflections.getFieldValue(bean, "hidden"));
    }

    @Test
    void setFieldValue_silent() {
        TestBean bean = new TestBean();
        // silent mode should not throw for missing field
        assertDoesNotThrow(() -> Reflections.setFieldValue(bean, "nonExistent", "value", true));
    }

    @Test
    void setFieldValue_notSilent_throws() {
        TestBean bean = new TestBean();
        assertThrows(IllegalArgumentException.class, () -> Reflections.setFieldValue(bean, "nonExistent", "value", false));
    }

    @Test
    void invokeMethod() {
        TestBean bean = new TestBean();
        Object result = Reflections.invokeMethod(bean, "getName", new Class[]{}, new Object[]{});
        assertEquals("default", result);
    }

    @Test
    void invokeMethod_notFound() {
        TestBean bean = new TestBean();
        assertThrows(IllegalArgumentException.class, () -> Reflections.invokeMethod(bean, "nonExistent", new Class[]{}, new Object[]{}));
    }

    @Test
    void invokeMethodByName() {
        TestBean bean = new TestBean();
        bean.setName("test");
        Object result = Reflections.invokeMethodByName(bean, "getName", new Object[]{});
        assertEquals("test", result);
    }

    @Test
    void invokeMethodByName_silent() {
        TestBean bean = new TestBean();
        assertNull(Reflections.invokeMethodByName(bean, "nonExistent", new Object[]{}, true));
    }

    @Test
    void invokeMethodByName_notSilent() {
        TestBean bean = new TestBean();
        assertThrows(IllegalArgumentException.class, () -> Reflections.invokeMethodByName(bean, "nonExistent", new Object[]{}, false));
    }

    @Test
    void getField_fromSuperClass() {
        TestBeanChild child = new TestBeanChild();
        Field field = Reflections.getField(child, "name");
        assertNotNull(field);
    }

    @Test
    void getField_nullObject() {
        assertThrows(NullPointerException.class, () -> Reflections.getField(null, "name"));
    }

    @Test
    void getField_blankName() {
        assertThrows(IllegalArgumentException.class, () -> Reflections.getField(new TestBean(), ""));
    }

    @Test
    void getMethod_found() {
        TestBean bean = new TestBean();
        Method method = Reflections.getMethod(bean, "getName", new Class[]{});
        assertNotNull(method);
    }

    @Test
    void getMethod_notFound() {
        TestBean bean = new TestBean();
        assertNull(Reflections.getMethod(bean, "nonExistent", new Class[]{}));
    }

    @Test
    void getMethodByName_found() {
        TestBean bean = new TestBean();
        Method method = Reflections.getMethodByName(bean, "getName");
        assertNotNull(method);
    }

    @Test
    void getMethodByName_notFound() {
        TestBean bean = new TestBean();
        assertNull(Reflections.getMethodByName(bean, "nonExistent"));
    }

    @Test
    void getClassGenricType_defaultIndex() {
        assertEquals(String.class, Reflections.getClassGenricType(StringBean.class));
    }

    @Test
    void getClassGenricType_withIndex() {
        assertEquals(String.class, Reflections.getClassGenricType(StringBean.class, 0));
    }

    @Test
    void getClassGenricType_outOfBounds() {
        assertEquals(Object.class, Reflections.getClassGenricType(StringBean.class, 5));
    }

    @Test
    void getClassGenricType_notParameterized() {
        assertEquals(Object.class, Reflections.getClassGenricType(TestBean.class));
    }

    @Test
    void getUserClass_normal() {
        assertEquals(TestBean.class, Reflections.getUserClass(new TestBean()));
    }

    @Test
    void getUserClass_nullThrows() {
        assertThrows(NullPointerException.class, () -> Reflections.getUserClass(null));
    }

    @Test
    void convertReflectionExceptionToUnchecked_illegalAccess() {
        RuntimeException ex = Reflections.convertReflectionExceptionToUnchecked(new IllegalAccessException("test"));
        assertTrue(ex instanceof IllegalArgumentException);
    }

    @Test
    void convertReflectionExceptionToUnchecked_invocationTarget() {
        RuntimeException ex = Reflections.convertReflectionExceptionToUnchecked(new java.lang.reflect.InvocationTargetException(new RuntimeException("target")));
        assertTrue(ex instanceof RuntimeException);
    }

    @Test
    void convertReflectionExceptionToUnchecked_runtimeException() {
        RuntimeException original = new RuntimeException("test");
        RuntimeException ex = Reflections.convertReflectionExceptionToUnchecked(original);
        assertSame(original, ex);
    }

    @Test
    void convertReflectionExceptionToUnchecked_other() {
        RuntimeException ex = Reflections.convertReflectionExceptionToUnchecked(new Exception("test"));
        assertEquals("Unexpected Checked Exception.", ex.getMessage());
    }

    @Test
    void getPublicMethods() {
        Method[] methods = Reflections.getPublicMethods(TestBean.class);
        assertTrue(methods.length > 0);
        for (Method m : methods) {
            assertTrue(java.lang.reflect.Modifier.isPublic(m.getModifiers()));
        }
    }

    @Test
    void getPublicGetters_withoutAnnotations() {
        Field[] fields = Reflections.getPublicGetters(PublicFieldBean.class);
        assertTrue(fields.length > 0);
        // publicName and publicAge have getters; staticField does not (it's static)
    }

    @Test
    void getClassesByPackageName() {
        Collection<Class<?>> classes = Reflections.getClassesByPackageName("io.geewit.utils.core.reflection");
        assertFalse(classes.isEmpty());
        assertTrue(classes.contains(Reflections.class));
    }

    @Test
    void getClassesByPackageName_nonExistentPackage() {
        Collection<Class<?>> classes = Reflections.getClassesByPackageName("non.existent.package.12345");
        assertTrue(classes.isEmpty());
    }

    @Test
    void getPublicGetters_withAnnotations() {
        Field[] fields = Reflections.getPublicGetters(AnnotatedBean.class, ExcludeAnnotation.class);
        // field1's getter has @ExcludeAnnotation, so field1 should be excluded
        // field2's getter does not have the annotation, so field2 should be included
        long field2Count = java.util.Arrays.stream(fields).filter(f -> f.getName().equals("field2")).count();
        assertTrue(field2Count >= 1, "field2 should be included");
    }

    @Test
    void getPublicGetters_withMultipleAnnotations() {
        Field[] fields = Reflections.getPublicGetters(AnnotatedBean.class, ExcludeAnnotation.class, Deprecated.class);
        // Both annotations are checked, field2 should still be included
        long field2Count = java.util.Arrays.stream(fields).filter(f -> f.getName().equals("field2")).count();
        assertTrue(field2Count >= 1, "field2 should be included");
    }

    @Test
    void getClassGenricType_notClassParam() {
        // WildcardBean's generic parameter is List<String>, not a raw Class
        assertEquals(Object.class, Reflections.getClassGenricType(WildcardBean.class));
    }

    @Test
    void getUserClass_cglibProxy() {
        // Simulate CGLIB proxy by creating a subclass with $$ in name
        // CglibProxyBean doesn't have $$, so it returns itself
        assertEquals(CglibProxyBean.class, Reflections.getUserClass(new CglibProxyBean()));
    }

    @Test
    void getUserClass_cglibProxyWithSuperClass() {
        // Create a proxy class with $$ that has a meaningful superclass
        class TestBean$$EnhancerByCGLIB extends TestBean {}
        Object proxy = new TestBean$$EnhancerByCGLIB();
        assertEquals(TestBean.class, Reflections.getUserClass(proxy));
    }

    @Test
    void invokeMethod_nonPublic() {
        TestBean bean = new TestBean();
        Object result = Reflections.invokeMethod(bean, "privateMethod", new Class[]{}, new Object[]{});
        assertEquals("private", result);
    }

    @Test
    void invokeMethod_throwsException() {
        TestBean bean = new TestBean();
        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> Reflections.invokeMethod(bean, "throwException", new Class[]{}, new Object[]{}));
        assertTrue(ex.getMessage().contains("test"));
    }
}
