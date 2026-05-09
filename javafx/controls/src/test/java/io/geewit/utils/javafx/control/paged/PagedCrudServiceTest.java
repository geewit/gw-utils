package io.geewit.utils.javafx.control.paged;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static org.assertj.core.api.Assertions.assertThat;

public class PagedCrudServiceTest {

    @Test
    void deleteById_delegatesToDeleteByIds() {
        PagedCrudService<String, Integer, String> service = new PagedCrudService<>() {
            @Override
            public CompletionStage<PageResult<String>> query(String query, int pageIndex, int pageSize) {
                return CompletableFuture.completedFuture(new PageResult<>(List.of(), 0));
            }

            @Override
            public CompletionStage<String> create(String entity) {
                return CompletableFuture.completedFuture(entity);
            }

            @Override
            public CompletionStage<String> update(String entity) {
                return CompletableFuture.completedFuture(entity);
            }

            @Override
            public CompletionStage<Void> deleteByIds(java.util.Collection<Integer> ids) {
                assertThat(ids).containsExactly(42);
                return CompletableFuture.completedFuture(null);
            }
        };

        service.deleteById(42).toCompletableFuture().join();
    }

    @Test
    void interface_defaultMethod_deleteById() {
        // Test that the default implementation works correctly
        PagedCrudService<String, String, String> service = new PagedCrudService<>() {
            @Override
            public CompletionStage<PageResult<String>> query(String query, int pageIndex, int pageSize) {
                return CompletableFuture.completedFuture(new PageResult<>(List.of(), 0));
            }

            @Override
            public CompletionStage<String> create(String entity) {
                return CompletableFuture.completedFuture(entity);
            }

            @Override
            public CompletionStage<String> update(String entity) {
                return CompletableFuture.completedFuture(entity);
            }

            @Override
            public CompletionStage<Void> deleteByIds(java.util.Collection<String> ids) {
                return CompletableFuture.completedFuture(null);
            }
        };

        // Verify default deleteById is called
        var future = service.deleteById("test-id");
        assertThat(future).isNotNull();
    }
}