package io.geewit.utils.javafx.control.paged;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

import javafx.scene.control.TableView;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PagedCrudTableSkin Tests")
@ExtendWith(ApplicationExtension.class)
class PagedCrudTableSkinTest {

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
        @DisplayName("should create skin with table and pagination")
        void shouldCreateSkinWithTableAndPagination() {
            PagedCrudTableControl<TestEntity, String, String> control = new PagedCrudTableControl<>();
            PagedCrudTableSkin<TestEntity, String, String> skin = new PagedCrudTableSkin<>(control);

            assertThat(skin.table).isNotNull();
            assertThat(skin.pagination).isNotNull();
        }

        @Test
        @DisplayName("should create Actions instance")
        void shouldCreateActionsInstance() {
            PagedCrudTableControl<TestEntity, String, String> control = new PagedCrudTableControl<>();
            PagedCrudTableSkin<TestEntity, String, String> skin = new PagedCrudTableSkin<>(control);

            assertThat(skin.actions).isNotNull();
        }
    }

    @Nested
    @DisplayName("tableView()")
    class TableViewMethod {

        @Test
        @DisplayName("should return the table view")
        void shouldReturnTableView() {
            PagedCrudTableControl<TestEntity, String, String> control = new PagedCrudTableControl<>();
            PagedCrudTableSkin<TestEntity, String, String> skin = new PagedCrudTableSkin<>(control);

            TableView<TestEntity> table = skin.tableView();

            assertThat(table).isSameAs(skin.table);
        }
    }

    @Nested
    @DisplayName("rebuildColumns")
    class RebuildColumns {

        @Test
        @DisplayName("should rebuild columns when called")
        void shouldRebuildColumnsWhenCalled() {
            PagedCrudTableControl<TestEntity, String, String> control = new PagedCrudTableControl<>();
            PagedCrudTableSkin<TestEntity, String, String> skin = new PagedCrudTableSkin<>(control);

            // Initially empty
            assertThat(skin.table.getColumns()).isEmpty();

            // Set columns on control
            javafx.scene.control.TableColumn<TestEntity, String> col = new javafx.scene.control.TableColumn<>("Name");
            control.setColumns(List.of(col));

            // Skin should have updated columns
            assertThat(skin.table.getColumns()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("loadPage")
    class LoadPage {

        @Test
        @DisplayName("should not load page when config is null")
        void shouldNotLoadPageWhenConfigIsNull() {
            PagedCrudTableControl<TestEntity, String, String> control = new PagedCrudTableControl<>();
            PagedCrudTableSkin<TestEntity, String, String> skin = new PagedCrudTableSkin<>(control);

            // Should not throw
            skin.loadPage(0);
        }

        @Test
        @DisplayName("should load page with data from service")
        void shouldLoadPageWithDataFromService() {
            PagedCrudTableControl<TestEntity, String, String> control = new PagedCrudTableControl<>();
            control.setConfig(createConfig());
            PagedCrudTableSkin<TestEntity, String, String> skin = new PagedCrudTableSkin<>(control);

            // Should not throw
            skin.loadPage(0);
        }
    }

    @Nested
    @DisplayName("selectedRows")
    class SelectedRows {

        @Test
        @DisplayName("should return empty list when no selection")
        void shouldReturnEmptyListWhenNoSelection() {
            PagedCrudTableControl<TestEntity, String, String> control = new PagedCrudTableControl<>();
            PagedCrudTableSkin<TestEntity, String, String> skin = new PagedCrudTableSkin<>(control);

            List<TestEntity> selected = skin.selectedRows();

            assertThat(selected).isEmpty();
        }

        @Test
        @DisplayName("should return selected item when single selection")
        void shouldReturnSelectedItemWhenSingleSelection() {
            PagedCrudTableControl<TestEntity, String, String> control = new PagedCrudTableControl<>();
            PagedCrudTableSkin<TestEntity, String, String> skin = new PagedCrudTableSkin<>(control);

            TestEntity entity = new TestEntity("1", "Test");
            skin.table.getSelectionModel().select(entity);

            List<TestEntity> selected = skin.selectedRows();

            assertThat(selected).hasSize(1);
            assertThat(selected.get(0)).isEqualTo(entity);
        }
    }

    @Nested
    @DisplayName("deleteBatch")
    class DeleteBatch {

        @Test
        @DisplayName("should return completed future when ids empty")
        void shouldReturnCompletedFutureWhenIdsEmpty() {
            PagedCrudTableControl<TestEntity, String, String> control = new PagedCrudTableControl<>();
            control.setConfig(createConfig());
            PagedCrudTableSkin<TestEntity, String, String> skin = new PagedCrudTableSkin<>(control);

            CompletionStage<Void> result = skin.deleteBatch(createConfig(), List.of());

            assertThat(result.toCompletableFuture()).isCompleted();
        }
    }

    @Nested
    @DisplayName("upsertWriteBack")
    class UpsertWriteBack {

        @Test
        @DisplayName("should accept consumer without throwing")
        void shouldAcceptConsumerWithoutThrowing() {
            PagedCrudTableControl<TestEntity, String, String> control = new PagedCrudTableControl<>();
            control.setConfig(createConfig());
            PagedCrudTableSkin<TestEntity, String, String> skin = new PagedCrudTableSkin<>(control);

            TestEntity newEntity = new TestEntity("1", "New");

            // Should not throw when accepting new entity
            skin.upsertWriteBack(createConfig()).accept(newEntity);
        }

        @Test
        @DisplayName("should handle null item without throwing")
        void shouldHandleNullItemWithoutThrowing() {
            PagedCrudTableControl<TestEntity, String, String> control = new PagedCrudTableControl<>();
            control.setConfig(createConfig());
            PagedCrudTableSkin<TestEntity, String, String> skin = new PagedCrudTableSkin<>(control);

            // Should not throw when accepting null
            skin.upsertWriteBack(createConfig()).accept(null);
        }
    }

    @Nested
    @DisplayName("runOnFx")
    class RunOnFx {

        @Test
        @DisplayName("should not throw when called")
        void shouldNotThrowWhenCalled() {
            PagedCrudTableControl<TestEntity, String, String> control = new PagedCrudTableControl<>();
            PagedCrudTableSkin<TestEntity, String, String> skin = new PagedCrudTableSkin<>(control);

            // Should not throw
            PagedCrudTableSkin.runOnFx(() -> {});
        }
    }

    @Nested
    @DisplayName("selectable text fields")
    class SelectableTextFields {

        @Test
        @DisplayName("should apply selectable text field factory to matching columns")
        void shouldApplySelectableFactory() {
            PagedCrudTableControl<TestEntity, String, String> control = new PagedCrudTableControl<>();
            control.setConfig(createConfig());

            javafx.scene.control.TableColumn<TestEntity, String> nameCol = new javafx.scene.control.TableColumn<>("Name");
            nameCol.setId("name");
            javafx.scene.control.TableColumn<TestEntity, String> idCol = new javafx.scene.control.TableColumn<>("ID");
            idCol.setId("id");

            control.setColumns(List.of(nameCol, idCol));
            control.setSelectableTextFieldIds(Set.of("name"));

            PagedCrudTableSkin<TestEntity, String, String> skin = new PagedCrudTableSkin<>(control);

            // The matching column should have its factory changed
            assertThat(nameCol.getCellFactory()).isNotNull();
            // The non-matching column should not be the same as the matching column's factory
            assertThat(idCol.getCellFactory()).isNotSameAs(nameCol.getCellFactory());
        }

        @Test
        @DisplayName("should restore original factory when selectable field ids change")
        void shouldRestoreOriginalFactory() {
            PagedCrudTableControl<TestEntity, String, String> control = new PagedCrudTableControl<>();
            control.setConfig(createConfig());

            javafx.scene.control.TableColumn<TestEntity, String> nameCol = new javafx.scene.control.TableColumn<>("Name");
            nameCol.setId("name");
            javafx.scene.control.TableCell<TestEntity, String> originalCell = new javafx.scene.control.TableCell<>();
            javafx.util.Callback<javafx.scene.control.TableColumn<TestEntity, String>, javafx.scene.control.TableCell<TestEntity, String>> originalFactory = _ -> originalCell;
            nameCol.setCellFactory(originalFactory);

            control.setColumns(List.of(nameCol));
            control.setSelectableTextFieldIds(Set.of("name"));

            PagedCrudTableSkin<TestEntity, String, String> skin = new PagedCrudTableSkin<>(control);

            // After applying selectable, factory should be different
            assertThat(nameCol.getCellFactory()).isNotSameAs(originalFactory);

            // Change selectable fields to empty
            control.setSelectableTextFieldIds(Set.of());

            // Factory should be restored
            assertThat(nameCol.getCellFactory()).isSameAs(originalFactory);
        }

        @Test
        @DisplayName("should install cell copy support")
        void shouldInstallCellCopySupport() {
            PagedCrudTableControl<TestEntity, String, String> control = new PagedCrudTableControl<>();
            PagedCrudTableSkin<TestEntity, String, String> skin = new PagedCrudTableSkin<>(control);

            assertThat(skin.table.getSelectionModel().isCellSelectionEnabled()).isTrue();
        }
    }
}