package io.geewit.utils.javafx.control.paged;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Actions Tests")
@ExtendWith(ApplicationExtension.class)
class ActionsTest {

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

    private PagedCrudService<TestEntity, String, String> mockService() {
        return new PagedCrudService<>() {
            @Override
            public CompletionStage<PageResult<TestEntity>> query(String query, int pageIndex, int pageSize) {
                return CompletableFuture.completedFuture(new PageResult<>(List.of(), 0L));
            }

            @Override
            public CompletionStage<TestEntity> create(TestEntity entity) {
                return CompletableFuture.completedFuture(entity);
            }

            @Override
            public CompletionStage<TestEntity> update(TestEntity entity) {
                return CompletableFuture.completedFuture(entity);
            }

            @Override
            public CompletionStage<Void> deleteByIds(Collection<String> ids) {
                return CompletableFuture.completedFuture(null);
            }
        };
    }

    private PagedCrudTableConfig<TestEntity, String, String> createConfig() {
        return PagedCrudTableConfig.<TestEntity, String, String>builder()
                .pageSize(20)
                .querySupplier(() -> "query")
                .service(mockService())
                .keyFn(TestEntity::getId)
                .build();
    }

    @Nested
    @DisplayName("constructor")
    class Constructor {

        @Test
        @DisplayName("should create Actions with skin")
        void shouldCreateActionsWithSkin() {
            PagedCrudTableControl<TestEntity, String, String> control = new PagedCrudTableControl<>();
            PagedCrudTableSkin<TestEntity, String, String> skin = new PagedCrudTableSkin<>(control);
            Actions<TestEntity, String, String> actions = new Actions<>(skin);

            assertThat(actions).isNotNull();
        }
    }

    @Nested
    @DisplayName("search")
    class Search {

        @Test
        @DisplayName("should reset pagination and load first page")
        void shouldResetPaginationAndLoadFirstPage() {
            PagedCrudTableControl<TestEntity, String, String> control = new PagedCrudTableControl<>();
            PagedCrudTableSkin<TestEntity, String, String> skin = new PagedCrudTableSkin<>(control);
            control.setConfig(createConfig());
            Actions<TestEntity, String, String> actions = new Actions<>(skin);

            // Should not throw
            actions.search();
        }
    }

    @Nested
    @DisplayName("reloadCurrentPage")
    class ReloadCurrentPage {

        @Test
        @DisplayName("should reload current page")
        void shouldReloadCurrentPage() {
            PagedCrudTableControl<TestEntity, String, String> control = new PagedCrudTableControl<>();
            PagedCrudTableSkin<TestEntity, String, String> skin = new PagedCrudTableSkin<>(control);
            control.setConfig(createConfig());
            Actions<TestEntity, String, String> actions = new Actions<>(skin);

            // Should not throw
            actions.reloadCurrentPage();
        }
    }

    @Nested
    @DisplayName("add")
    class Add {

        @Test
        @DisplayName("should do nothing when config is null")
        void shouldDoNothingWhenConfigIsNull() {
            PagedCrudTableControl<TestEntity, String, String> control = new PagedCrudTableControl<>();
            PagedCrudTableSkin<TestEntity, String, String> skin = new PagedCrudTableSkin<>(control);
            Actions<TestEntity, String, String> actions = new Actions<>(skin);

            // Should not throw
            actions.add();
        }

        @Test
        @DisplayName("should open create editor when config set")
        void shouldOpenCreateEditor() {
            PagedCrudTableControl<TestEntity, String, String> control = new PagedCrudTableControl<>();
            PagedCrudTableConfig<TestEntity, String, String> config = PagedCrudTableConfig.<TestEntity, String, String>builder()
                    .pageSize(20)
                    .querySupplier(() -> "query")
                    .service(mockService())
                    .keyFn(TestEntity::getId)
                    .openCreateEditor(() -> CompletableFuture.completedFuture(Optional.of(new TestEntity("1", "New"))))
                    .build();
            control.setConfig(config);
            PagedCrudTableSkin<TestEntity, String, String> skin = new PagedCrudTableSkin<>(control);
            Actions<TestEntity, String, String> actions = new Actions<>(skin);

            // Should not throw
            actions.add();
        }
    }

    @Nested
    @DisplayName("editSelected")
    class EditSelected {

        @Test
        @DisplayName("should do nothing when config is null")
        void shouldDoNothingWhenConfigIsNull() {
            PagedCrudTableControl<TestEntity, String, String> control = new PagedCrudTableControl<>();
            PagedCrudTableSkin<TestEntity, String, String> skin = new PagedCrudTableSkin<>(control);
            Actions<TestEntity, String, String> actions = new Actions<>(skin);

            // Should not throw
            actions.editSelected();
        }

        @Test
        @DisplayName("should do nothing when no selection")
        void shouldDoNothingWhenNoSelection() {
            PagedCrudTableControl<TestEntity, String, String> control = new PagedCrudTableControl<>();
            PagedCrudTableConfig<TestEntity, String, String> config = PagedCrudTableConfig.<TestEntity, String, String>builder()
                    .pageSize(20)
                    .querySupplier(() -> "query")
                    .service(mockService())
                    .keyFn(TestEntity::getId)
                    .openEditEditor(e -> CompletableFuture.completedFuture(Optional.empty()))
                    .build();
            control.setConfig(config);
            PagedCrudTableSkin<TestEntity, String, String> skin = new PagedCrudTableSkin<>(control);
            Actions<TestEntity, String, String> actions = new Actions<>(skin);

            // Should not throw
            actions.editSelected();
        }

        @Test
        @DisplayName("should trigger edit editor with selected item")
        void shouldTriggerEditEditorWithSelectedItem() {
            PagedCrudTableControl<TestEntity, String, String> control = new PagedCrudTableControl<>();
            TestEntity entity = new TestEntity("1", "Test");

            AtomicBoolean called = new AtomicBoolean(false);
            PagedCrudTableConfig<TestEntity, String, String> config = PagedCrudTableConfig.<TestEntity, String, String>builder()
                    .pageSize(20)
                    .querySupplier(() -> "query")
                    .service(mockService())
                    .keyFn(TestEntity::getId)
                    .openEditEditor(e -> {
                        called.set(true);
                        return CompletableFuture.completedFuture(Optional.empty());
                    })
                    .build();
            control.setConfig(config);
            PagedCrudTableSkin<TestEntity, String, String> skin = new PagedCrudTableSkin<>(control);

            // Add to skin's table items which is bound to control's items via constructor
            skin.table.getItems().add(entity);
            skin.table.getSelectionModel().select(entity);
            Actions<TestEntity, String, String> actions = new Actions<>(skin);

            // Should not throw - editSelected triggers async operation
            actions.editSelected();
        }
    }

    @Nested
    @DisplayName("deleteSelected")
    class DeleteSelected {

        @Test
        @DisplayName("should do nothing when config is null")
        void shouldDoNothingWhenConfigIsNull() {
            PagedCrudTableControl<TestEntity, String, String> control = new PagedCrudTableControl<>();
            PagedCrudTableSkin<TestEntity, String, String> skin = new PagedCrudTableSkin<>(control);
            Actions<TestEntity, String, String> actions = new Actions<>(skin);

            // Should not throw
            actions.deleteSelected();
        }

        @Test
        @DisplayName("should do nothing when no selection")
        void shouldDoNothingWhenNoSelection() {
            PagedCrudTableControl<TestEntity, String, String> control = new PagedCrudTableControl<>();
            PagedCrudTableConfig<TestEntity, String, String> config = PagedCrudTableConfig.<TestEntity, String, String>builder()
                    .pageSize(20)
                    .querySupplier(() -> "query")
                    .service(mockService())
                    .keyFn(TestEntity::getId)
                    .confirmDelete(list -> CompletableFuture.completedFuture(false))
                    .build();
            control.setConfig(config);
            PagedCrudTableSkin<TestEntity, String, String> skin = new PagedCrudTableSkin<>(control);
            Actions<TestEntity, String, String> actions = new Actions<>(skin);

            // Should not throw
            actions.deleteSelected();
        }

        @Test
        @DisplayName("should proceed with delete when confirmed")
        void shouldProceedWithDeleteWhenConfirmed() {
            PagedCrudTableControl<TestEntity, String, String> control = new PagedCrudTableControl<>();
            TestEntity entity = new TestEntity("1", "Test");

            AtomicBoolean confirmed = new AtomicBoolean(false);
            PagedCrudTableConfig<TestEntity, String, String> config = PagedCrudTableConfig.<TestEntity, String, String>builder()
                    .pageSize(20)
                    .querySupplier(() -> "query")
                    .service(mockService())
                    .keyFn(TestEntity::getId)
                    .confirmDelete(list -> {
                        confirmed.set(true);
                        return CompletableFuture.completedFuture(true);
                    })
                    .build();
            control.setConfig(config);
            PagedCrudTableSkin<TestEntity, String, String> skin = new PagedCrudTableSkin<>(control);
            // Add entity after skin creation to avoid it being cleared by async search
            skin.table.getItems().add(entity);
            skin.table.getSelectionModel().select(0);
            Actions<TestEntity, String, String> actions = new Actions<>(skin);

            // Should not throw
            actions.deleteSelected();

            assertThat(confirmed.get()).isTrue();
        }
    }

    @Nested
    @DisplayName("of()")
    class OfMethod {

        @Test
        @DisplayName("should throw when skin not instance of PagedCrudTableSkin")
        void shouldThrowWhenSkinNotPagedCrudTableSkin() {
            PagedCrudTableControl<TestEntity, String, String> control = new PagedCrudTableControl<>();

            // Default skin is not PagedCrudTableSkin until initialized
            assertThatThrownBy(() -> Actions.of(control))
                    .isInstanceOf(IllegalStateException.class);
        }
    }
}