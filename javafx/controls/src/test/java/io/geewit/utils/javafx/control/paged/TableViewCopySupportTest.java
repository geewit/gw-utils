package io.geewit.utils.javafx.control.paged;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("TableViewCopySupport Tests")
@ExtendWith(ApplicationExtension.class)
class TableViewCopySupportTest {

    @Nested
    @DisplayName("installCellCopy")
    class InstallCellCopy {

        @Test
        @DisplayName("should throw NullPointerException when tableView is null")
        void shouldThrowWhenNull() {
            assertThatThrownBy(() -> TableViewCopySupport.installCellCopy(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("tableView must not be null");
        }

        @Test
        @DisplayName("should enable cell selection when installed")
        void shouldEnableCellSelection() {
            TableView<String> tableView = new TableView<>();
            tableView.getItems().addAll("A", "B", "C");

            TableViewCopySupport.installCellCopy(tableView);

            assertThat(tableView.getSelectionModel().isCellSelectionEnabled()).isTrue();
        }

        @Test
        @DisplayName("should not duplicate install")
        void shouldNotDuplicateInstall() {
            TableView<String> tableView = new TableView<>();

            TableViewCopySupport.installCellCopy(tableView);
            TableViewCopySupport.installCellCopy(tableView);

            // Should not throw and should still work
            assertThat(tableView.getSelectionModel().isCellSelectionEnabled()).isTrue();
        }

        @Test
        @DisplayName("should not throw when no cells selected")
        void shouldNotThrowWhenNoSelection() {
            TableView<String> tableView = new TableView<>();
            TableViewCopySupport.installCellCopy(tableView);

            KeyEvent event = new KeyEvent(
                    KeyEvent.KEY_PRESSED,
                    "c",
                    "c",
                    KeyCode.C,
                    false,
                    false,
                    false,
                    true
            );

            // Should not throw
            tableView.fireEvent(event);
            assertThat(event.isConsumed()).isFalse();
        }
    }

    @Nested
    @DisplayName("buildTsv")
    class BuildTsv {

        @Test
        @DisplayName("should build TSV from selected cells")
        void shouldBuildTsv() {
            TableView<String> tableView = new TableView<>();
            tableView.getItems().addAll("Alice", "Bob");

            TableColumn<String, String> col1 = new TableColumn<>("Name");
            col1.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()));
            TableColumn<String, String> col2 = new TableColumn<>("Upper");
            col2.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().toUpperCase()));
            tableView.getColumns().addAll(col1, col2);

            List<TablePosition> positions = List.of(
                    new TablePosition<>(tableView, 0, col1),
                    new TablePosition<>(tableView, 0, col2),
                    new TablePosition<>(tableView, 1, col1),
                    new TablePosition<>(tableView, 1, col2)
            );

            String tsv = TableViewCopySupport.buildTsv(tableView, FXCollections.observableArrayList(positions));

            String expected = "Alice\tALICE" + System.lineSeparator() + "Bob\tBOB";
            assertThat(tsv).isEqualTo(expected);
        }

        @Test
        @DisplayName("should handle null cell values as empty string")
        void shouldHandleNullValues() {
            TableView<String> tableView = new TableView<>();
            tableView.getItems().add(null);

            TableColumn<String, String> col = new TableColumn<>("Name");
            col.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()));
            tableView.getColumns().add(col);

            List<TablePosition> positions = List.of(
                    new TablePosition<>(tableView, 0, col)
            );

            String tsv = TableViewCopySupport.buildTsv(tableView, javafx.collections.FXCollections.observableArrayList(positions));
            assertThat(tsv).isEqualTo("");
        }

        @Test
        @DisplayName("should sanitize tab and newline characters")
        void shouldSanitizeSpecialChars() {
            TableView<String> tableView = new TableView<>();
            tableView.getItems().add("A\tB\nC\rD");

            TableColumn<String, String> col = new TableColumn<>("Value");
            col.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()));
            tableView.getColumns().add(col);

            List<TablePosition> positions = List.of(
                    new TablePosition<>(tableView, 0, col)
            );

            String tsv = TableViewCopySupport.buildTsv(tableView, javafx.collections.FXCollections.observableArrayList(positions));
            assertThat(tsv).isEqualTo("A B C D");
        }

        @Test
        @DisplayName("should return empty string for empty selection")
        void shouldReturnEmptyForEmptySelection() {
            TableView<String> tableView = new TableView<>();
            String tsv = TableViewCopySupport.buildTsv(tableView, javafx.collections.FXCollections.observableArrayList());
            assertThat(tsv).isEmpty();
        }
    }

    @Nested
    @DisplayName("applySelectableTextFields")
    class ApplySelectableTextFields {

        @Test
        @DisplayName("should install selectable factory for matching id column")
        void shouldInstallForMatchingId() {
            TableColumn<String, String> col = new TableColumn<>("Name");
            col.setId("name");

            TableViewCopySupport.applySelectableTextFields(
                    List.of(col),
                    Set.of("name")
            );

            assertThat(col.getCellFactory()).isNotNull();
        }

        @Test
        @DisplayName("should install selectable factory for matching userData column")
        void shouldInstallForMatchingUserData() {
            TableColumn<String, String> col = new TableColumn<>("Name");
            col.setUserData("name");

            TableViewCopySupport.applySelectableTextFields(
                    List.of(col),
                    Set.of("name")
            );

            assertThat(col.getCellFactory()).isNotNull();
        }

        @Test
        @DisplayName("should not modify non-matching columns")
        void shouldNotModifyNonMatching() {
            TableColumn<String, String> col = new TableColumn<>("Name");
            col.setId("other");

            Callback<TableColumn<String, String>, TableCell<String, String>> originalFactory = _ -> new TableCell<>();
            col.setCellFactory(originalFactory);

            TableViewCopySupport.applySelectableTextFields(
                    List.of(col),
                    Set.of("name")
            );

            assertThat(col.getCellFactory()).isSameAs(originalFactory);
        }

        @Test
        @DisplayName("should restore original factory when field removed")
        void shouldRestoreOriginalFactory() {
            TableColumn<String, String> col = new TableColumn<>("Name");
            col.setId("name");

            Callback<TableColumn<String, String>, TableCell<String, String>> originalFactory = _ -> new TableCell<>();
            col.setCellFactory(originalFactory);

            // First apply
            TableViewCopySupport.applySelectableTextFields(List.of(col), Set.of("name"));
            assertThat(col.getCellFactory()).isNotSameAs(originalFactory);

            // Then remove
            TableViewCopySupport.applySelectableTextFields(List.of(col), Set.of());
            assertThat(col.getCellFactory()).isSameAs(originalFactory);
        }

        @Test
        @DisplayName("should restore null factory when field removed")
        void shouldRestoreNullFactory() {
            TableColumn<String, String> col = new TableColumn<>("Name");
            col.setId("name");
            col.setCellFactory(null);

            // First apply
            TableViewCopySupport.applySelectableTextFields(List.of(col), Set.of("name"));
            assertThat(col.getCellFactory()).isNotNull();

            // Then remove
            TableViewCopySupport.applySelectableTextFields(List.of(col), Set.of());
            assertThat(col.getCellFactory()).isNull();
        }

        @Test
        @DisplayName("should handle nested columns")
        void shouldHandleNestedColumns() {
            TableColumn<String, String> parent = new TableColumn<>("Parent");
            TableColumn<String, String> child = new TableColumn<>("Child");
            child.setId("childId");
            parent.getColumns().add(child);

            TableViewCopySupport.applySelectableTextFields(
                    List.of(parent),
                    Set.of("childId")
            );

            assertThat(child.getCellFactory()).isNotNull();
        }

        @Test
        @DisplayName("should handle null and blank field ids")
        void shouldHandleNullAndBlankFieldIds() {
            TableColumn<String, String> col = new TableColumn<>("Name");
            col.setId("name");

            TableViewCopySupport.applySelectableTextFields(
                    List.of(col),
                    Arrays.asList("name", null, "", "  ")
            );

            assertThat(col.getCellFactory()).isNotNull();
        }

        @Test
        @DisplayName("should handle null columns collection")
        void shouldHandleNullColumns() {
            TableViewCopySupport.applySelectableTextFields(null, Set.of("name"));
            // Should not throw
        }
    }

    @Nested
    @DisplayName("selectableTextCellFactory")
    class SelectableTextCellFactory {

        @Test
        @DisplayName("should create cell with text field")
        void shouldCreateCellWithTextField() {
            TableColumn<String, String> col = new TableColumn<>("Name");
            Callback<TableColumn<String, String>, TableCell<String, String>> factory =
                    TableViewCopySupport.selectableTextCellFactory();

            TableCell<String, String> cell = factory.call(col);
            assertThat(cell).isNotNull();
        }

    }

    @Nested
    @DisplayName("flattenColumns")
    class FlattenColumns {

        @Test
        @DisplayName("should flatten nested columns")
        void shouldFlattenNested() {
            TableColumn<String, String> parent = new TableColumn<>("Parent");
            TableColumn<String, String> child1 = new TableColumn<>("Child1");
            TableColumn<String, String> child2 = new TableColumn<>("Child2");
            parent.getColumns().addAll(child1, child2);

            List<TableColumn<String, ?>> result = TableViewCopySupport.flattenColumns(List.of(parent));

            assertThat(result).containsExactly(child1, child2);
        }

        @Test
        @DisplayName("should return leaf columns directly")
        void shouldReturnLeafColumns() {
            TableColumn<String, String> col1 = new TableColumn<>("Col1");
            TableColumn<String, String> col2 = new TableColumn<>("Col2");

            List<TableColumn<String, ?>> result = TableViewCopySupport.flattenColumns(List.of(col1, col2));

            assertThat(result).containsExactly(col1, col2);
        }
    }

    @Nested
    @DisplayName("resolveFieldId")
    class ResolveFieldId {

        @Test
        @DisplayName("should return id when set")
        void shouldReturnId() {
            TableColumn<String, String> col = new TableColumn<>("Name");
            col.setId("username");

            assertThat(TableViewCopySupport.resolveFieldId(col)).isEqualTo("username");
        }

        @Test
        @DisplayName("should return trimmed id")
        void shouldReturnTrimmedId() {
            TableColumn<String, String> col = new TableColumn<>("Name");
            col.setId("  username  ");

            assertThat(TableViewCopySupport.resolveFieldId(col)).isEqualTo("username");
        }

        @Test
        @DisplayName("should fallback to userData when id is blank")
        void shouldFallbackToUserData() {
            TableColumn<String, String> col = new TableColumn<>("Name");
            col.setUserData("username");

            assertThat(TableViewCopySupport.resolveFieldId(col)).isEqualTo("username");
        }

        @Test
        @DisplayName("should prefer id over userData")
        void shouldPreferId() {
            TableColumn<String, String> col = new TableColumn<>("Name");
            col.setId("id");
            col.setUserData("userData");

            assertThat(TableViewCopySupport.resolveFieldId(col)).isEqualTo("id");
        }

        @Test
        @DisplayName("should return null when neither id nor userData is set")
        void shouldReturnNull() {
            TableColumn<String, String> col = new TableColumn<>("Name");

            assertThat(TableViewCopySupport.resolveFieldId(col)).isNull();
        }
    }

    @Nested
    @DisplayName("normalizeFieldIds")
    class NormalizeFieldIds {

        @Test
        @DisplayName("should return empty set for null")
        void shouldReturnEmptyForNull() {
            assertThat(TableViewCopySupport.normalizeFieldIds(null)).isEqualTo(Set.of());
        }

        @Test
        @DisplayName("should return empty set for empty collection")
        void shouldReturnEmptyForEmpty() {
            assertThat(TableViewCopySupport.normalizeFieldIds(List.of())).isEqualTo(Set.of());
        }

        @Test
        @DisplayName("should trim and filter")
        void shouldTrimAndFilter() {
            Set<String> result = TableViewCopySupport.normalizeFieldIds(
                    Arrays.asList("a", "  b  ", null, "", "  ")
            );
            assertThat(result).containsExactly("a", "b");
        }
    }
}
