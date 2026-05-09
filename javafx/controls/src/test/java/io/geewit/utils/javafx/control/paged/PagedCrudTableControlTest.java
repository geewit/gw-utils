package io.geewit.utils.javafx.control.paged;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("PagedCrudTableControl Tests")
@ExtendWith(ApplicationExtension.class)
class PagedCrudTableControlTest {

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
        @DisplayName("should add paged-crud-table style class")
        void shouldAddStyleClass() {
            PagedCrudTableControl<TestEntity, String, String> control = new PagedCrudTableControl<>();

            assertThat(control.getStyleClass()).contains("paged-crud-table");
        }

        @Test
        @DisplayName("should initialize with empty items list")
        void shouldInitializeWithEmptyItems() {
            PagedCrudTableControl<TestEntity, String, String> control = new PagedCrudTableControl<>();

            assertThat(control.getItems()).isEmpty();
        }
    }

    @Nested
    @DisplayName("initialize")
    class Initialize {

        @Test
        @DisplayName("should set columns and config")
        void shouldSetColumnsAndConfig() {
            PagedCrudTableControl<TestEntity, String, String> control = new PagedCrudTableControl<>();
            TableColumn<TestEntity, String> col = new TableColumn<>("Name");
            List<TableColumn<TestEntity, ?>> columns = List.of(col);

            control.initialize(columns, createConfig());

            assertThat(control.getColumns()).hasSize(1);
            assertThat(control.getConfig()).isNotNull();
        }

        @Test
        @DisplayName("should throw NullPointerException when config is null")
        void shouldThrowWhenConfigIsNull() {
            PagedCrudTableControl<TestEntity, String, String> control = new PagedCrudTableControl<>();

            assertThatThrownBy(() -> control.initialize(List.of(), null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("config");
        }
    }

    @Nested
    @DisplayName("columns property")
    class ColumnsProperty {

        @Test
        @DisplayName("should get columns")
        void shouldGetColumns() {
            PagedCrudTableControl<TestEntity, String, String> control = new PagedCrudTableControl<>();
            TableColumn<TestEntity, String> col = new TableColumn<>("Name");
            control.setColumns(List.of(col));

            assertThat(control.getColumns()).hasSize(1);
        }

        @Test
        @DisplayName("should handle null columns")
        void shouldHandleNullColumns() {
            PagedCrudTableControl<TestEntity, String, String> control = new PagedCrudTableControl<>();

            control.setColumns(null);

            assertThat(control.getColumns()).isEmpty();
        }

        @Test
        @DisplayName("should return defensive copy")
        void shouldReturnDefensiveCopy() {
            PagedCrudTableControl<TestEntity, String, String> control = new PagedCrudTableControl<>();
            TableColumn<TestEntity, String> col = new TableColumn<>("Name");
            List<TableColumn<TestEntity, ?>> original = new java.util.ArrayList<>(List.of(col));
            control.setColumns(original);

            original.clear();

            assertThat(control.getColumns()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("config property")
    class ConfigProperty {

        @Test
        @DisplayName("should get and set config")
        void shouldGetAndSetConfig() {
            PagedCrudTableControl<TestEntity, String, String> control = new PagedCrudTableControl<>();
            PagedCrudTableConfig<TestEntity, String, String> config = createConfig();

            control.setConfig(config);

            assertThat(control.getConfig()).isSameAs(config);
        }

        @Test
        @DisplayName("should throw NullPointerException when setting null config")
        void shouldThrowWhenSettingNullConfig() {
            PagedCrudTableControl<TestEntity, String, String> control = new PagedCrudTableControl<>();

            assertThatThrownBy(() -> control.setConfig(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("config must not be null");
        }
    }

    @Nested
    @DisplayName("search")
    class Search {

        @Test
        @DisplayName("should accept config without throwing")
        void shouldAcceptConfigWithoutThrowing() {
            PagedCrudTableControl<TestEntity, String, String> control = new PagedCrudTableControl<>();
            TableColumn<TestEntity, String> col = new TableColumn<>("Name");
            PagedCrudTableConfig<TestEntity, String, String> config = createConfig();

            // Should not throw when setting config
            control.setConfig(config);
            assertThat(control.getConfig()).isNotNull();
        }
    }

    @Nested
    @DisplayName("selection")
    class Selection {

        @Test
        @DisplayName("should have no selected item initially")
        void shouldHaveNoSelectedItemInitially() {
            PagedCrudTableControl<TestEntity, String, String> control = new PagedCrudTableControl<>();

            assertThat(control.getSelectedItem()).isNull();
        }

        @Test
        @DisplayName("should provide selectedItem property")
        void shouldProvideSelectedItemProperty() {
            PagedCrudTableControl<TestEntity, String, String> control = new PagedCrudTableControl<>();

            assertThat(control.selectedItemProperty()).isNotNull();
        }
    }

    @Nested
    @DisplayName("getTableView()")
    class GetTableView {

        @Test
        @DisplayName("should throw IllegalStateException when skin not ready")
        void shouldThrowWhenSkinNotReady() {
            PagedCrudTableControl<TestEntity, String, String> control = new PagedCrudTableControl<>();

            // Skin not initialized yet, getTableView should throw
            assertThatThrownBy(control::getTableView)
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("actions")
    class Actions {

        @Test
        @DisplayName("should return config when set")
        void shouldReturnConfigWhenSet() {
            PagedCrudTableControl<TestEntity, String, String> control = new PagedCrudTableControl<>();
            PagedCrudTableConfig<TestEntity, String, String> config = createConfig();
            control.setConfig(config);

            // Verify config is set
            assertThat(control.getConfig()).isSameAs(config);
        }
    }

    @Nested
    @DisplayName("selectableTextFieldIds")
    class SelectableTextFieldIds {

        @Test
        @DisplayName("should default to empty set")
        void shouldDefaultToEmptySet() {
            PagedCrudTableControl<TestEntity, String, String> control = new PagedCrudTableControl<>();

            assertThat(control.getSelectableTextFieldIds()).isEqualTo(Set.of());
        }

        @Test
        @DisplayName("should set from collection and normalize")
        void shouldSetFromCollection() {
            PagedCrudTableControl<TestEntity, String, String> control = new PagedCrudTableControl<>();

            control.setSelectableTextFieldIds(java.util.Arrays.asList("name", "  remark  ", null, ""));

            assertThat(control.getSelectableTextFieldIds()).containsExactly("name", "remark");
        }

        @Test
        @DisplayName("should set from varargs and normalize")
        void shouldSetFromVarargs() {
            PagedCrudTableControl<TestEntity, String, String> control = new PagedCrudTableControl<>();

            control.setSelectableTextFieldIds("name", "  remark  ", null, "");

            assertThat(control.getSelectableTextFieldIds()).containsExactly("name", "remark");
        }

        @Test
        @DisplayName("should return empty set when set to null collection")
        void shouldHandleNullCollection() {
            PagedCrudTableControl<TestEntity, String, String> control = new PagedCrudTableControl<>();

            control.setSelectableTextFieldIds((Collection<String>) null);

            assertThat(control.getSelectableTextFieldIds()).isEqualTo(Set.of());
        }

        @Test
        @DisplayName("should return empty set when varargs is null")
        void shouldHandleNullVarargs() {
            PagedCrudTableControl<TestEntity, String, String> control = new PagedCrudTableControl<>();

            control.setSelectableTextFieldIds((String[]) null);

            assertThat(control.getSelectableTextFieldIds()).isEqualTo(Set.of());
        }

        @Test
        @DisplayName("should provide property")
        void shouldProvideProperty() {
            PagedCrudTableControl<TestEntity, String, String> control = new PagedCrudTableControl<>();

            assertThat(control.selectableTextFieldIdsProperty()).isNotNull();
        }

        @Test
        @DisplayName("should initialize with selectable field ids")
        void shouldInitializeWithSelectableFieldIds() {
            PagedCrudTableControl<TestEntity, String, String> control = new PagedCrudTableControl<>();
            TableColumn<TestEntity, String> col = new TableColumn<>("Name");

            control.initialize(List.of(col), createConfig(), Set.of("name", "remark"));

            assertThat(control.getSelectableTextFieldIds()).containsExactly("name", "remark");
            assertThat(control.getConfig()).isNotNull();
            assertThat(control.getColumns()).hasSize(1);
        }
    }
}