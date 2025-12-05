package io.geewit.utils.web.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.core.JacksonException;
import tools.jackson.core.json.JsonReadFeature;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.ext.javatime.deser.InstantDeserializer;
import tools.jackson.databind.ext.javatime.ser.InstantSerializer;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.ser.std.ToStringSerializer;
import tools.jackson.databind.type.CollectionType;
import tools.jackson.databind.type.TypeFactory;


import java.time.Instant;
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
    private final static Logger log = LoggerFactory.getLogger(JsonUtils.class);

    private JsonUtils() {
    }

    // Jackson 3 推荐使用 JsonMapper / Use JsonMapper (Jackson 3)
    private static final JsonMapper JSON_MAPPER = jsonMapper();

    /**
     * 创建并配置一个JsonMapper实例
     *
     * @return 配置好的JsonMapper实例
     */
    public static JsonMapper jsonMapper() {
        // 创建并配置JsonMapper基础特性
        JsonMapper mapper = JsonMapper.builder()
                // Serialization / Deserialization features
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                // 允许未转义控制字符 / allow unescaped control chars
                .configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS, true)
                // 枚举大小写不敏感 / case-insensitive enums
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                // Jackson 3 没有 serializationInclusion()，用 changeDefaultPropertyInclusion
                .changeDefaultPropertyInclusion(_ ->
                        JsonInclude.Value.construct(
                                JsonInclude.Include.NON_NULL,   // 序列化时忽略 null
                                JsonInclude.Include.ALWAYS      // 反序列化保持默认
                        )
                )
                .build();

        // 配置自定义序列化和反序列化器
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
        simpleModule.addSerializer(Instant.class, InstantSerializer.INSTANCE);
        simpleModule.addDeserializer(Instant.class, InstantDeserializer.INSTANT);

        // 注册自定义模块到mapper
        mapper = mapper.rebuild()
                .addModule(simpleModule)
                .build();

        // ParameterNamesModule 在 Jackson 3 已内置，可省略 / parameter-names support is built-in in Jackson 3
        return mapper;
    }

    /**
     * 将对象转换为JSON字符串
     *
     * @param value 需要转换的对象
     * @return 转换后的JSON字符串
     * @throws RuntimeException 当JSON转换失败时抛出
     */
    public static String toJson(Object value) {
        try {
            // 使用JSON_MAPPER将对象序列化为JSON字符串
            return JSON_MAPPER.writeValueAsString(value);
        } catch (JacksonException e) {
            // 记录警告日志并重新抛出运行时异常
            log.warn("{}, value : {}", e.getMessage(), value);
            throw new RuntimeException(e);
        }
    }

    /**
     * 将对象转换为JSON字符串，支持指定JSON视图过滤字段
     *
     * @param value 需要转换为JSON的对象
     * @param jsonView JSON视图类，用于控制序列化时包含的字段
     * @return 序列化后的JSON字符串
     */
    public static String toJson(Object value, Class<?> jsonView) {
        // Jackson 3 的 JsonMapper 不再有 copy()，使用 rebuild() 派生新实例
        // 创建新的JsonMapper实例并禁用默认视图包含功能，确保只序列化指定视图中的字段
        JsonMapper mapper = JSON_MAPPER.rebuild()
                .disable(MapperFeature.DEFAULT_VIEW_INCLUSION)
                .build();

        try {
            return mapper.writerWithView(jsonView).writeValueAsString(value);
        } catch (JacksonException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将JSON字符串转换为指定类型的对象
     *
     * @param json 需要转换的JSON字符串
     * @param valueType 目标对象的Class类型
     * @param <T> 目标对象类型
     * @return 转换后的目标类型对象
     * @throws RuntimeException 当JSON解析失败时抛出
     */
    public static <T> T fromJson(String json, Class<T> valueType) {
        try {
            // 使用JSON_MAPPER将JSON字符串反序列化为指定类型的对象
            return JSON_MAPPER.readValue(json, valueType);
        } catch (JacksonException e) {
            // 记录警告日志并重新抛出运行时异常
            log.warn("{}, json : {}", e.getMessage(), json);
            throw new RuntimeException(e);
        }
    }

    /**
     * 将JSON字符串转换为指定类型的对象
     *
     * @param json JSON字符串
     * @param typeReference 目标对象的类型引用
     * @param <T> 目标对象类型
     * @return 转换后的对象
     * @throws RuntimeException 当JSON解析失败时抛出
     */
    public static <T> T fromJson(String json, TypeReference<T> typeReference) {
        try {
            // 使用JSON_MAPPER将JSON字符串解析为指定类型的对象
            return JSON_MAPPER.readValue(json, typeReference);
        } catch (JacksonException e) {
            // 记录警告日志并重新抛出运行时异常
            log.warn("{}, json : {}", e.getMessage(), json);
            throw new RuntimeException(e);
        }
    }

    /**
     * 将JSON字符串转换为指定类型的对象
     *
     * @param json 需要转换的JSON字符串
     * @param javaType 目标对象的Java类型信息
     * @param <T> 目标对象类型
     * @return 转换后的对象实例
     * @throws RuntimeException 当JSON解析失败时抛出运行时异常
     */
    public static <T> T fromJson(String json, JavaType javaType) {
        try {
            // 使用Jackson ObjectMapper将JSON字符串反序列化为指定类型的对象
            return JSON_MAPPER.readValue(json, javaType);
        } catch (JacksonException e) {
            // 记录警告日志并重新抛出为运行时异常
            log.warn("{}, json : {}", e.getMessage(), json);
            throw new RuntimeException(e);
        }
    }

    /**
     * 将JSON字符串转换为指定类型的List集合
     *
     * @param json 待转换的JSON字符串
     * @param clazz List中元素的类型Class对象
     * @param <T> 集合中元素的类型
     * @return 转换后的List集合
     * @throws RuntimeException 当JSON解析失败时抛出运行时异常
     */
    public static <T> List<T> toList(String json, Class<T> clazz) {
        // 构造ArrayList类型的集合类型信息
        TypeFactory factory = JSON_MAPPER.getTypeFactory();
        CollectionType javaType = factory.constructCollectionType(ArrayList.class, clazz);
        try {
            // 执行JSON反序列化操作
            return JSON_MAPPER.readValue(json, javaType);
        } catch (JacksonException e) {
            // 记录警告日志并抛出运行时异常
            log.warn("{}, json : {}", e.getMessage(), json);
            throw new RuntimeException(e);
        }
    }
}
