package io.geewit.data.commons.utils;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public class ListBatchPageUtils {
    /**
     * 该函数实现对列表的分页处理，每页大小由 pageSize 指定，通过 subList 截取子列表并传给 consumer 执行。使用 IntStream 生成起始索引，确保遍历完整列表且不重复。
     * 时间复杂度：O(n)，主要开销为一次 List 拷贝和遍历。
     * 空间复杂度：O(n)，subList 不会复制数据。
     * @param list 列表
     * @param pageSize 每次从collection截取的子collection的数量
     * @param consumer 列表的消费者
     * @param <T> 列表元素类型
     */
    public static <T> void pageLoadAndBatchConsumer(List<T> list,
                                                   int pageSize,
                                                   Consumer<List<T>> consumer) {

        Objects.requireNonNull(list, "list must not be null");
        Objects.requireNonNull(consumer,   "consumer must not be null");
        if (pageSize <= 0) {
            throw new IllegalArgumentException("pageSize must be greater than 0");
        }

        int total = list.size();
        // 使用 IntStream 生成每页的起始索引：0, pageSize, 2*pageSize, ...
        IntStream.iterate(0, i -> i + pageSize)
                .limit((total + pageSize - 1) / pageSize)  // 计算总页数 = ceil(total / pageSize)
                .mapToObj(start -> {
                    int end = Math.min(total, start + pageSize);
                    return list.subList(start, end);
                })
                .forEach(consumer);
    }
}
