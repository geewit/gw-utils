package io.geewit.utils.core.jackson.databind.serializer;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.json.JsonMapper;

import java.io.StringWriter;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Supplementary tests for JsonPageSerializer to improve coverage.
 */
class JsonPageSerializerSupplementTest {

    private final ObjectMapper mapper = new ObjectMapper();

    private JsonGenerator createGenerator(StringWriter writer) throws Exception {
        return mapper.createGenerator(writer);
    }

    private SerializationContext createContext() {
        return mapper._serializationContext();
    }

    static class SimplePage<T> implements Page<T> {
        private final List<T> content;
        private final int number;
        private final int size;
        private final long totalElements;
        private final Sort sort;

        SimplePage(List<T> content, int number, int size, long totalElements) {
            this(content, number, size, totalElements, Sort.unsorted());
        }

        SimplePage(List<T> content, int number, int size, long totalElements, Sort sort) {
            this.content = content;
            this.number = number;
            this.size = size;
            this.totalElements = totalElements;
            this.sort = sort;
        }

        @Override public int getTotalPages() { return (int) Math.ceil((double) totalElements / size); }
        @Override public long getTotalElements() { return totalElements; }
        @Override public int getNumber() { return number; }
        @Override public int getSize() { return size; }
        @Override public int getNumberOfElements() { return content == null ? 0 : content.size(); }
        @Override public List<T> getContent() { return content; }
        @Override public boolean hasContent() { return content != null && !content.isEmpty(); }
        @Override public Sort getSort() { return sort; }
        @Override public boolean isFirst() { return number == 0; }
        @Override public boolean isLast() { return number >= getTotalPages() - 1; }
        @Override public boolean hasNext() { return !isLast(); }
        @Override public boolean hasPrevious() { return number > 0; }
        @Override public Pageable nextPageable() { return null; }
        @Override public Pageable previousPageable() { return null; }
        @Override public <U> Page<U> map(Function<? super T, ? extends U> converter) { return null; }
        @Override public Iterator<T> iterator() { return content == null ? Collections.emptyIterator() : content.iterator(); }
    }

    @Test
    void serialize_emptyPage() throws Exception {
        StringWriter writer = new StringWriter();
        JsonGenerator gen = createGenerator(writer);
        JsonPageSerializer serializer = new JsonPageSerializer(mapper);
        Page<String> page = new SimplePage<>(Collections.emptyList(), 0, 10, 0);
        serializer.serialize(page, gen, createContext());
        gen.close();
        String result = writer.toString();
        assertTrue(result.contains("\"size\":10"));
        assertTrue(result.contains("\"number\":0"));
        assertTrue(result.contains("\"totalElements\":0"));
        assertTrue(result.contains("\"totalPages\":0"));
        assertTrue(result.contains("\"content\":[]"));
    }

    @Test
    void serialize_secondPage() throws Exception {
        StringWriter writer = new StringWriter();
        JsonGenerator gen = createGenerator(writer);
        JsonPageSerializer serializer = new JsonPageSerializer(mapper);
        // 5 items, size 2, page 2 (0-indexed), so pages = 3, isLast = true
        Page<String> page = new SimplePage<>(List.of("e"), 2, 2, 5);
        serializer.serialize(page, gen, createContext());
        gen.close();
        String result = writer.toString();
        assertTrue(result.contains("\"number\":2"));
        assertTrue(result.contains("\"totalPages\":3"));
        assertTrue(result.contains("\"last\":true"));
        assertTrue(result.contains("\"first\":false"));
    }

    @Test
    void serialize_firstPage() throws Exception {
        StringWriter writer = new StringWriter();
        JsonGenerator gen = createGenerator(writer);
        JsonPageSerializer serializer = new JsonPageSerializer(mapper);
        Page<String> page = new SimplePage<>(List.of("a", "b"), 0, 2, 5);
        serializer.serialize(page, gen, createContext());
        gen.close();
        String result = writer.toString();
        assertTrue(result.contains("\"number\":0"));
        assertTrue(result.contains("\"first\":true"));
        assertTrue(result.contains("\"last\":false"));
    }

    @Test
    void serialize_pageWithSort() throws Exception {
        StringWriter writer = new StringWriter();
        JsonGenerator gen = createGenerator(writer);
        JsonPageSerializer serializer = new JsonPageSerializer(mapper);
        Sort sort = Sort.by(Sort.Direction.DESC, "name");
        Page<String> page = new SimplePage<>(List.of("b", "a"), 0, 10, 2, sort);
        serializer.serialize(page, gen, createContext());
        gen.close();
        String result = writer.toString();
        assertTrue(result.contains("\"sort\""));
    }

    @Test
    void serialize_pageWithNullContent() throws Exception {
        StringWriter writer = new StringWriter();
        JsonGenerator gen = createGenerator(writer);
        JsonPageSerializer serializer = new JsonPageSerializer(mapper);
        Page<String> page = new SimplePage<>(null, 0, 10, 0);
        serializer.serialize(page, gen, createContext());
        gen.close();
        String result = writer.toString();
        assertTrue(result.contains("\"numberOfElements\":0"));
    }

    @Test
    void serialize_pageWithLargeTotalElements() throws Exception {
        StringWriter writer = new StringWriter();
        JsonGenerator gen = createGenerator(writer);
        JsonPageSerializer serializer = new JsonPageSerializer(mapper);
        Page<String> page = new SimplePage<>(List.of("item"), 0, 10, 1000000);
        serializer.serialize(page, gen, createContext());
        gen.close();
        String result = writer.toString();
        assertTrue(result.contains("\"totalElements\":1000000"));
        assertTrue(result.contains("\"totalPages\":100000"));
    }

    @Test
    void constructor_withJsonMapper() throws Exception {
        // Test that JsonPageSerializer works with JsonMapper (builder pattern)
        JsonMapper jsonMapper = JsonMapper.builder().build();
        JsonPageSerializer serializer = new JsonPageSerializer(jsonMapper);
        assertNotNull(serializer);
    }
}
