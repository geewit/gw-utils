package io.geewit.utils.javafx.control.paged;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class PageResultTest {

    @Test
    void totalPages_withZeroPageSize_returnsZero() {
        PageResult<String> result = new PageResult<>(List.of("a", "b", "c"), 10);
        assertThat(result.totalPages(0)).isZero();
    }

    @Test
    void totalPages_withNegativePageSize_returnsZero() {
        PageResult<String> result = new PageResult<>(List.of("a", "b", "c"), 10);
        assertThat(result.totalPages(-1)).isZero();
    }

    @Test
    void totalPages_withExactDivision_returnsCorrectCount() {
        PageResult<String> result = new PageResult<>(List.of("a", "b", "c"), 10);
        assertThat(result.totalPages(5)).isEqualTo(2);
    }

    @Test
    void totalPages_withRemainder_roundsUp() {
        PageResult<String> result = new PageResult<>(List.of("a", "b", "c"), 10);
        assertThat(result.totalPages(3)).isEqualTo(4);
    }

    @Test
    void totalPages_withZeroTotal_returnsZero() {
        PageResult<String> result = new PageResult<>(List.of(), 0);
        assertThat(result.totalPages(10)).isZero();
    }

    @Test
    void totalPages_withPageSizeLargerThanTotal_returnsOne() {
        PageResult<String> result = new PageResult<>(List.of("a", "b", "c"), 3);
        assertThat(result.totalPages(10)).isEqualTo(1);
    }

    @Test
    void pageResult_record_accessors() {
        List<String> records = List.of("a", "b", "c");
        PageResult<String> result = new PageResult<>(records, 10);
        
        assertThat(result.records()).isEqualTo(records);
        assertThat(result.total()).isEqualTo(10);
    }
}