package io.geewit.utils.core.jackson.databind.serializer;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.JsonSerializer;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializerProvider;
import org.springframework.data.domain.Page;

import java.io.IOException;

/**
 * org.springframework.data.domain.Page 序列化
 * @author geewit
 */
@SuppressWarnings({"unused"})
public class JsonPageSerializer extends JsonSerializer<Page> {
    private final ObjectMapper mapper;

    public JsonPageSerializer(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void serialize(Page page, JsonGenerator generator, SerializerProvider provider) throws IOException {
        generator.writeStartObject();
        generator.writeFieldName("size");
        generator.writeNumber(page.getSize());

        generator.writeFieldName("number");
        generator.writeNumber(page.getNumber());
        generator.writeFieldName("totalElements");
        generator.writeNumber(page.getTotalElements());
        generator.writeFieldName("last");
        generator.writeBoolean(page.isLast());
        generator.writeFieldName("totalPages");
        generator.writeNumber(page.getTotalPages());
        generator.writeObjectField("sort", page.getSort());
        generator.writeFieldName("first");
        generator.writeBoolean(page.isFirst());
        generator.writeFieldName("numberOfElements");
        generator.writeNumber(page.getNumberOfElements());
        generator.writeFieldName("content");
        String json;
        if(provider.getActiveView() != null) {
            boolean defaultViewInclusionEnabled = mapper.isEnabled(MapperFeature.DEFAULT_VIEW_INCLUSION);
            if(defaultViewInclusionEnabled) {
                mapper.disable(MapperFeature.DEFAULT_VIEW_INCLUSION);
            }
            json = mapper.writerWithView(provider.getActiveView()).writeValueAsString(page.getContent());

            if(defaultViewInclusionEnabled) {
                mapper.enable(MapperFeature.DEFAULT_VIEW_INCLUSION);
            }
        } else {
            json = mapper.writeValueAsString(page.getContent());
        }
        generator.writeRawValue(json);
        generator.writeEndObject();
    }

}
