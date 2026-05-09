package io.geewit.utils.core.jackson.databind.serializer;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationContext;

import java.io.StringWriter;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class JsonPageSerializerTest {

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

        SimplePage(List<T> content, int number, int size, long totalElements) {
            this.content = content;
            this.number = number;
            this.size = size;
            this.totalElements = totalElements;
        }

        @Override public int getTotalPages() { return (int) Math.ceil((double) totalElements / size); }
        @Override public long getTotalElements() { return totalElements; }
        @Override public int getNumber() { return number; }
        @Override public int getSize() { return size; }
        @Override public int getNumberOfElements() { return content.size(); }
        @Override public List<T> getContent() { return content; }
        @Override public boolean hasContent() { return !content.isEmpty(); }
        @Override public Sort getSort() { return Sort.unsorted(); }
        @Override public boolean isFirst() { return number == 0; }
        @Override public boolean isLast() { return number >= getTotalPages() - 1; }
        @Override public boolean hasNext() { return !isLast(); }
        @Override public boolean hasPrevious() { return number > 0; }
        @Override public Pageable nextPageable() { return null; }
        @Override public Pageable previousPageable() { return null; }
        @Override public <U> Page<U> map(Function<? super T, ? extends U> converter) { return null; }
        @Override public Iterator<T> iterator() { return content.iterator(); }
    }

    @Test
    void serialize_nonNullPage() throws Exception {
        StringWriter writer = new StringWriter();
        JsonGenerator gen = createGenerator(writer);
        JsonPageSerializer serializer = new JsonPageSerializer(mapper);
        Page<String> page = new SimplePage<>(List.of("a", "b"), 0, 10, 2);
        serializer.serialize(page, gen, createContext());
        gen.close();
        String result = writer.toString();
        assertTrue(result.contains("\"size\":10"));
        assertTrue(result.contains("\"number\":0"));
        assertTrue(result.contains("\"totalElements\":2"));
        assertTrue(result.contains("\"content\":[\"a\",\"b\"]"));
    }

    @Test
    void serialize_nullPage() throws Exception {
        StringWriter writer = new StringWriter();
        JsonGenerator gen = createGenerator(writer);
        JsonPageSerializer serializer = new JsonPageSerializer(mapper);
        serializer.serialize(null, gen, createContext());
        gen.close();
        assertEquals("null", writer.toString().trim());
    }
}
