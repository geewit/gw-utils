package io.geewit.utils.core.jackson.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Jackson 配置
 * @author geewit
 */
@ComponentScan("io.geewit.utils.core.jackson.databind.serializer")
@Configuration
public class GwJacksonConfig {
    private static final Logger logger = LoggerFactory.getLogger(GwJacksonConfig.class);

    /**
     * 构造函数
     */
    public GwJacksonConfig() {
        logger.info("JacksonConfig initializing");
    }
}
