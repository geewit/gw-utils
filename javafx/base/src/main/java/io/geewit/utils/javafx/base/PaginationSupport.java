package io.geewit.utils.javafx.base;

import javafx.scene.control.Pagination;

import java.util.List;
import java.util.Objects;

public final class PaginationSupport {
    private PaginationSupport() {
    }

    public static Params params(Pagination pagination,
                                int rowsPerPage,
                                int totalItems) {
        return new Params(pagination, rowsPerPage, totalItems);
    }

    public record Params(Pagination pagination,
                         int rowsPerPage,
                         int totalItems) {

        public Params {
            Objects.requireNonNull(pagination, "Pagination must not be null");
            if (rowsPerPage <= 0) {
                throw new IllegalArgumentException("rowsPerPage must be greater than zero");
            }
            if (totalItems < 0) {
                throw new IllegalArgumentException("totalItems must not be negative");
            }
        }

        public int pageCount() {
            if (totalItems == 0) {
                return 0;
            }
            return (int) Math.ceil((double) totalItems / rowsPerPage);
        }

        public void applyPageCount() {
            pagination.setPageCount(Math.max(pageCount(), 1));
            int lastIndex = Math.max(pagination.getPageCount() - 1, 0);
            if (pagination.getCurrentPageIndex() > lastIndex) {
                pagination.setCurrentPageIndex(lastIndex);
            }
        }

        public int clampPageIndex(int requestedIndex) {
            int lastIndex = Math.max(pageCount() - 1, 0);
            if (requestedIndex < 0) {
                return 0;
            }
            return Math.min(requestedIndex, lastIndex);
        }

        public int fromIndex() {
            int currentIndex = clampPageIndex(pagination.getCurrentPageIndex());
            if (totalItems == 0) {
                return 0;
            }
            int maxStart = Math.max(totalItems - 1, 0);
            return Math.min(currentIndex * rowsPerPage, maxStart);
        }

        public int toIndex() {
            if (totalItems == 0) {
                return 0;
            }
            return Math.min(fromIndex() + rowsPerPage, totalItems);
        }

        public <T> List<T> slice(List<T> items) {
            if (items == null || items.isEmpty()) {
                return List.of();
            }
            int from = Math.min(fromIndex(), items.size());
            int to = Math.min(toIndex(), items.size());
            if (from >= to) {
                return List.of();
            }
            return items.subList(from, to);
        }
    }
}
