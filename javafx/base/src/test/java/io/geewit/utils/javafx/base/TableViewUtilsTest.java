package io.geewit.utils.javafx.base;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@ExtendWith(ApplicationExtension.class)
public class TableViewUtilsTest {

    @Test
    void adjustTableHeight_shouldHandleNullTableView() {
        // Should not throw
        TableViewUtils.adjustTableHeight(null);
        TableViewUtils.adjustTableHeight(null, null);
    }

    @Test
    void adjustTableHeight_shouldCalculateWithNoStage() {
        javafx.scene.control.TableView<String> tableView = new javafx.scene.control.TableView<>();
        tableView.getItems().addAll("a", "b", "c");

        TableViewUtils.adjustTableHeight(tableView);

        assertThat(tableView.getPrefHeight()).isGreaterThan(0);
    }

    @Test
    void adjustTableHeight_shouldUseDefaultRowHeight() {
        javafx.scene.control.TableView<String> tableView = new javafx.scene.control.TableView<>();
        tableView.getItems().addAll("a", "b", "c", "d", "e");

        TableViewUtils.adjustTableHeight(tableView);

        // Default row height is 32, header is 28, with padding of 6
        // visibleRows = min(max(5, 1), 10) = 5
        // prefHeight = 28 + 5 * 32 + 6 = 194
        assertThat(tableView.getPrefHeight()).isCloseTo(194, within(5.0));
    }

    @Test
    void adjustTableHeight_shouldUseFixedCellSize() {
        javafx.scene.control.TableView<String> tableView = new javafx.scene.control.TableView<>();
        tableView.setFixedCellSize(48);
        tableView.getItems().addAll("a", "b", "c");

        TableViewUtils.adjustTableHeight(tableView);

        // Should use 48 instead of default 32
        assertThat(tableView.getPrefHeight()).isGreaterThan(28 + 3 * 48);
    }

    @Test
    void adjustTableHeight_shouldClampVisibleRows() {
        javafx.scene.control.TableView<String> tableView = new javafx.scene.control.TableView<>();
        tableView.getItems().addAll("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k");

        TableViewUtils.adjustTableHeight(tableView);

        // maxVisibleRows is 10, so should clamp at 10
        // prefHeight = 28 + 10 * 32 + 6 = 354
        // maxHeight = 28 + 11 * 32 + 6 = 386
        // The actual prefHeight depends on platform-specific insets, so use a wide range
        assertThat(tableView.getPrefHeight()).isGreaterThan(340);
        assertThat(tableView.getPrefHeight()).isLessThan(400);
    }

    @Test
    void adjustTableHeight_withCustomParameters() {
        javafx.scene.control.TableView<String> tableView = new javafx.scene.control.TableView<>();
        tableView.getItems().addAll("a", "b", "c", "d", "e");
        javafx.stage.Stage stage = null;

        TableViewUtils.adjustTableHeight(tableView, stage, 40, 30, 2, 8);

        assertThat(tableView.getPrefHeight()).isGreaterThan(0);
        assertThat(tableView.getMinHeight()).isGreaterThan(0);
        assertThat(tableView.getMaxHeight()).isGreaterThan(0);
    }

    @Test
    void constants_shouldHaveCorrectValues() {
        assertThat(TableViewUtils.DEFAULT_ROW_HEIGHT).isEqualTo(32);
        assertThat(TableViewUtils.TABLE_HEADER_HEIGHT).isEqualTo(28);
        assertThat(TableViewUtils.MIN_VISIBLE_ROWS).isEqualTo(1);
        assertThat(TableViewUtils.MAX_VISIBLE_ROWS).isEqualTo(10);
    }
}
