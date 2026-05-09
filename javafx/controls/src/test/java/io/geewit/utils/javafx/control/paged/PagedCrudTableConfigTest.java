package io.geewit.utils.javafx.control.paged;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("PagedCrudTableConfig Tests")
class PagedCrudTableConfigTest {

    private static class TestEntity {
        private final String id;
        private final String name;

        TestEntity(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

    @Nested
    @DisplayName("record accessors")
    class RecordAccessors {

        @Test
        @DisplayName("should return all record components")
        void shouldReturnAllRecordComponents() {
            PagedCrudTableConfig<TestEntity, String, String> config = PagedCrudTableConfig.<TestEntity, String, String>builder()
                    .pageSize(25)
                    .querySupplier(() -> "query")
                    .service(mock(PagedCrudService.class))
                    .keyFn(TestEntity::getId)
                    .build();

            assertThat(config.pageSize()).isEqualTo(25);
            assertThat(config.querySupplier()).isNotNull();
            assertThat(config.service()).isNotNull();
            assertThat(config.keyFn()).isNotNull();
            assertThat(config.mp()).isNotNull();
        }
    }

    @Nested
    @DisplayName("Builder")
    class BuilderTests {

        @Test
        @DisplayName("should use default page size when not specified")
        void shouldUseDefaultPageSizeWhenNotSpecified() {
            PagedCrudTableConfig<TestEntity, String, String> config = PagedCrudTableConfig.<TestEntity, String, String>builder()
                    .querySupplier(() -> "query")
                    .service(mock(PagedCrudService.class))
                    .keyFn(TestEntity::getId)
                    .build();

            assertThat(config.pageSize()).isEqualTo(PagedCrudTableConfig.DEFAULT_PAGE_SIZE);
        }

        @Test
        @DisplayName("should use default page size when negative")
        void shouldUseDefaultPageSizeWhenNegative() {
            PagedCrudTableConfig<TestEntity, String, String> config = PagedCrudTableConfig.<TestEntity, String, String>builder()
                    .pageSize(-5)
                    .querySupplier(() -> "query")
                    .service(mock(PagedCrudService.class))
                    .keyFn(TestEntity::getId)
                    .build();

            assertThat(config.pageSize()).isEqualTo(PagedCrudTableConfig.DEFAULT_PAGE_SIZE);
        }

        @Test
        @DisplayName("should use default page size when zero")
        void shouldUseDefaultPageSizeWhenZero() {
            PagedCrudTableConfig<TestEntity, String, String> config = PagedCrudTableConfig.<TestEntity, String, String>builder()
                    .pageSize(0)
                    .querySupplier(() -> "query")
                    .service(mock(PagedCrudService.class))
                    .keyFn(TestEntity::getId)
                    .build();

            assertThat(config.pageSize()).isEqualTo(PagedCrudTableConfig.DEFAULT_PAGE_SIZE);
        }

        @Test
        @DisplayName("should throw NullPointerException when querySupplier is null")
        void shouldThrowWhenQuerySupplierIsNull() {
            assertThatThrownBy(() -> PagedCrudTableConfig.<TestEntity, String, String>builder()
                    .service(mock(PagedCrudService.class))
                    .keyFn(TestEntity::getId)
                    .build())
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("querySupplier");
        }

        @Test
        @DisplayName("should throw NullPointerException when service is null")
        void shouldThrowWhenServiceIsNull() {
            assertThatThrownBy(() -> PagedCrudTableConfig.<TestEntity, String, String>builder()
                    .querySupplier(() -> "query")
                    .keyFn(TestEntity::getId)
                    .build())
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("service");
        }

        @Test
        @DisplayName("should throw NullPointerException when keyFn is null")
        void shouldThrowWhenKeyFnIsNull() {
            assertThatThrownBy(() -> PagedCrudTableConfig.<TestEntity, String, String>builder()
                    .querySupplier(() -> "query")
                    .service(mock(PagedCrudService.class))
                    .build())
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("keyFn");
        }

        @Test
        @DisplayName("should use noopCopier when copier is null")
        void shouldUseNoopCopierWhenCopierIsNull() {
            PagedCrudTableConfig<TestEntity, String, String> config = PagedCrudTableConfig.<TestEntity, String, String>builder()
                    .querySupplier(() -> "query")
                    .service(mock(PagedCrudService.class))
                    .keyFn(TestEntity::getId)
                    .build();

            // Should not throw when applying copier
            TestEntity src = new TestEntity("1", "source");
            TestEntity target = new TestEntity("1", "target");
            config.copier().accept(src, target);
        }

        @Test
        @DisplayName("should default refreshAfterCopy to true")
        void shouldDefaultRefreshAfterCopyToTrue() {
            PagedCrudTableConfig<TestEntity, String, String> config = PagedCrudTableConfig.<TestEntity, String, String>builder()
                    .querySupplier(() -> "query")
                    .service(mock(PagedCrudService.class))
                    .keyFn(TestEntity::getId)
                    .build();

            assertThat(config.refreshAfterCopy()).isTrue();
        }

        @Test
        @DisplayName("should use default create editor when null")
        void shouldUseDefaultCreateEditorWhenNull() throws Exception {
            PagedCrudTableConfig<TestEntity, String, String> config = PagedCrudTableConfig.<TestEntity, String, String>builder()
                    .querySupplier(() -> "query")
                    .service(mock(PagedCrudService.class))
                    .keyFn(TestEntity::getId)
                    .build();

            CompletionStage<Optional<TestEntity>> result = config.openCreateEditor().get();
            assertThat(result.toCompletableFuture().get()).isEmpty();
        }

        @Test
        @DisplayName("should use default edit editor when null")
        void shouldUseDefaultEditEditorWhenNull() throws Exception {
            PagedCrudTableConfig<TestEntity, String, String> config = PagedCrudTableConfig.<TestEntity, String, String>builder()
                    .querySupplier(() -> "query")
                    .service(mock(PagedCrudService.class))
                    .keyFn(TestEntity::getId)
                    .build();

            CompletionStage<Optional<TestEntity>> result = config.openEditEditor().apply(new TestEntity("1", "test"));
            assertThat(result.toCompletableFuture().get()).isEmpty();
        }

        @Test
        @DisplayName("should use default confirm delete when null")
        void shouldUseDefaultConfirmDeleteWhenNull() throws Exception {
            PagedCrudTableConfig<TestEntity, String, String> config = PagedCrudTableConfig.<TestEntity, String, String>builder()
                    .querySupplier(() -> "query")
                    .service(mock(PagedCrudService.class))
                    .keyFn(TestEntity::getId)
                    .build();

            CompletionStage<Boolean> result = config.confirmDelete().apply(List.of());
            assertThat(result.toCompletableFuture().get()).isTrue();
        }

        @Test
        @DisplayName("should use identity message provider when null")
        void shouldUseIdentityMessageProviderWhenNull() {
            PagedCrudTableConfig<TestEntity, String, String> config = PagedCrudTableConfig.<TestEntity, String, String>builder()
                    .querySupplier(() -> "query")
                    .service(mock(PagedCrudService.class))
                    .keyFn(TestEntity::getId)
                    .build();

            String result = config.mp().message("testKey");
            assertThat(result).isEqualTo("testKey");
        }

        @Test
        @DisplayName("should use empty row actions when null")
        void shouldUseEmptyRowActionsWhenNull() {
            PagedCrudTableConfig<TestEntity, String, String> config = PagedCrudTableConfig.<TestEntity, String, String>builder()
                    .querySupplier(() -> "query")
                    .service(mock(PagedCrudService.class))
                    .keyFn(TestEntity::getId)
                    .build();

            assertThat(config.rowActions()).isEmpty();
        }

        @Test
        @DisplayName("should use default error handler when null")
        void shouldUseDefaultErrorHandlerWhenNull() {
            PagedCrudTableConfig<TestEntity, String, String> config = PagedCrudTableConfig.<TestEntity, String, String>builder()
                    .querySupplier(() -> "query")
                    .service(mock(PagedCrudService.class))
                    .keyFn(TestEntity::getId)
                    .build();

            // Should not throw
            config.errorHandler().accept(new RuntimeException("test"));
        }

        @Test
        @DisplayName("should accept custom copier")
        void shouldAcceptCustomCopier() {
            PagedCrudTableConfig<TestEntity, String, String> config = PagedCrudTableConfig.<TestEntity, String, String>builder()
                    .querySupplier(() -> "query")
                    .service(mock(PagedCrudService.class))
                    .keyFn(TestEntity::getId)
                    .copier((src, target) -> {
                        // Custom copy logic
                    })
                    .build();

            assertThat(config.copier()).isNotNull();
        }

        @Test
        @DisplayName("should accept custom message provider")
        void shouldAcceptCustomMessageProvider() {
            MessageProvider customProvider = (key, args) -> "custom:" + key;

            PagedCrudTableConfig<TestEntity, String, String> config = PagedCrudTableConfig.<TestEntity, String, String>builder()
                    .querySupplier(() -> "query")
                    .service(mock(PagedCrudService.class))
                    .keyFn(TestEntity::getId)
                    .messageProvider(customProvider)
                    .build();

            assertThat(config.mp()).isSameAs(customProvider);
        }

        @Test
        @DisplayName("should accept custom row actions")
        void shouldAcceptCustomRowActions() {
            RowAction<TestEntity> action = new RowAction<>("key", "icon", null, e -> {});

            PagedCrudTableConfig<TestEntity, String, String> config = PagedCrudTableConfig.<TestEntity, String, String>builder()
                    .querySupplier(() -> "query")
                    .service(mock(PagedCrudService.class))
                    .keyFn(TestEntity::getId)
                    .rowActions(List.of(action))
                    .build();

            assertThat(config.rowActions()).hasSize(1);
        }

        @Test
        @DisplayName("should preserve custom row actions list")
        void shouldPreserveCustomRowActionsList() {
            RowAction<TestEntity> action1 = new RowAction<>("key1", "icon1", null, e -> {});
            RowAction<TestEntity> action2 = new RowAction<>("key2", "icon2", null, e -> {});

            PagedCrudTableConfig<TestEntity, String, String> config = PagedCrudTableConfig.<TestEntity, String, String>builder()
                    .querySupplier(() -> "query")
                    .service(mock(PagedCrudService.class))
                    .keyFn(TestEntity::getId)
                    .rowActions(List.of(action1, action2))
                    .build();

            assertThat(config.rowActions()).hasSize(2);
            assertThat(config.rowActions().get(0).textKey()).isEqualTo("key1");
            assertThat(config.rowActions().get(1).textKey()).isEqualTo("key2");
        }
    }

    @Nested
    @DisplayName("noopCopier()")
    class NoopCopierTests {

        @Test
        @DisplayName("should return a no-op copier")
        void shouldReturnNoOpCopier() {
            BiConsumer<Object, Object> copier = PagedCrudTableConfig.noopCopier();

            // Should not throw
            copier.accept("source", "target");
        }

        @Test
        @DisplayName("should work with different types")
        void shouldWorkWithDifferentTypes() {
            BiConsumer<Integer, Integer> copier = PagedCrudTableConfig.noopCopier();

            copier.accept(1, 2);
            copier.accept(null, null);
        }
    }

    private PagedCrudService<TestEntity, String, String> mock(Class<PagedCrudService> clazz) {
        return org.mockito.Mockito.mock(clazz);
    }
}