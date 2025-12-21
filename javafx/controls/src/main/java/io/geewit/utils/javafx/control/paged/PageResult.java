package io.geewit.utils.javafx.control.paged;

import java.util.List;

public record PageResult<T>(List<T> records, long total) {

    public int totalPages(int pageSize) {
        if (pageSize <= 0) {
            return 0;
        }
        return (int) ((total + pageSize - 1) / pageSize);
    }
}
