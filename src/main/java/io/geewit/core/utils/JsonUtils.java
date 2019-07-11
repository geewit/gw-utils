package io.geewit.core.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;


/**
 * JSON 工具类
 *
 * @author geewit
 * @since 2015-05-18
 */
@SuppressWarnings({"unused"})
public class JsonUtils {
    private final static Logger logger = LoggerFactory.getLogger(JsonUtils.class);

    private static ObjectMapper objectMapper;

    private static ObjectMapper objectMapper() {
        if (objectMapper != null) {
            return objectMapper;
        }
        try {
            //region 从spring中获取ObjectMapper
            objectMapper = SpringContextUtil.getBean(ObjectMapper.class);
            //endregion
        } catch (Exception e) {
            final Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
            builder.featuresToDisable(
                    SerializationFeature.FAIL_ON_EMPTY_BEANS,
                    DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
            );
            builder.serializationInclusion(JsonInclude.Include.NON_NULL);
            objectMapper = builder.build();
        }
        return objectMapper;
    }

    public static String toJson(Object value) {
        try {
            return objectMapper().writeValueAsString(value);
        } catch (Exception e) {
            logger.warn(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static String toJson(Object value, Class<?> jsonView) {
        try {
            objectMapper = objectMapper();
            objectMapper.disable(MapperFeature.DEFAULT_VIEW_INCLUSION);
            String json = objectMapper.writerWithView(jsonView).writeValueAsString(value);
            objectMapper.enable(MapperFeature.DEFAULT_VIEW_INCLUSION);
            return json;
        } catch (Exception e) {
            logger.warn(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static <T> T fromJson(String json, Class<T> valueType) {
        try {
            return objectMapper().readValue(json, valueType);
        } catch (Exception e) {
            logger.warn(e.getMessage() + ", json : " + json);
            throw new RuntimeException(e);
        }

    }

    public static <T> T fromJson(String json, TypeReference<T> typeReference) {
        try {
            return objectMapper().readValue(json, typeReference);
        } catch (Exception e) {
            logger.warn(e.getMessage() + ", json : " + json);
            throw new RuntimeException(e);
        }

    }

    public static <T> T fromJson(String json, JavaType javaType) {
        try {
            return objectMapper().readValue(json, javaType);
        } catch (Exception e) {
            logger.warn(e.getMessage() + ", json : " + json);
            throw new RuntimeException(e);
        }
    }
}
