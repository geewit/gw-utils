package io.geewit.utils.javafx.base;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(ApplicationExtension.class)
public class UniqueDialogManagerTest {

    @Test
    void identifier_shouldReturnClassNameWhenIdentifierIsNull() {
        String result = UniqueDialogManager.identifier(javafx.stage.Stage.class, null);

        assertThat(result).isEqualTo(javafx.stage.Stage.class.getName());
    }

    @Test
    void identifier_shouldCombineClassNameAndIdentifier() {
        String result = UniqueDialogManager.identifier(javafx.stage.Stage.class, "my-dialog");

        assertThat(result).isEqualTo(javafx.stage.Stage.class.getName() + "#my-dialog");
    }

    @Test
    void identifier_shouldHandleEmptyStringIdentifier() {
        String result = UniqueDialogManager.identifier(javafx.stage.Stage.class, "");

        assertThat(result).isEqualTo(javafx.stage.Stage.class.getName() + "#");
    }

    @Test
    void identifier_shouldHandleNumericIdentifier() {
        String result = UniqueDialogManager.identifier(javafx.stage.Stage.class, 42);

        assertThat(result).isEqualTo(javafx.stage.Stage.class.getName() + "#42");
    }
}
