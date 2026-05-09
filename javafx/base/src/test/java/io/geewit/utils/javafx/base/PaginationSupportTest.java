package io.geewit.utils.javafx.base;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(ApplicationExtension.class)
public class PaginationSupportTest {

    @Test
    void params_shouldRejectNullPagination() {
        assertThatThrownBy(() -> PaginationSupport.params(null, 10, 100))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Pagination must not be null");
    }

    @Test
    void params_shouldRejectZeroRowsPerPage() {
        var pagination = new javafx.scene.control.Pagination();
        assertThatThrownBy(() -> PaginationSupport.params(pagination, 0, 100))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("rowsPerPage must be greater than zero");
    }

    @Test
    void params_shouldRejectNegativeRowsPerPage() {
        var pagination = new javafx.scene.control.Pagination();
        assertThatThrownBy(() -> PaginationSupport.params(pagination, -1, 100))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("rowsPerPage must be greater than zero");
    }

    @Test
    void params_shouldAcceptZeroTotalItems() {
        var pagination = new javafx.scene.control.Pagination();
        var params = PaginationSupport.params(pagination, 10, 0);
        assertThat(params.totalItems()).isEqualTo(0);
    }

    @Test
    void params_shouldRejectNegativeTotalItems() {
        var pagination = new javafx.scene.control.Pagination();
        assertThatThrownBy(() -> PaginationSupport.params(pagination, 10, -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("totalItems must not be negative");
    }

    @Test
    void pageCount_shouldReturnZeroWhenTotalItemsIsZero() {
        var pagination = new javafx.scene.control.Pagination();
        var params = PaginationSupport.params(pagination, 10, 0);
        assertThat(params.pageCount()).isEqualTo(0);
    }

    @Test
    void pageCount_shouldCalculateCorrectly() {
        var pagination = new javafx.scene.control.Pagination();
        var params = PaginationSupport.params(pagination, 10, 95);
        assertThat(params.pageCount()).isEqualTo(10); // ceil(95/10) = 10
    }

    @Test
    void pageCount_shouldRoundUp() {
        var pagination = new javafx.scene.control.Pagination();
        var params = PaginationSupport.params(pagination, 10, 101);
        assertThat(params.pageCount()).isEqualTo(11); // ceil(101/10) = 11
    }

    @Test
    void pageCount_shouldHandleExactDivision() {
        var pagination = new javafx.scene.control.Pagination();
        var params = PaginationSupport.params(pagination, 10, 100);
        assertThat(params.pageCount()).isEqualTo(10); // exact division
    }

    @Test
    void applyPageCount_shouldSetPageCountAndClampIndex() {
        var pagination = new javafx.scene.control.Pagination();
        pagination.setPageCount(1);
        pagination.setCurrentPageIndex(5);

        var params = PaginationSupport.params(pagination, 10, 100);
        params.applyPageCount();

        assertThat(pagination.getPageCount()).isEqualTo(10);
        // The clamped index should be within valid range (0 to pageCount-1)
        assertThat(pagination.getCurrentPageIndex()).isLessThanOrEqualTo(9);
        assertThat(pagination.getCurrentPageIndex()).isGreaterThanOrEqualTo(0);
    }

    @Test
    void applyPageCount_shouldUseMinimumOfOne() {
        var pagination = new javafx.scene.control.Pagination();
        pagination.setPageCount(1);
        pagination.setCurrentPageIndex(0);

        var params = PaginationSupport.params(pagination, 10, 0);
        params.applyPageCount();

        assertThat(pagination.getPageCount()).isEqualTo(1); // min 1
    }

    @Test
    void clampPageIndex_shouldReturnZeroForNegativeIndex() {
        var pagination = new javafx.scene.control.Pagination();
        var params = PaginationSupport.params(pagination, 10, 100);

        assertThat(params.clampPageIndex(-1)).isEqualTo(0);
    }

    @Test
    void clampPageIndex_shouldReturnZeroWhenPageCountIsZero() {
        var pagination = new javafx.scene.control.Pagination();
        var params = PaginationSupport.params(pagination, 10, 0);

        assertThat(params.clampPageIndex(5)).isEqualTo(0);
    }

    @Test
    void clampPageIndex_shouldClampToLastIndex() {
        var pagination = new javafx.scene.control.Pagination();
        pagination.setCurrentPageIndex(0);
        var params = PaginationSupport.params(pagination, 10, 100);

        assertThat(params.clampPageIndex(99)).isEqualTo(9); // last valid index
    }

    @Test
    void clampPageIndex_shouldReturnValidIndex() {
        var pagination = new javafx.scene.control.Pagination();
        pagination.setCurrentPageIndex(5);
        var params = PaginationSupport.params(pagination, 10, 100);

        assertThat(params.clampPageIndex(5)).isEqualTo(5);
    }

    @Test
    void fromIndex_shouldReturnZeroWhenNoItems() {
        var pagination = new javafx.scene.control.Pagination();
        var params = PaginationSupport.params(pagination, 10, 0);

        assertThat(params.fromIndex()).isEqualTo(0);
    }

    @Test
    void fromIndex_shouldCalculateCorrectly() {
        var pagination = new javafx.scene.control.Pagination();
        pagination.setCurrentPageIndex(3);
        var params = PaginationSupport.params(pagination, 10, 100);

        assertThat(params.fromIndex()).isEqualTo(30); // 3 * 10
    }

    @Test
    void toIndex_shouldReturnZeroWhenNoItems() {
        var pagination = new javafx.scene.control.Pagination();
        var params = PaginationSupport.params(pagination, 10, 0);

        assertThat(params.toIndex()).isEqualTo(0);
    }

    @Test
    void toIndex_shouldCalculateCorrectly() {
        var pagination = new javafx.scene.control.Pagination();
        pagination.setCurrentPageIndex(0);
        var params = PaginationSupport.params(pagination, 10, 100);

        assertThat(params.toIndex()).isEqualTo(10); // min(0 + 10, 100)
    }

    @Test
    void slice_shouldReturnEmptyListWhenNullInput() {
        var pagination = new javafx.scene.control.Pagination();
        var params = PaginationSupport.params(pagination, 10, 100);

        assertThat(params.slice(null)).isEmpty();
    }

    @Test
    void slice_shouldReturnEmptyListWhenEmptyInput() {
        var pagination = new javafx.scene.control.Pagination();
        var params = PaginationSupport.params(pagination, 10, 100);

        assertThat(params.slice(List.of())).isEmpty();
    }

    @Test
    void slice_shouldReturnCorrectSlice() {
        var pagination = new javafx.scene.control.Pagination();
        pagination.setCurrentPageIndex(1);
        var params = PaginationSupport.params(pagination, 5, 100);
        List<String> items = new ArrayList<>(List.of("a", "b", "c", "d", "e", "f", "g", "h", "i", "j"));

        List<String> result = params.slice(items);

        assertThat(result).containsExactly("f", "g", "h", "i", "j");
    }

    @Test
    void slice_shouldHandlePartialLastPage() {
        var pagination = new javafx.scene.control.Pagination();
        pagination.setCurrentPageIndex(2);
        var params = PaginationSupport.params(pagination, 5, 12);
        List<String> items = new ArrayList<>(List.of("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l"));

        List<String> result = params.slice(items);

        assertThat(result).containsExactly("k", "l");
    }

    @Test
    void slice_shouldClampToItemsSize() {
        var pagination = new javafx.scene.control.Pagination();
        pagination.setCurrentPageIndex(0);
        var params = PaginationSupport.params(pagination, 10, 5);
        List<String> items = new ArrayList<>(List.of("a", "b", "c"));

        List<String> result = params.slice(items);

        assertThat(result).containsExactly("a", "b", "c");
    }
}
