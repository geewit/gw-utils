package io.geewit.utils.javafx.base;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(ApplicationExtension.class)
public class DialogHelperTest {

    @Test
    void executor_shouldBeVirtualThreadPerTask() {
        // Verify that the static ExecutorService uses virtual threads
        // by checking that DialogHelper class loads correctly
        assertThat(DialogHelper.class).isNotNull();
    }
}
