package io.geewit.utils.core.jackson.databind.serializer;

import org.springframework.data.domain.Page;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

/**
 * org.springframework.data.domain.Page 序列化
 * Page serializer
 *
 * @author geewit
 */
@SuppressWarnings({"unused"})
public class JsonPageSerializer extends ValueSerializer<Page<?>> {

    private final ObjectMapper mapper;

    /**
     * 构造函数
     * @param mapper ObjectMapper
     */
    public JsonPageSerializer(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void serialize(Page<?> page,
                          JsonGenerator generator,
                          SerializationContext context) throws JacksonException {

        // Page 理论上不为空，这里防御性处理一下
        if (page == null) {
            generator.writeNull();   // 注意：是 JsonGenerator 上的方法
            return;
        }

        generator.writeStartObject();

        generator.writeName("size");
        generator.writeNumber(page.getSize());

        generator.writeName("number");
        generator.writeNumber(page.getNumber());

        generator.writeName("totalElements");
        generator.writeNumber(page.getTotalElements());

        generator.writeName("last");
        generator.writeBoolean(page.isLast());

        generator.writeName("totalPages");
        generator.writeNumber(page.getTotalPages());

        // sort 直接作为一个属性对象输出
        generator.writePOJOProperty("sort", page.getSort());

        generator.writeName("first");
        generator.writeBoolean(page.isFirst());

        generator.writeName("numberOfElements");
        generator.writeNumber(page.getNumberOfElements());

        generator.writeName("content");

        String json;
        Class<?> activeView = context.getActiveView();
        if (activeView != null) {
            // 有 JsonView 时使用 view
            json = mapper.writerWithView(activeView)
                    .writeValueAsString(page.getContent());
        } else {
            // 否则用默认配置
            json = mapper.writeValueAsString(page.getContent());
        }

        // 直接把 content 的 JSON 数组原样写入
        generator.writeRawValue(json);

        generator.writeEndObject();
    }
}
