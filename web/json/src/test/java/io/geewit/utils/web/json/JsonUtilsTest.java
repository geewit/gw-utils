package io.geewit.utils.web.json;

import com.fasterxml.jackson.annotation.JsonView;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive JUnit 5 tests for JsonUtils
 */
@DisplayName("JsonUtils Tests")
class JsonUtilsTest {

    // ===== DTO Classes for Testing =====

    static class SimpleDto {
        public String name;
        public int age;

        public SimpleDto() {}

        public SimpleDto(String name, int age) {
            this.name = name;
            this.age = age;
        }
    }

    static class PersonDto {
        public String name;
        public int age;
        public String email;

        public PersonDto() {}

        public PersonDto(String name, int age, String email) {
            this.name = name;
            this.age = age;
            this.email = email;
        }
    }

    static class WithNullableFields {
        public String name;
        public String nullableField;
        public Integer count;

        public WithNullableFields() {}

        public WithNullableFields(String name, String nullableField, Integer count) {
            this.name = name;
            this.nullableField = nullableField;
            this.count = count;
        }
    }

    static class WithLongValue {
        public String name;
        public Long longValue;

        public WithLongValue() {}

        public WithLongValue(String name, Long longValue) {
            this.name = name;
            this.longValue = longValue;
        }
    }

    static class WithInstantValue {
        public String eventName;
        public Instant timestamp;

        public WithInstantValue() {}

        public WithInstantValue(String eventName, Instant timestamp) {
            this.eventName = eventName;
            this.timestamp = timestamp;
        }
    }

    // JSON View classes
    static class Views {
        static class Public {}
        static class Internal extends Public {}
    }

    static class ViewDto {
        @JsonView(Views.Public.class)
        public String name;

        @JsonView(Views.Internal.class)
        public String internalData;

        public ViewDto() {}

        public ViewDto(String name, String internalData) {
            this.name = name;
            this.internalData = internalData;
        }
    }

    // Classes for Edge Cases tests (must be static top-level to be serializable)
    static class BooleanDto {
        public boolean active;
        public Boolean boxedActive;
    }

    static class NestedOuter {
        public String outerName;
        public SimpleDto inner;
    }

    // Class with self-reference to trigger JacksonException in toJson
    static class CyclicReference {
        public String name;
        public CyclicReference self;

        public CyclicReference() {}

        public CyclicReference(String name) {
            this.name = name;
        }
    }

    @Nested
    @DisplayName("jsonMapper() Tests")
    class JsonMapperTests {

        @Test
        @DisplayName("should return non-null JsonMapper instance")
        void shouldReturnNonNullJsonMapper() {
            JsonMapper mapper = JsonUtils.jsonMapper();
            assertNotNull(mapper);
        }

        @Test
        @DisplayName("should return configured JsonMapper with expected features")
        void shouldReturnConfiguredJsonMapper() {
            JsonMapper mapper = JsonUtils.jsonMapper();
            assertNotNull(mapper);
            // Verify mapper can be used
            String json = JsonUtils.toJson(new SimpleDto("test", 25));
            assertNotNull(json);
            assertTrue(json.contains("test"));
            assertTrue(json.contains("25"));
        }
    }

    @Nested
    @DisplayName("toJson(Object) Tests")
    class ToJsonObjectTests {

        @Test
        @DisplayName("should serialize simple object to JSON string")
        void shouldSerializeSimpleObject() {
            SimpleDto dto = new SimpleDto("Alice", 30);
            String json = JsonUtils.toJson(dto);
            assertNotNull(json);
            assertTrue(json.contains("\"name\""));
            assertTrue(json.contains("\"Alice\""));
            assertTrue(json.contains("\"age\""));
            assertTrue(json.contains("30"));
        }

        @Test
        @DisplayName("should serialize object with all fields")
        void shouldSerializeObjectWithAllFields() {
            PersonDto person = new PersonDto("Bob", 25, "bob@example.com");
            String json = JsonUtils.toJson(person);
            assertTrue(json.contains("Bob"));
            assertTrue(json.contains("25"));
            assertTrue(json.contains("bob@example.com"));
        }

        @Test
        @DisplayName("should exclude null values from JSON")
        void shouldExcludeNullValues() {
            WithNullableFields dto = new WithNullableFields("Test", null, null);
            String json = JsonUtils.toJson(dto);
            assertTrue(json.contains("\"name\""));
            assertTrue(json.contains("\"Test\""));
            assertFalse(json.contains("nullableField"));
            assertFalse(json.contains("count"));
        }

        @Test
        @DisplayName("should serialize Long as string")
        void shouldSerializeLongAsString() {
            WithLongValue dto = new WithLongValue("test", 1234567890123456789L);
            String json = JsonUtils.toJson(dto);
            // Long values should be serialized as strings due to ToStringSerializer
            assertTrue(json.contains("\"1234567890123456789\""));
        }

        @Test
        @DisplayName("should serialize Instant correctly")
        void shouldSerializeInstant() {
            Instant instant = Instant.parse("2024-01-15T10:30:00.000Z");
            WithInstantValue dto = new WithInstantValue("testEvent", instant);
            String json = JsonUtils.toJson(dto);
            assertTrue(json.contains("2024-01-15T10:30:00"));
        }

        @Test
        @DisplayName("should serialize list of objects")
        void shouldSerializeList() {
            List<SimpleDto> list = new ArrayList<>();
            list.add(new SimpleDto("One", 1));
            list.add(new SimpleDto("Two", 2));
            String json = JsonUtils.toJson(list);
            assertTrue(json.contains("One"));
            assertTrue(json.contains("Two"));
            assertTrue(json.contains("1"));
            assertTrue(json.contains("2"));
        }

        @Test
        @DisplayName("should serialize map")
        void shouldSerializeMap() {
            Map<String, Object> map = Map.of("key1", "value1", "key2", 42);
            String json = JsonUtils.toJson(map);
            assertTrue(json.contains("key1"));
            assertTrue(json.contains("value1"));
            assertTrue(json.contains("key2"));
            assertTrue(json.contains("42"));
        }
    }

    @Nested
    @DisplayName("toJson(Object, Class<?>) Tests - JSON View Filtering")
    class ToJsonWithViewTests {

        @Test
        @DisplayName("should filter fields based on JSON view - only public fields")
        void shouldFilterFieldsBasedOnView() {
            ViewDto dto = new ViewDto("PublicName", "InternalSecret");
            String json = JsonUtils.toJson(dto, Views.Public.class);
            assertTrue(json.contains("PublicName"));
            assertFalse(json.contains("InternalSecret"));
            assertFalse(json.contains("internalData"));
        }

        @Test
        @DisplayName("should include internal fields when using internal view")
        void shouldIncludeInternalFieldsWithInternalView() {
            ViewDto dto = new ViewDto("PublicName", "InternalSecret");
            String json = JsonUtils.toJson(dto, Views.Internal.class);
            assertTrue(json.contains("PublicName"));
            assertTrue(json.contains("InternalSecret"));
        }
    }

    @Nested
    @DisplayName("fromJson(String, Class<T>) Tests")
    class FromJsonClassTests {

        @Test
        @DisplayName("should deserialize JSON to simple object")
        void shouldDeserializeToSimpleObject() {
            String json = "{\"name\":\"Charlie\",\"age\":35}";
            SimpleDto dto = JsonUtils.fromJson(json, SimpleDto.class);
            assertNotNull(dto);
            assertEquals("Charlie", dto.name);
            assertEquals(35, dto.age);
        }

        @Test
        @DisplayName("should deserialize JSON with all fields")
        void shouldDeserializeJsonWithAllFields() {
            String json = "{\"name\":\"David\",\"age\":40,\"email\":\"david@example.com\"}";
            PersonDto person = JsonUtils.fromJson(json, PersonDto.class);
            assertEquals("David", person.name);
            assertEquals(40, person.age);
            assertEquals("david@example.com", person.email);
        }

        @Test
        @DisplayName("should handle JSON with extra unknown fields (ignored)")
        void shouldHandleUnknownFields() {
            String json = "{\"name\":\"Eve\",\"age\":28,\"unknownField\":\"ignored\"}";
            SimpleDto dto = JsonUtils.fromJson(json, SimpleDto.class);
            assertEquals("Eve", dto.name);
            assertEquals(28, dto.age);
        }

        @Test
        @DisplayName("should throw RuntimeException for invalid JSON")
        void shouldThrowRuntimeExceptionForInvalidJson() {
            String invalidJson = "{ invalid json }";
            assertThrows(RuntimeException.class, () -> JsonUtils.fromJson(invalidJson, SimpleDto.class));
        }

        @Test
        @DisplayName("should throw RuntimeException for JSON with type mismatch")
        void shouldThrowRuntimeExceptionForTypeMismatch() {
            String json = "{\"name\":\"Frank\",\"age\":\"not-a-number\"}";
            assertThrows(RuntimeException.class, () -> JsonUtils.fromJson(json, SimpleDto.class));
        }
    }

    @Nested
    @DisplayName("fromJson(String, TypeReference<T>) Tests")
    class FromJsonTypeReferenceTests {

        @Test
        @DisplayName("should deserialize JSON to list using TypeReference")
        void shouldDeserializeToListUsingTypeReference() {
            String json = "[{\"name\":\"Grace\",\"age\":32},{\"name\":\"Henry\",\"age\":45}]";
            TypeReference<List<SimpleDto>> typeRef = new TypeReference<>() {};
            List<SimpleDto> list = JsonUtils.fromJson(json, typeRef);
            assertEquals(2, list.size());
            assertEquals("Grace", list.get(0).name);
            assertEquals("Henry", list.get(1).name);
        }

        @Test
        @DisplayName("should deserialize JSON to map using TypeReference")
        void shouldDeserializeToMapUsingTypeReference() {
            String json = "{\"person1\":{\"name\":\"Ivy\",\"age\":25},\"person2\":{\"name\":\"Jack\",\"age\":30}}";
            TypeReference<Map<String, SimpleDto>> typeRef = new TypeReference<>() {};
            Map<String, SimpleDto> map = JsonUtils.fromJson(json, typeRef);
            assertEquals(2, map.size());
            assertEquals("Ivy", map.get("person1").name);
            assertEquals("Jack", map.get("person2").name);
        }

        @Test
        @DisplayName("should deserialize empty list")
        void shouldDeserializeEmptyList() {
            String json = "[]";
            TypeReference<List<SimpleDto>> typeRef = new TypeReference<>() {};
            List<SimpleDto> list = JsonUtils.fromJson(json, typeRef);
            assertTrue(list.isEmpty());
        }

        @Test
        @DisplayName("should throw RuntimeException for invalid JSON")
        void shouldThrowRuntimeExceptionForInvalidJson() {
            String invalidJson = "not valid json";
            TypeReference<List<SimpleDto>> typeRef = new TypeReference<>() {};
            assertThrows(RuntimeException.class, () -> JsonUtils.fromJson(invalidJson, typeRef));
        }
    }

    @Nested
    @DisplayName("fromJson(String, JavaType) Tests")
    class FromJsonJavaTypeTests {

        @Test
        @DisplayName("should deserialize JSON using JavaType")
        void shouldDeserializeUsingJavaType() {
            String json = "{\"name\":\"Karen\",\"age\":38}";
            JavaType javaType = JsonUtils.jsonMapper().constructType(SimpleDto.class);
            SimpleDto dto = JsonUtils.fromJson(json, javaType);
            assertEquals("Karen", dto.name);
            assertEquals(38, dto.age);
        }

        @Test
        @DisplayName("should throw RuntimeException for invalid JSON with JavaType")
        void shouldThrowRuntimeExceptionForInvalidJson() {
            String invalidJson = "broken";
            JavaType javaType = JsonUtils.jsonMapper().constructType(SimpleDto.class);
            assertThrows(RuntimeException.class, () -> JsonUtils.fromJson(invalidJson, javaType));
        }
    }

    @Nested
    @DisplayName("toList(String, Class<T>) Tests")
    class ToListTests {

        @Test
        @DisplayName("should deserialize JSON array to List")
        void shouldDeserializeJsonArrayToList() {
            String json = "[{\"name\":\"Leo\",\"age\":28},{\"name\":\"Mia\",\"age\":33}]";
            List<SimpleDto> list = JsonUtils.toList(json, SimpleDto.class);
            assertEquals(2, list.size());
            assertEquals("Leo", list.get(0).name);
            assertEquals(28, list.get(0).age);
            assertEquals("Mia", list.get(1).name);
            assertEquals(33, list.get(1).age);
        }

        @Test
        @DisplayName("should return empty list for empty JSON array")
        void shouldReturnEmptyListForEmptyJsonArray() {
            String json = "[]";
            List<SimpleDto> list = JsonUtils.toList(json, SimpleDto.class);
            assertTrue(list.isEmpty());
        }

        @Test
        @DisplayName("should throw RuntimeException for invalid JSON")
        void shouldThrowRuntimeExceptionForInvalidJson() {
            String invalidJson = "not-json";
            assertThrows(RuntimeException.class, () -> JsonUtils.toList(invalidJson, SimpleDto.class));
        }

        @Test
        @DisplayName("should throw RuntimeException for JSON array with invalid elements")
        void shouldThrowRuntimeExceptionForInvalidElements() {
            String invalidJson = "[{\"name\":\"invalid\",\"age\":\"not-a-number\"}]";
            assertThrows(RuntimeException.class, () -> JsonUtils.toList(invalidJson, SimpleDto.class));
        }
    }

    @Nested
    @DisplayName("Null and Empty JSON Handling Tests")
    class NullAndEmptyJsonTests {

        @Test
        @DisplayName("should handle null input for toJson")
        void shouldHandleNullInputForToJson() {
            String json = JsonUtils.toJson(null);
            // null serialized as JSON is "null"
            assertEquals("null", json);
        }

        @Test
        @DisplayName("should throw RuntimeException for null JSON string in fromJson with Class")
        void shouldThrowRuntimeExceptionForNullJsonWithClass() {
            assertThrows(RuntimeException.class, () -> JsonUtils.fromJson(null, SimpleDto.class));
        }

        @Test
        @DisplayName("should throw RuntimeException for null JSON string in fromJson with TypeReference")
        void shouldThrowRuntimeExceptionForNullJsonWithTypeReference() {
            TypeReference<SimpleDto> typeRef = new TypeReference<>() {};
            assertThrows(RuntimeException.class, () -> JsonUtils.fromJson(null, typeRef));
        }

        @Test
        @DisplayName("should throw RuntimeException for null JSON string in fromJson with JavaType")
        void shouldThrowRuntimeExceptionForNullJsonWithJavaType() {
            JavaType javaType = JsonUtils.jsonMapper().constructType(SimpleDto.class);
            assertThrows(RuntimeException.class, () -> JsonUtils.fromJson(null, javaType));
        }

        @Test
        @DisplayName("should throw RuntimeException for null JSON string in toList")
        void shouldThrowRuntimeExceptionForNullJsonInToList() {
            assertThrows(RuntimeException.class, () -> JsonUtils.toList(null, SimpleDto.class));
        }

        @Test
        @DisplayName("should handle empty string JSON")
        void shouldHandleEmptyStringJson() {
            assertThrows(RuntimeException.class, () -> JsonUtils.fromJson("", SimpleDto.class));
        }

        @Test
        @DisplayName("should handle literal null JSON string")
        void shouldHandleLiteralNullJson() {
            SimpleDto dto = JsonUtils.fromJson("null", SimpleDto.class);
            assertNull(dto);
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("should handle special characters in strings")
        void shouldHandleSpecialCharactersInStrings() {
            SimpleDto dto = new SimpleDto("John \"Jack\"", 30);
            String json = JsonUtils.toJson(dto);
            assertTrue(json.contains("John"));
            SimpleDto deserialized = JsonUtils.fromJson(json, SimpleDto.class);
            assertEquals("John \"Jack\"", deserialized.name);
        }

        @Test
        @DisplayName("should handle unicode characters")
        void shouldHandleUnicodeCharacters() {
            SimpleDto dto = new SimpleDto("中文测试", 25);
            String json = JsonUtils.toJson(dto);
            assertTrue(json.contains("中文测试"));
            SimpleDto deserialized = JsonUtils.fromJson(json, SimpleDto.class);
            assertEquals("中文测试", deserialized.name);
        }

        @Test
        @DisplayName("should handle large numbers without precision loss for Long")
        void shouldHandleLargeNumbers() {
            long largeNumber = 9007199254740993L;
            WithLongValue dto = new WithLongValue("large", largeNumber);
            String json = JsonUtils.toJson(dto);
            assertTrue(json.contains("9007199254740993"));
        }

        @Test
        @DisplayName("should handle negative numbers")
        void shouldHandleNegativeNumbers() {
            SimpleDto dto = new SimpleDto("Neg", -100);
            String json = JsonUtils.toJson(dto);
            SimpleDto deserialized = JsonUtils.fromJson(json, SimpleDto.class);
            assertEquals(-100, deserialized.age);
        }

        @Test
        @DisplayName("should handle zero values")
        void shouldHandleZeroValues() {
            SimpleDto dto = new SimpleDto("Zero", 0);
            String json = JsonUtils.toJson(dto);
            SimpleDto deserialized = JsonUtils.fromJson(json, SimpleDto.class);
            assertEquals(0, deserialized.age);
        }

        @Test
        @DisplayName("should handle boolean values")
        void shouldHandleBooleanValues() {
            BooleanDto dto = new BooleanDto();
            dto.active = true;
            dto.boxedActive = false;
            String json = JsonUtils.toJson(dto);
            assertTrue(json.contains("true"));
            assertTrue(json.contains("false"));
        }

        @Test
        @DisplayName("should roundtrip complex nested object")
        void shouldRoundtripComplexNestedObject() {
            NestedOuter outer = new NestedOuter();
            outer.outerName = "outer";
            outer.inner = new SimpleDto("inner", 10);

            String json = JsonUtils.toJson(outer);
            NestedOuter deserialized = JsonUtils.fromJson(json, NestedOuter.class);

            assertEquals("outer", deserialized.outerName);
            assertEquals("inner", deserialized.inner.name);
            assertEquals(10, deserialized.inner.age);
        }
    }

    @Nested
    @DisplayName("JacksonException Handling Tests")
    class ExceptionHandlingTests {

        @Test
        @DisplayName("should throw RuntimeException when toJson encounters cyclic reference")
        void shouldThrowRuntimeExceptionForCyclicReference() {
            CyclicReference obj = new CyclicReference("loop");
            obj.self = obj; // self-referential

            RuntimeException exception = assertThrows(RuntimeException.class,
                () -> JsonUtils.toJson(obj));
            assertNotNull(exception.getCause());
        }

        @Test
        @DisplayName("should catch exception in fromJson with TypeReference and wrap in RuntimeException")
        void shouldCatchAndWrapExceptionInFromJsonWithTypeReference() {
            // Invalid JSON that will cause parse error
            String invalidJson = "\u0000\u0001\u0002"; // control characters

            RuntimeException exception = assertThrows(RuntimeException.class,
                () -> JsonUtils.fromJson(invalidJson, new TypeReference<List<SimpleDto>>() {}));
            assertNotNull(exception.getCause());
        }

        @Test
        @DisplayName("should catch exception in fromJson with JavaType and wrap in RuntimeException")
        void shouldCatchAndWrapExceptionInFromJsonWithJavaType() {
            // Invalid JSON that will cause parse error
            String invalidJson = "{{[";
            JavaType javaType = JsonUtils.jsonMapper().constructType(SimpleDto.class);

            RuntimeException exception = assertThrows(RuntimeException.class,
                () -> JsonUtils.fromJson(invalidJson, javaType));
            assertNotNull(exception.getCause());
        }

        @Test
        @DisplayName("should catch exception in toList and wrap in RuntimeException")
        void shouldCatchAndWrapExceptionInToList() {
            // Invalid JSON array with malformed elements
            String invalidJson = "[{invalid}]";

            RuntimeException exception = assertThrows(RuntimeException.class,
                () -> JsonUtils.toList(invalidJson, SimpleDto.class));
            assertNotNull(exception.getCause());
        }

        @Test
        @DisplayName("should handle JSON with trailing commas in toList")
        void shouldHandleTrailingCommaInToList() {
            // JSON arrays don't allow trailing commas, this should fail
            String jsonWithTrailingComma = "[{\"name\":\"test\",\"age\":1},]";

            assertThrows(RuntimeException.class,
                () -> JsonUtils.toList(jsonWithTrailingComma, SimpleDto.class));
        }

        @Test
        @DisplayName("should handle malformed JSON string in fromJson Class")
        void shouldHandleMalformedJsonInFromJsonClass() {
            String malformedJson = "{\"name\":}";

            RuntimeException exception = assertThrows(RuntimeException.class,
                () -> JsonUtils.fromJson(malformedJson, SimpleDto.class));
            assertNotNull(exception.getCause());
        }
    }
}
