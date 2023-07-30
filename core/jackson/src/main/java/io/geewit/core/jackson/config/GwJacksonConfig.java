package io.geewit.core.jackson.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author geewit
 */
@ComponentScan("io.geewit.core.jackson.databind.serializer")
@Configuration
public class GwJacksonConfig {
    private static final Logger logger = LoggerFactory.getLogger(GwJacksonConfig.class);

    public GwJacksonConfig() {
        logger.info("JacksonConfig initializing");
    }
}
