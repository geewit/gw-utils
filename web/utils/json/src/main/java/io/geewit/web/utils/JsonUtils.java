package io.geewit.web.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.util.ArrayList;
import java.util.List;


/**
 * JSON 工具类
 *
 * @author geewit
 * @since 2015-05-18
 */
@SuppressWarnings({"unused"})
public class JsonUtils {
    private final static Logger logger = LoggerFactory.getLogger(JsonUtils.class);

    private JsonUtils() {
    }

    private final static ObjectMapper objectMapper = objectMapper();


    public static ObjectMapper objectMapper() {
        ObjectMapper objectMapper;
        try {
            //region 从spring中获取ObjectMapper
            objectMapper = SpringContextUtil.getBean(ObjectMapper.class);
            objectMapper = objectMapper.copy();
            objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
            objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            objectMapper.enable(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS);
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            return objectMapper;
            //endregion
        } catch (Exception e) {
            final Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
            builder.featuresToDisable(
                    SerializationFeature.FAIL_ON_EMPTY_BEANS,
                    DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
            );
            builder.featuresToEnable(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS);
            builder.serializationInclusion(JsonInclude.Include.NON_NULL);
            objectMapper = builder.build();
            SimpleModule simpleModule = new SimpleModule();
            simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
            simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
            objectMapper.registerModule(simpleModule);
        }
        return objectMapper;
    }

    public static String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            logger.warn(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static String toJson(Object value, boolean useIgnore) {
        try {
            if(!useIgnore) {
                objectMapper.disable(MapperFeature.USE_ANNOTATIONS);
            }
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            logger.warn(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static String toJson(Object value, Class<?> jsonView) {
        try {
            boolean defaultViewInclusionEnabled = objectMapper.isEnabled(MapperFeature.DEFAULT_VIEW_INCLUSION);
            if(defaultViewInclusionEnabled) {
                objectMapper.disable(MapperFeature.DEFAULT_VIEW_INCLUSION);
            }
            String json = objectMapper.writerWithView(jsonView).writeValueAsString(value);
            if(defaultViewInclusionEnabled) {
                objectMapper.enable(MapperFeature.DEFAULT_VIEW_INCLUSION);
            }
            return json;
        } catch (Exception e) {
            logger.warn(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static <T> T fromJson(String json, Class<T> valueType) {
        try {
            return objectMapper.readValue(json, valueType);
        } catch (Exception e) {
            logger.warn(e.getMessage() + ", json : " + json);
            throw new RuntimeException(e);
        }

    }

    public static <T> T fromJson(String json, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(json, typeReference);
        } catch (Exception e) {
            logger.warn(e.getMessage() + ", json : " + json);
            throw new RuntimeException(e);
        }
    }

    public static <T> T fromJson(String json, JavaType javaType) {
        try {
            return objectMapper.readValue(json, javaType);
        } catch (Exception e) {
            logger.warn(e.getMessage() + ", json : " + json);
            throw new RuntimeException(e);
        }
    }

    public static <T> List<T> toList(String json, Class<T> clazz) {
        TypeFactory factory = objectMapper.getTypeFactory();
        CollectionType javaType = factory.constructCollectionType(ArrayList.class, clazz);
        try {
            return objectMapper.readValue(json, javaType);
        } catch (Exception e) {
            logger.warn(e.getMessage() + ", json : " + json);
            throw new RuntimeException(e);
        }
    }
}
