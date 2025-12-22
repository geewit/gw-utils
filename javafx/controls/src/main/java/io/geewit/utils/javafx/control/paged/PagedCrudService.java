package io.geewit.utils.javafx.control.paged;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletionStage;

public interface PagedCrudService<T, K, Q> {

    CompletionStage<PageResult<T>> query(Q query, int pageIndex, int pageSize);

    CompletionStage<T> create(T entity);

    CompletionStage<T> update(T entity);

    // ✅ 批量删除（唯一删除入口）
    CompletionStage<Void> deleteByIds(Collection<K> ids);

    // ✅ 兼容：可选保留，方便单行删除复用批量接口
    default CompletionStage<Void> deleteById(K id) {
        return this.deleteByIds(List.of(id));
    }
}
