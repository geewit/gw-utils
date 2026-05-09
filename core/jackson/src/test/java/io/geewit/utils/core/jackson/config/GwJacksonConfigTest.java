package io.geewit.utils.core.jackson.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GwJacksonConfigTest {

    @Test
    void constructor_doesNotThrow() {
        assertDoesNotThrow(GwJacksonConfig::new);
    }
}
