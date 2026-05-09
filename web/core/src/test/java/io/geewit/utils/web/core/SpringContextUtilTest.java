package io.geewit.utils.web.core;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SpringContextUtilTest {

    @Test
    void getBean_withNameAndClass() {
        ApplicationContext ctx = mock(ApplicationContext.class);
        Map<String, String> beanMap = new HashMap<>();
        beanMap.put("testBean", "value");
        when(ctx.getBeansOfType(String.class)).thenReturn(beanMap);

        String result = SpringContextUtil.getBean("testBean", String.class, ctx);
        assertEquals("value", result);
    }

    @Test
    void getBean_withNameNotFound() {
        ApplicationContext ctx = mock(ApplicationContext.class);
        Map<String, String> beanMap = new HashMap<>();
        beanMap.put("otherBean", "value");
        when(ctx.getBeansOfType(String.class)).thenReturn(beanMap);

        String result = SpringContextUtil.getBean("missingBean", String.class, ctx);
        assertNull(result);
    }

    @Test
    void getBean_emptyMap() {
        ApplicationContext ctx = mock(ApplicationContext.class);
        when(ctx.getBeansOfType(String.class)).thenReturn(new HashMap<>());

        String result = SpringContextUtil.getBean("any", String.class, ctx);
        assertNull(result);
    }

    @Test
    void getBean_multipleBeans() {
        ApplicationContext ctx = mock(ApplicationContext.class);
        Map<String, String> beanMap = new HashMap<>();
        beanMap.put("bean1", "value1");
        beanMap.put("bean2", "value2");
        when(ctx.getBeansOfType(String.class)).thenReturn(beanMap);

        String result = SpringContextUtil.getBean(null, String.class, ctx);
        assertNotNull(result);
    }

    @Test
    void getBean_nullName() {
        ApplicationContext ctx = mock(ApplicationContext.class);
        Map<String, String> beanMap = new HashMap<>();
        beanMap.put("bean1", "value1");
        when(ctx.getBeansOfType(String.class)).thenReturn(beanMap);

        String result = SpringContextUtil.getBean(null, String.class, ctx);
        assertEquals("value1", result);
    }

    @Test
    void getBean_withIntegerClass() {
        ApplicationContext ctx = mock(ApplicationContext.class);
        Map<String, Integer> beanMap = new HashMap<>();
        beanMap.put("intBean", 42);
        when(ctx.getBeansOfType(Integer.class)).thenReturn(beanMap);

        Integer result = SpringContextUtil.getBean("intBean", Integer.class, ctx);
        assertEquals(42, result);
    }

    @Test
    void getBean_static_withNameAndClass() {
        ApplicationContext ctx = mock(ApplicationContext.class);
        Map<String, String> beanMap = new HashMap<>();
        beanMap.put("testBean", "value");
        when(ctx.getBeansOfType(String.class)).thenReturn(beanMap);

        // Set static application context via instance method
        SpringContextUtil util = new SpringContextUtil();
        util.setApplicationContext(ctx);

        String result = SpringContextUtil.getBean("testBean", String.class);
        assertEquals("value", result);
    }

    @Test
    void getBean_static_withClassOnly() {
        ApplicationContext ctx = mock(ApplicationContext.class);
        Map<String, String> beanMap = new HashMap<>();
        beanMap.put("bean1", "value1");
        when(ctx.getBeansOfType(String.class)).thenReturn(beanMap);

        // Set static application context via instance method
        SpringContextUtil util = new SpringContextUtil();
        util.setApplicationContext(ctx);

        String result = SpringContextUtil.getBean(String.class);
        assertEquals("value1", result);
    }

    @Test
    void setApplicationContext() {
        ApplicationContext ctx = mock(ApplicationContext.class);
        SpringContextUtil util = new SpringContextUtil();
        
        // Should not throw
        assertDoesNotThrow(() -> util.setApplicationContext(ctx));
    }
}