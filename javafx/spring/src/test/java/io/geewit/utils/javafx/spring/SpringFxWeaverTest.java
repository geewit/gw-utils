package io.geewit.utils.javafx.spring;

import io.geewit.utils.javafx.spring.weaver.FxWeaver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ConfigurableApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SpringFxWeaverTest {

    @Mock
    private ConfigurableApplicationContext context;

    @Test
    void constructor() {
        SpringFxWeaver weaver = new SpringFxWeaver(context);
        assertNotNull(weaver);
        assertTrue(weaver instanceof FxWeaver);
    }
}
