package io.geewit.utils.web.xml;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.geewit.utils.web.core.SpringContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;


/**
 * XML 工具类
 *
 * @author geewit
 * @since 2022-01-21
 */
@SuppressWarnings({"unused"})
public class XmlUtils {
    private final static Logger logger = LoggerFactory.getLogger(XmlUtils.class);

    private XmlUtils() {
    }

    private final static XmlMapper xmlMapper = xmlMapper();


    public static XmlMapper xmlMapper() {
        XmlMapper xmlMapper;
        try {
            //region 从spring中获取 XmlMapper
            xmlMapper = SpringContextUtil.getBean(XmlMapper.class);
            xmlMapper = xmlMapper.copy();
            xmlMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
            xmlMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            xmlMapper.enable(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS);
            xmlMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            return xmlMapper;
            //endregion
        } catch (Exception e) {
            final Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
            builder.createXmlMapper(true)
                    .featuresToDisable(
                            SerializationFeature.FAIL_ON_EMPTY_BEANS,
                            DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .featuresToEnable(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS)
                    .serializationInclusion(JsonInclude.Include.NON_NULL);
            xmlMapper = builder.build();
        }
        return xmlMapper;
    }

    public static String toXml(Object value) {
        try {
            return xmlMapper.writeValueAsString(value);
        } catch (Exception e) {
            logger.warn(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static String toXml(Object value, boolean useIgnore) {
        try {
            if(!useIgnore) {
                xmlMapper.disable(MapperFeature.USE_ANNOTATIONS);
            }
            return xmlMapper.writeValueAsString(value);
        } catch (Exception e) {
            logger.warn(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static String toXml(Object value, Class<?> jsonView) {
        try {
            boolean defaultViewInclusionEnabled = xmlMapper.isEnabled(MapperFeature.DEFAULT_VIEW_INCLUSION);
            if(defaultViewInclusionEnabled) {
                xmlMapper.disable(MapperFeature.DEFAULT_VIEW_INCLUSION);
            }
            String json = xmlMapper.writerWithView(jsonView).writeValueAsString(value);
            if(defaultViewInclusionEnabled) {
                xmlMapper.enable(MapperFeature.DEFAULT_VIEW_INCLUSION);
            }
            return json;
        } catch (Exception e) {
            logger.warn(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static <T> T fromXml(String xml, Class<T> valueType) {
        try {
            return xmlMapper.readValue(xml, valueType);
        } catch (Exception e) {
            logger.warn(e.getMessage() + ", xml : " + xml);
            throw new RuntimeException(e);
        }

    }

    public static <T> T fromXml(String xml, TypeReference<T> typeReference) {
        try {
            return xmlMapper.readValue(xml, typeReference);
        } catch (Exception e) {
            logger.warn(e.getMessage() + ", xml : " + xml);
            throw new RuntimeException(e);
        }
    }

    public static <T> T fromXml(String xml, JavaType javaType) {
        try {
            return xmlMapper.readValue(xml, javaType);
        } catch (Exception e) {
            logger.warn(e.getMessage() + ", xml : " + xml);
            throw new RuntimeException(e);
        }
    }

}
