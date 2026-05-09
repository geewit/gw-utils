package io.geewit.utils.data.spring;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.io.Serializable;
import java.util.*;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link CollectionPageableLoads}.
 */
@DisplayName("CollectionPageableLoads Tests")
class CollectionPageableLoadsTest {

    // --- PageLoadFunction tests ---

    @Nested
    @DisplayName("pageLoadToList tests")
    class PageLoadToListTests {

        @Test
        @DisplayName("should load multiple pages into list")
        void shouldLoadMultiplePagesIntoList() {
            // Given
            int pageSize = 2;
            List<String> page1Content = Arrays.asList("a", "b");
            List<String> page2Content = Arrays.asList("c", "d");
            List<String> page3Content = Collections.singletonList("e");

            Page<String> page1 = new PageImpl<>(page1Content, PageRequest.of(0, pageSize), 5);
            Page<String> page2 = new PageImpl<>(page2Content, PageRequest.of(1, pageSize), 5);
            Page<String> page3 = new PageImpl<>(page3Content, PageRequest.of(2, pageSize), 5);

            CollectionPageableLoads.PageLoadFunction<String> loadFunction = mock(CollectionPageableLoads.PageLoadFunction.class);
            when(loadFunction.page(any(Pageable.class)))
                    .thenAnswer(invocation -> {
                        Pageable p = invocation.getArgument(0);
                        return switch ((int) p.getPageNumber()) {
                            case 0 -> page1;
                            case 1 -> page2;
                            case 2 -> page3;
                            default -> null;
                        };
                    });

            // When
            List<String> result = CollectionPageableLoads.pageLoadToList(pageSize, loadFunction);

            // Then
            assertThat(result).containsExactly("a", "b", "c", "d", "e");
            verify(loadFunction, times(3)).page(any(Pageable.class));
        }

        @Test
        @DisplayName("should handle single page")
        void shouldHandleSinglePage() {
            // Given
            int pageSize = 5;
            List<String> content = Arrays.asList("a", "b", "c");
            Page<String> page = new PageImpl<>(content, PageRequest.of(0, pageSize), 3);

            CollectionPageableLoads.PageLoadFunction<String> loadFunction = mock(CollectionPageableLoads.PageLoadFunction.class);
            when(loadFunction.page(any(Pageable.class))).thenReturn(page);

            // When
            List<String> result = CollectionPageableLoads.pageLoadToList(pageSize, loadFunction);

            // Then
            assertThat(result).containsExactly("a", "b", "c");
            verify(loadFunction, times(1)).page(any(Pageable.class));
        }

        @Test
        @DisplayName("should handle empty results")
        void shouldHandleEmptyResults() {
            // Given
            int pageSize = 5;
            Page<String> page = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, pageSize), 0);

            CollectionPageableLoads.PageLoadFunction<String> loadFunction = mock(CollectionPageableLoads.PageLoadFunction.class);
            when(loadFunction.page(any(Pageable.class))).thenReturn(page);

            // When
            List<String> result = CollectionPageableLoads.pageLoadToList(pageSize, loadFunction);

            // Then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("should handle null page returned")
        void shouldHandleNullPage() {
            // Given
            int pageSize = 5;
            CollectionPageableLoads.PageLoadFunction<String> loadFunction = mock(CollectionPageableLoads.PageLoadFunction.class);
            when(loadFunction.page(any(Pageable.class))).thenReturn(null);

            // When
            List<String> result = CollectionPageableLoads.pageLoadToList(pageSize, loadFunction);

            // Then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("pageLoadAndConsumer tests")
    class PageLoadAndConsumerTests {

        @Test
        @DisplayName("should consume all elements across multiple pages")
        void shouldConsumeAllElementsAcrossMultiplePages() {
            // Given
            int pageSize = 2;
            List<String> page1Content = Arrays.asList("a", "b");
            List<String> page2Content = Arrays.asList("c", "d");

            Page<String> page1 = new PageImpl<>(page1Content, PageRequest.of(0, pageSize), 4);
            Page<String> page2 = new PageImpl<>(page2Content, PageRequest.of(1, pageSize), 4);

            CollectionPageableLoads.PageLoadFunction<String> loadFunction = mock(CollectionPageableLoads.PageLoadFunction.class);
            when(loadFunction.page(any(Pageable.class)))
                    .thenAnswer(invocation -> {
                        Pageable p = invocation.getArgument(0);
                        return switch ((int) p.getPageNumber()) {
                            case 0 -> page1;
                            case 1 -> page2;
                            default -> null;
                        };
                    });

            List<String> consumed = Collections.synchronizedList(new ArrayList<>());
            Consumer<String> consumer = consumed::add;

            // When
            CollectionPageableLoads.pageLoadAndConsumer(pageSize, loadFunction, consumer);

            // Then
            assertThat(consumed).containsExactly("a", "b", "c", "d");
        }

        @Test
        @DisplayName("should handle single page consumption")
        void shouldHandleSinglePageConsumption() {
            // Given
            int pageSize = 5;
            List<String> content = Arrays.asList("a", "b", "c");
            Page<String> page = new PageImpl<>(content, PageRequest.of(0, pageSize), 3);

            CollectionPageableLoads.PageLoadFunction<String> loadFunction = mock(CollectionPageableLoads.PageLoadFunction.class);
            when(loadFunction.page(any(Pageable.class))).thenReturn(page);

            List<String> consumed = new ArrayList<>();
            Consumer<String> consumer = consumed::add;

            // When
            CollectionPageableLoads.pageLoadAndConsumer(pageSize, loadFunction, consumer);

            // Then
            assertThat(consumed).containsExactly("a", "b", "c");
        }

        @Test
        @DisplayName("should handle empty page")
        void shouldHandleEmptyPage() {
            // Given
            int pageSize = 5;
            Page<String> page = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, pageSize), 0);

            CollectionPageableLoads.PageLoadFunction<String> loadFunction = mock(CollectionPageableLoads.PageLoadFunction.class);
            when(loadFunction.page(any(Pageable.class))).thenReturn(page);

            List<String> consumed = new ArrayList<>();
            Consumer<String> consumer = consumed::add;

            // When
            CollectionPageableLoads.pageLoadAndConsumer(pageSize, loadFunction, consumer);

            // Then
            assertThat(consumed).isEmpty();
        }

        @Test
        @DisplayName("should handle null page")
        void shouldHandleNullPage() {
            // Given
            int pageSize = 5;
            CollectionPageableLoads.PageLoadFunction<String> loadFunction = mock(CollectionPageableLoads.PageLoadFunction.class);
            when(loadFunction.page(any(Pageable.class))).thenReturn(null);

            List<String> consumed = new ArrayList<>();
            Consumer<String> consumer = consumed::add;

            // When
            CollectionPageableLoads.pageLoadAndConsumer(pageSize, loadFunction, consumer);

            // Then
            assertThat(consumed).isEmpty();
        }
    }

    @Nested
    @DisplayName("pageLoadAndListConsumer tests")
    class PageLoadAndListConsumerTests {

        @Test
        @DisplayName("should consume all pages")
        void shouldConsumeAllPages() {
            // Given
            int pageSize = 2;
            List<String> page1Content = Arrays.asList("a", "b");
            List<String> page2Content = Arrays.asList("c", "d");

            Page<String> page1 = new PageImpl<>(page1Content, PageRequest.of(0, pageSize), 4);
            Page<String> page2 = new PageImpl<>(page2Content, PageRequest.of(1, pageSize), 4);

            CollectionPageableLoads.PageLoadFunction<String> loadFunction = mock(CollectionPageableLoads.PageLoadFunction.class);
            when(loadFunction.page(any(Pageable.class)))
                    .thenAnswer(invocation -> {
                        Pageable p = invocation.getArgument(0);
                        return switch ((int) p.getPageNumber()) {
                            case 0 -> page1;
                            case 1 -> page2;
                            default -> null;
                        };
                    });

            List<List<String>> consumedPages = new ArrayList<>();
            Consumer<List<String>> consumer = consumedPages::add;

            // When
            CollectionPageableLoads.pageLoadAndListConsumer(pageSize, loadFunction, consumer);

            // Then
            assertThat(consumedPages).hasSize(2);
            assertThat(consumedPages.get(0)).containsExactly("a", "b");
            assertThat(consumedPages.get(1)).containsExactly("c", "d");
        }

        @Test
        @DisplayName("should handle single page")
        void shouldHandleSinglePage() {
            // Given
            int pageSize = 5;
            List<String> content = Arrays.asList("a", "b", "c");
            Page<String> page = new PageImpl<>(content, PageRequest.of(0, pageSize), 3);

            CollectionPageableLoads.PageLoadFunction<String> loadFunction = mock(CollectionPageableLoads.PageLoadFunction.class);
            when(loadFunction.page(any(Pageable.class))).thenReturn(page);

            List<List<String>> consumedPages = new ArrayList<>();
            Consumer<List<String>> consumer = consumedPages::add;

            // When
            CollectionPageableLoads.pageLoadAndListConsumer(pageSize, loadFunction, consumer);

            // Then
            assertThat(consumedPages).hasSize(1);
            assertThat(consumedPages.get(0)).containsExactly("a", "b", "c");
        }

        @Test
        @DisplayName("should handle empty page")
        void shouldHandleEmptyPage() {
            // Given
            int pageSize = 5;
            Page<String> page = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, pageSize), 0);

            CollectionPageableLoads.PageLoadFunction<String> loadFunction = mock(CollectionPageableLoads.PageLoadFunction.class);
            when(loadFunction.page(any(Pageable.class))).thenReturn(page);

            List<List<String>> consumedPages = new ArrayList<>();
            Consumer<List<String>> consumer = consumedPages::add;

            // When
            CollectionPageableLoads.pageLoadAndListConsumer(pageSize, loadFunction, consumer);

            // Then
            assertThat(consumedPages).isEmpty();
        }

        @Test
        @DisplayName("should handle null page")
        void shouldHandleNullPage() {
            // Given
            int pageSize = 5;
            CollectionPageableLoads.PageLoadFunction<String> loadFunction = mock(CollectionPageableLoads.PageLoadFunction.class);
            when(loadFunction.page(any(Pageable.class))).thenReturn(null);

            List<List<String>> consumedPages = new ArrayList<>();
            Consumer<List<String>> consumer = consumedPages::add;

            // When
            CollectionPageableLoads.pageLoadAndListConsumer(pageSize, loadFunction, consumer);

            // Then
            assertThat(consumedPages).isEmpty();
        }
    }

    // --- LoadFunction tests ---

    @Nested
    @DisplayName("pageableLoadToMap tests")
    class PageableLoadToMapTests {

        @Test
        @DisplayName("should load multiple pages into map")
        void shouldLoadMultiplePagesIntoMap() {
            // Given
            Collection<Integer> ids = Arrays.asList(1, 2, 3, 4);
            int pageSize = 2;

            @SuppressWarnings("unchecked")
            CollectionPageableLoads.LoadFunction<String, Integer> loadFunction = mock(CollectionPageableLoads.LoadFunction.class);
            when(loadFunction.list(any()))
                    .thenAnswer(invocation -> {
                        Collection<Integer> idsArg = invocation.getArgument(0);
                        if (idsArg.contains(1) && idsArg.contains(2)) {
                            return Arrays.asList("entity1", "entity2");
                        } else {
                            return Arrays.asList("entity3", "entity4");
                        }
                    });

            CollectionPageableLoads.EntityToIdFunction<String, Integer> entityToIdFunction = mock(CollectionPageableLoads.EntityToIdFunction.class);
            when(entityToIdFunction.entityId("entity1")).thenReturn(1);
            when(entityToIdFunction.entityId("entity2")).thenReturn(2);
            when(entityToIdFunction.entityId("entity3")).thenReturn(3);
            when(entityToIdFunction.entityId("entity4")).thenReturn(4);

            // When
            Map<Integer, String> result = CollectionPageableLoads.pageableLoadToMap(ids, pageSize, loadFunction, entityToIdFunction);

            // Then
            assertThat(result).hasSize(4);
            assertThat(result.get(1)).isEqualTo("entity1");
            assertThat(result.get(2)).isEqualTo("entity2");
            assertThat(result.get(3)).isEqualTo("entity3");
            assertThat(result.get(4)).isEqualTo("entity4");
        }

        @Test
        @DisplayName("should handle empty ids collection")
        void shouldHandleEmptyIdsCollection() {
            // Given
            Collection<Integer> ids = Collections.emptyList();
            int pageSize = 5;

            @SuppressWarnings("unchecked")
            CollectionPageableLoads.LoadFunction<String, Integer> loadFunction = mock(CollectionPageableLoads.LoadFunction.class);
            CollectionPageableLoads.EntityToIdFunction<String, Integer> entityToIdFunction = mock(CollectionPageableLoads.EntityToIdFunction.class);

            // When
            Map<Integer, String> result = CollectionPageableLoads.pageableLoadToMap(ids, pageSize, loadFunction, entityToIdFunction);

            // Then
            assertThat(result).isEmpty();
            verify(loadFunction, never()).list(any());
        }

        @Test
        @DisplayName("should handle null ids collection")
        void shouldHandleNullIdsCollection() {
            // Given
            Collection<Integer> ids = null;
            int pageSize = 5;

            @SuppressWarnings("unchecked")
            CollectionPageableLoads.LoadFunction<String, Integer> loadFunction = mock(CollectionPageableLoads.LoadFunction.class);
            CollectionPageableLoads.EntityToIdFunction<String, Integer> entityToIdFunction = mock(CollectionPageableLoads.EntityToIdFunction.class);

            // When
            Map<Integer, String> result = CollectionPageableLoads.pageableLoadToMap(ids, pageSize, loadFunction, entityToIdFunction);

            // Then
            assertThat(result).isEmpty();
            verify(loadFunction, never()).list(any());
        }

        @Test
        @DisplayName("should handle single page")
        void shouldHandleSinglePage() {
            // Given
            Collection<Integer> ids = Arrays.asList(1, 2);
            int pageSize = 5;

            @SuppressWarnings("unchecked")
            CollectionPageableLoads.LoadFunction<String, Integer> loadFunction = mock(CollectionPageableLoads.LoadFunction.class);
            when(loadFunction.list(any()))
                    .thenReturn(Arrays.asList("entity1", "entity2"));

            CollectionPageableLoads.EntityToIdFunction<String, Integer> entityToIdFunction = mock(CollectionPageableLoads.EntityToIdFunction.class);
            when(entityToIdFunction.entityId("entity1")).thenReturn(1);
            when(entityToIdFunction.entityId("entity2")).thenReturn(2);

            // When
            Map<Integer, String> result = CollectionPageableLoads.pageableLoadToMap(ids, pageSize, loadFunction, entityToIdFunction);

            // Then
            assertThat(result).hasSize(2);
            assertThat(result.get(1)).isEqualTo("entity1");
            assertThat(result.get(2)).isEqualTo("entity2");
        }

        @Test
        @DisplayName("should handle null returned from load function")
        void shouldHandleNullReturnedFromLoadFunction() {
            // Given
            Collection<Integer> ids = Arrays.asList(1, 2);
            int pageSize = 5;

            @SuppressWarnings("unchecked")
            CollectionPageableLoads.LoadFunction<String, Integer> loadFunction = mock(CollectionPageableLoads.LoadFunction.class);
            when(loadFunction.list(any())).thenReturn(null);

            CollectionPageableLoads.EntityToIdFunction<String, Integer> entityToIdFunction = mock(CollectionPageableLoads.EntityToIdFunction.class);

            // When
            Map<Integer, String> result = CollectionPageableLoads.pageableLoadToMap(ids, pageSize, loadFunction, entityToIdFunction);

            // Then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("should handle empty list returned from load function")
        void shouldHandleEmptyListReturnedFromLoadFunction() {
            // Given
            Collection<Integer> ids = Arrays.asList(1, 2);
            int pageSize = 5;

            @SuppressWarnings("unchecked")
            CollectionPageableLoads.LoadFunction<String, Integer> loadFunction = mock(CollectionPageableLoads.LoadFunction.class);
            when(loadFunction.list(any())).thenReturn(Collections.emptyList());

            CollectionPageableLoads.EntityToIdFunction<String, Integer> entityToIdFunction = mock(CollectionPageableLoads.EntityToIdFunction.class);

            // When
            Map<Integer, String> result = CollectionPageableLoads.pageableLoadToMap(ids, pageSize, loadFunction, entityToIdFunction);

            // Then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("pageableLoadToSetMap tests")
    class PageableLoadToSetMapTests {

        @Test
        @DisplayName("should load multiple pages into set map")
        void shouldLoadMultiplePagesIntoSetMap() {
            // Given
            Collection<Integer> ids = Arrays.asList(1, 2, 3, 4);
            int pageSize = 2;

            @SuppressWarnings("unchecked")
            CollectionPageableLoads.LoadFunction<String, Integer> loadFunction = mock(CollectionPageableLoads.LoadFunction.class);
            when(loadFunction.list(any()))
                    .thenAnswer(invocation -> {
                        Collection<Integer> idsArg = invocation.getArgument(0);
                        if (idsArg.contains(1) && idsArg.contains(2)) {
                            return Arrays.asList("value1-A", "value1-B", "value2-A");
                        } else {
                            return Arrays.asList("value3-A", "value4-A", "value4-B");
                        }
                    });

            @SuppressWarnings("unchecked")
            CollectionPageableLoads.EntityToKeyFunction<String, Integer> entityToKeyFunction = mock(CollectionPageableLoads.EntityToKeyFunction.class);
            when(entityToKeyFunction.mapKey("value1-A")).thenReturn(1);
            when(entityToKeyFunction.mapKey("value1-B")).thenReturn(1);
            when(entityToKeyFunction.mapKey("value2-A")).thenReturn(2);
            when(entityToKeyFunction.mapKey("value3-A")).thenReturn(3);
            when(entityToKeyFunction.mapKey("value4-A")).thenReturn(4);
            when(entityToKeyFunction.mapKey("value4-B")).thenReturn(4);

            @SuppressWarnings("unchecked")
            CollectionPageableLoads.EntityToValueFunction<String, String> entityToValueFunction = mock(CollectionPageableLoads.EntityToValueFunction.class);
            when(entityToValueFunction.mapValue("value1-A")).thenReturn("value1-A");
            when(entityToValueFunction.mapValue("value1-B")).thenReturn("value1-B");
            when(entityToValueFunction.mapValue("value2-A")).thenReturn("value2-A");
            when(entityToValueFunction.mapValue("value3-A")).thenReturn("value3-A");
            when(entityToValueFunction.mapValue("value4-A")).thenReturn("value4-A");
            when(entityToValueFunction.mapValue("value4-B")).thenReturn("value4-B");

            // When
            Map<Integer, Set<String>> result = CollectionPageableLoads.pageableLoadToSetMap(ids, pageSize, loadFunction, entityToKeyFunction, entityToValueFunction);

            // Then
            assertThat(result).hasSize(4);
            assertThat(result.get(1)).containsExactlyInAnyOrder("value1-A", "value1-B");
            assertThat(result.get(2)).containsExactlyInAnyOrder("value2-A");
            assertThat(result.get(3)).containsExactlyInAnyOrder("value3-A");
            assertThat(result.get(4)).containsExactlyInAnyOrder("value4-A", "value4-B");
        }

        @Test
        @DisplayName("should handle empty ids collection")
        void shouldHandleEmptyIdsCollection() {
            // Given
            Collection<Integer> ids = Collections.emptyList();
            int pageSize = 5;

            @SuppressWarnings("unchecked")
            CollectionPageableLoads.LoadFunction<String, Integer> loadFunction = mock(CollectionPageableLoads.LoadFunction.class);
            @SuppressWarnings("unchecked")
            CollectionPageableLoads.EntityToKeyFunction<String, Integer> entityToKeyFunction = mock(CollectionPageableLoads.EntityToKeyFunction.class);
            @SuppressWarnings("unchecked")
            CollectionPageableLoads.EntityToValueFunction<String, String> entityToValueFunction = mock(CollectionPageableLoads.EntityToValueFunction.class);

            // When
            Map<Integer, Set<String>> result = CollectionPageableLoads.pageableLoadToSetMap(ids, pageSize, loadFunction, entityToKeyFunction, entityToValueFunction);

            // Then
            assertThat(result).isEmpty();
            verify(loadFunction, never()).list(any());
        }

        @Test
        @DisplayName("should handle null ids collection")
        void shouldHandleNullIdsCollection() {
            // Given
            Collection<Integer> ids = null;
            int pageSize = 5;

            @SuppressWarnings("unchecked")
            CollectionPageableLoads.LoadFunction<String, Integer> loadFunction = mock(CollectionPageableLoads.LoadFunction.class);
            @SuppressWarnings("unchecked")
            CollectionPageableLoads.EntityToKeyFunction<String, Integer> entityToKeyFunction = mock(CollectionPageableLoads.EntityToKeyFunction.class);
            @SuppressWarnings("unchecked")
            CollectionPageableLoads.EntityToValueFunction<String, String> entityToValueFunction = mock(CollectionPageableLoads.EntityToValueFunction.class);

            // When
            Map<Integer, Set<String>> result = CollectionPageableLoads.pageableLoadToSetMap(ids, pageSize, loadFunction, entityToKeyFunction, entityToValueFunction);

            // Then
            assertThat(result).isEmpty();
            verify(loadFunction, never()).list(any());
        }

        @Test
        @DisplayName("should handle null returned from load function")
        void shouldHandleNullReturnedFromLoadFunction() {
            // Given
            Collection<Integer> ids = Arrays.asList(1, 2);
            int pageSize = 5;

            @SuppressWarnings("unchecked")
            CollectionPageableLoads.LoadFunction<String, Integer> loadFunction = mock(CollectionPageableLoads.LoadFunction.class);
            when(loadFunction.list(any())).thenReturn(null);

            @SuppressWarnings("unchecked")
            CollectionPageableLoads.EntityToKeyFunction<String, Integer> entityToKeyFunction = mock(CollectionPageableLoads.EntityToKeyFunction.class);
            @SuppressWarnings("unchecked")
            CollectionPageableLoads.EntityToValueFunction<String, String> entityToValueFunction = mock(CollectionPageableLoads.EntityToValueFunction.class);

            // When
            Map<Integer, Set<String>> result = CollectionPageableLoads.pageableLoadToSetMap(ids, pageSize, loadFunction, entityToKeyFunction, entityToValueFunction);

            // Then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("should handle empty list returned from load function")
        void shouldHandleEmptyListReturnedFromLoadFunction() {
            // Given
            Collection<Integer> ids = Arrays.asList(1, 2);
            int pageSize = 5;

            @SuppressWarnings("unchecked")
            CollectionPageableLoads.LoadFunction<String, Integer> loadFunction = mock(CollectionPageableLoads.LoadFunction.class);
            when(loadFunction.list(any())).thenReturn(Collections.emptyList());

            @SuppressWarnings("unchecked")
            CollectionPageableLoads.EntityToKeyFunction<String, Integer> entityToKeyFunction = mock(CollectionPageableLoads.EntityToKeyFunction.class);
            @SuppressWarnings("unchecked")
            CollectionPageableLoads.EntityToValueFunction<String, String> entityToValueFunction = mock(CollectionPageableLoads.EntityToValueFunction.class);

            // When
            Map<Integer, Set<String>> result = CollectionPageableLoads.pageableLoadToSetMap(ids, pageSize, loadFunction, entityToKeyFunction, entityToValueFunction);

            // Then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("pageableLoadToList tests")
    class PageableLoadToListTests {

        @Test
        @DisplayName("should load multiple pages into list")
        void shouldLoadMultiplePagesIntoList() {
            // Given
            Collection<Integer> ids = Arrays.asList(1, 2, 3, 4);
            int pageSize = 2;

            @SuppressWarnings("unchecked")
            CollectionPageableLoads.LoadFunction<String, Integer> loadFunction = mock(CollectionPageableLoads.LoadFunction.class);
            when(loadFunction.list(any()))
                    .thenAnswer(invocation -> {
                        Collection<Integer> idsArg = invocation.getArgument(0);
                        if (idsArg.contains(1) && idsArg.contains(2)) {
                            return Arrays.asList("entity1", "entity2");
                        } else {
                            return Arrays.asList("entity3", "entity4");
                        }
                    });

            // When
            List<String> result = CollectionPageableLoads.pageableLoadToList(ids, pageSize, loadFunction);

            // Then
            assertThat(result).hasSize(4);
            assertThat(result).containsExactly("entity1", "entity2", "entity3", "entity4");
        }

        @Test
        @DisplayName("should handle empty ids collection")
        void shouldHandleEmptyIdsCollection() {
            // Given
            Collection<Integer> ids = Collections.emptyList();
            int pageSize = 5;

            @SuppressWarnings("unchecked")
            CollectionPageableLoads.LoadFunction<String, Integer> loadFunction = mock(CollectionPageableLoads.LoadFunction.class);

            // When
            List<String> result = CollectionPageableLoads.pageableLoadToList(ids, pageSize, loadFunction);

            // Then
            assertThat(result).isEmpty();
            verify(loadFunction, never()).list(any());
        }

        @Test
        @DisplayName("should handle null ids collection")
        void shouldHandleNullIdsCollection() {
            // Given
            Collection<Integer> ids = null;
            int pageSize = 5;

            @SuppressWarnings("unchecked")
            CollectionPageableLoads.LoadFunction<String, Integer> loadFunction = mock(CollectionPageableLoads.LoadFunction.class);

            // When
            List<String> result = CollectionPageableLoads.pageableLoadToList(ids, pageSize, loadFunction);

            // Then
            assertThat(result).isEmpty();
            verify(loadFunction, never()).list(any());
        }

        @Test
        @DisplayName("should handle single page")
        void shouldHandleSinglePage() {
            // Given
            Collection<Integer> ids = Arrays.asList(1, 2);
            int pageSize = 5;

            @SuppressWarnings("unchecked")
            CollectionPageableLoads.LoadFunction<String, Integer> loadFunction = mock(CollectionPageableLoads.LoadFunction.class);
            when(loadFunction.list(any()))
                    .thenReturn(Arrays.asList("entity1", "entity2"));

            // When
            List<String> result = CollectionPageableLoads.pageableLoadToList(ids, pageSize, loadFunction);

            // Then
            assertThat(result).hasSize(2);
            assertThat(result).containsExactly("entity1", "entity2");
        }

        @Test
        @DisplayName("should handle null returned from load function")
        void shouldHandleNullReturnedFromLoadFunction() {
            // Given
            Collection<Integer> ids = Arrays.asList(1, 2);
            int pageSize = 5;

            @SuppressWarnings("unchecked")
            CollectionPageableLoads.LoadFunction<String, Integer> loadFunction = mock(CollectionPageableLoads.LoadFunction.class);
            when(loadFunction.list(any())).thenReturn(null);

            // When
            List<String> result = CollectionPageableLoads.pageableLoadToList(ids, pageSize, loadFunction);

            // Then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("should handle empty list returned from load function")
        void shouldHandleEmptyListReturnedFromLoadFunction() {
            // Given
            Collection<Integer> ids = Arrays.asList(1, 2);
            int pageSize = 5;

            @SuppressWarnings("unchecked")
            CollectionPageableLoads.LoadFunction<String, Integer> loadFunction = mock(CollectionPageableLoads.LoadFunction.class);
            when(loadFunction.list(any())).thenReturn(Collections.emptyList());

            // When
            List<String> result = CollectionPageableLoads.pageableLoadToList(ids, pageSize, loadFunction);

            // Then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("pageableLoadToSet tests")
    class PageableLoadToSetTests {

        @Test
        @DisplayName("should load multiple pages into set")
        void shouldLoadMultiplePagesIntoSet() {
            // Given
            Collection<Integer> ids = Arrays.asList(1, 2, 3, 4);
            int pageSize = 2;

            @SuppressWarnings("unchecked")
            CollectionPageableLoads.LoadFunction<String, Integer> loadFunction = mock(CollectionPageableLoads.LoadFunction.class);
            when(loadFunction.list(any()))
                    .thenAnswer(invocation -> {
                        Collection<Integer> idsArg = invocation.getArgument(0);
                        if (idsArg.contains(1) && idsArg.contains(2)) {
                            return Arrays.asList("entity1", "entity2");
                        } else {
                            return Arrays.asList("entity3", "entity4");
                        }
                    });

            // When
            Set<String> result = CollectionPageableLoads.pageableLoadToSet(ids, pageSize, loadFunction);

            // Then
            assertThat(result).hasSize(4);
            assertThat(result).containsExactlyInAnyOrder("entity1", "entity2", "entity3", "entity4");
        }

        @Test
        @DisplayName("should handle empty ids collection")
        void shouldHandleEmptyIdsCollection() {
            // Given
            Collection<Integer> ids = Collections.emptyList();
            int pageSize = 5;

            @SuppressWarnings("unchecked")
            CollectionPageableLoads.LoadFunction<String, Integer> loadFunction = mock(CollectionPageableLoads.LoadFunction.class);

            // When
            Set<String> result = CollectionPageableLoads.pageableLoadToSet(ids, pageSize, loadFunction);

            // Then
            assertThat(result).isEmpty();
            verify(loadFunction, never()).list(any());
        }

        @Test
        @DisplayName("should handle null ids collection")
        void shouldHandleNullIdsCollection() {
            // Given
            Collection<Integer> ids = null;
            int pageSize = 5;

            @SuppressWarnings("unchecked")
            CollectionPageableLoads.LoadFunction<String, Integer> loadFunction = mock(CollectionPageableLoads.LoadFunction.class);

            // When
            Set<String> result = CollectionPageableLoads.pageableLoadToSet(ids, pageSize, loadFunction);

            // Then
            assertThat(result).isEmpty();
            verify(loadFunction, never()).list(any());
        }

        @Test
        @DisplayName("should handle single page")
        void shouldHandleSinglePage() {
            // Given
            Collection<Integer> ids = Arrays.asList(1, 2);
            int pageSize = 5;

            @SuppressWarnings("unchecked")
            CollectionPageableLoads.LoadFunction<String, Integer> loadFunction = mock(CollectionPageableLoads.LoadFunction.class);
            when(loadFunction.list(any()))
                    .thenReturn(Arrays.asList("entity1", "entity2"));

            // When
            Set<String> result = CollectionPageableLoads.pageableLoadToSet(ids, pageSize, loadFunction);

            // Then
            assertThat(result).hasSize(2);
            assertThat(result).containsExactlyInAnyOrder("entity1", "entity2");
        }

        @Test
        @DisplayName("should handle null returned from load function")
        void shouldHandleNullReturnedFromLoadFunction() {
            // Given
            Collection<Integer> ids = Arrays.asList(1, 2);
            int pageSize = 5;

            @SuppressWarnings("unchecked")
            CollectionPageableLoads.LoadFunction<String, Integer> loadFunction = mock(CollectionPageableLoads.LoadFunction.class);
            when(loadFunction.list(any())).thenReturn(null);

            // When
            Set<String> result = CollectionPageableLoads.pageableLoadToSet(ids, pageSize, loadFunction);

            // Then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("should handle empty list returned from load function")
        void shouldHandleEmptyListReturnedFromLoadFunction() {
            // Given
            Collection<Integer> ids = Arrays.asList(1, 2);
            int pageSize = 5;

            @SuppressWarnings("unchecked")
            CollectionPageableLoads.LoadFunction<String, Integer> loadFunction = mock(CollectionPageableLoads.LoadFunction.class);
            when(loadFunction.list(any())).thenReturn(Collections.emptyList());

            // When
            Set<String> result = CollectionPageableLoads.pageableLoadToSet(ids, pageSize, loadFunction);

            // Then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("should deduplicate elements in set")
        void shouldDeduplicateElementsInSet() {
            // Given
            Collection<Integer> ids = Arrays.asList(1, 2, 3);
            int pageSize = 5;

            @SuppressWarnings("unchecked")
            CollectionPageableLoads.LoadFunction<String, Integer> loadFunction = mock(CollectionPageableLoads.LoadFunction.class);
            when(loadFunction.list(any()))
                    .thenReturn(Arrays.asList("entity1", "entity1", "entity2"));

            // When
            Set<String> result = CollectionPageableLoads.pageableLoadToSet(ids, pageSize, loadFunction);

            // Then
            assertThat(result).hasSize(2);
            assertThat(result).containsExactlyInAnyOrder("entity1", "entity2");
        }
    }

    @Nested
    @DisplayName("pageableLoadToMap with large page size tests")
    class PageableLoadToMapLargePageSizeTests {

        @Test
        @DisplayName("should handle page size larger than ids size")
        void shouldHandlePageSizeLargerThanIdsSize() {
            // Given
            Collection<Integer> ids = Arrays.asList(1, 2, 3);
            int pageSize = 10;

            @SuppressWarnings("unchecked")
            CollectionPageableLoads.LoadFunction<String, Integer> loadFunction = mock(CollectionPageableLoads.LoadFunction.class);
            when(loadFunction.list(any()))
                    .thenReturn(Arrays.asList("entity1", "entity2", "entity3"));

            CollectionPageableLoads.EntityToIdFunction<String, Integer> entityToIdFunction = mock(CollectionPageableLoads.EntityToIdFunction.class);
            when(entityToIdFunction.entityId("entity1")).thenReturn(1);
            when(entityToIdFunction.entityId("entity2")).thenReturn(2);
            when(entityToIdFunction.entityId("entity3")).thenReturn(3);

            // When
            Map<Integer, String> result = CollectionPageableLoads.pageableLoadToMap(ids, pageSize, loadFunction, entityToIdFunction);

            // Then
            assertThat(result).hasSize(3);
        }
    }

    @Nested
    @DisplayName("pageableLoadToSetMap with large page size tests")
    class PageableLoadToSetMapLargePageSizeTests {

        @Test
        @DisplayName("should handle page size larger than ids size")
        void shouldHandlePageSizeLargerThanIdsSize() {
            // Given
            Collection<Integer> ids = Arrays.asList(1, 2, 3);
            int pageSize = 10;

            @SuppressWarnings("unchecked")
            CollectionPageableLoads.LoadFunction<String, Integer> loadFunction = mock(CollectionPageableLoads.LoadFunction.class);
            when(loadFunction.list(any()))
                    .thenReturn(Arrays.asList("value1-A", "value2-A", "value3-A"));

            @SuppressWarnings("unchecked")
            CollectionPageableLoads.EntityToKeyFunction<String, Integer> entityToKeyFunction = mock(CollectionPageableLoads.EntityToKeyFunction.class);
            when(entityToKeyFunction.mapKey("value1-A")).thenReturn(1);
            when(entityToKeyFunction.mapKey("value2-A")).thenReturn(2);
            when(entityToKeyFunction.mapKey("value3-A")).thenReturn(3);

            @SuppressWarnings("unchecked")
            CollectionPageableLoads.EntityToValueFunction<String, String> entityToValueFunction = mock(CollectionPageableLoads.EntityToValueFunction.class);
            when(entityToValueFunction.mapValue("value1-A")).thenReturn("value1-A");
            when(entityToValueFunction.mapValue("value2-A")).thenReturn("value2-A");
            when(entityToValueFunction.mapValue("value3-A")).thenReturn("value3-A");

            // When
            Map<Integer, Set<String>> result = CollectionPageableLoads.pageableLoadToSetMap(ids, pageSize, loadFunction, entityToKeyFunction, entityToValueFunction);

            // Then
            assertThat(result).hasSize(3);
        }
    }

    @Nested
    @DisplayName("pageableLoadToList with large page size tests")
    class PageableLoadToListLargePageSizeTests {

        @Test
        @DisplayName("should handle page size larger than ids size")
        void shouldHandlePageSizeLargerThanIdsSize() {
            // Given
            Collection<Integer> ids = Arrays.asList(1, 2, 3);
            int pageSize = 10;

            @SuppressWarnings("unchecked")
            CollectionPageableLoads.LoadFunction<String, Integer> loadFunction = mock(CollectionPageableLoads.LoadFunction.class);
            when(loadFunction.list(any()))
                    .thenReturn(Arrays.asList("entity1", "entity2", "entity3"));

            // When
            List<String> result = CollectionPageableLoads.pageableLoadToList(ids, pageSize, loadFunction);

            // Then
            assertThat(result).hasSize(3);
            assertThat(result).containsExactly("entity1", "entity2", "entity3");
        }
    }

    @Nested
    @DisplayName("pageableLoadToSet with large page size tests")
    class PageableLoadToSetLargePageSizeTests {

        @Test
        @DisplayName("should handle page size larger than ids size")
        void shouldHandlePageSizeLargerThanIdsSize() {
            // Given
            Collection<Integer> ids = Arrays.asList(1, 2, 3);
            int pageSize = 10;

            @SuppressWarnings("unchecked")
            CollectionPageableLoads.LoadFunction<String, Integer> loadFunction = mock(CollectionPageableLoads.LoadFunction.class);
            when(loadFunction.list(any()))
                    .thenReturn(Arrays.asList("entity1", "entity2", "entity3"));

            // When
            Set<String> result = CollectionPageableLoads.pageableLoadToSet(ids, pageSize, loadFunction);

            // Then
            assertThat(result).hasSize(3);
            assertThat(result).containsExactlyInAnyOrder("entity1", "entity2", "entity3");
        }
    }
}
