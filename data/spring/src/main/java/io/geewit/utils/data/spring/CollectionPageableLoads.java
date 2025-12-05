package io.geewit.utils.data.spring;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.io.Serializable;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 分页加载工具类.
 *
 * @author geewit
 */
public class CollectionPageableLoads {
    private static final Logger log = LoggerFactory.getLogger(CollectionPageableLoads.class);

    private CollectionPageableLoads() {
    }

    /**
     * 分页加载数据并转换为列表
     *
     * @param pageSize 每页大小
     * @param loadFunction 分页加载函数接口，用于获取分页数据
     * @param <T> 实体类型
     * @return 包含所有分页数据的列表
     */
    public static <T> List<T> pageLoadToList(int pageSize,
                                             PageLoadFunction<T> loadFunction) {
        Pageable pageable = Pageable.ofSize(pageSize);
        List<T> result = new ArrayList<>();
        Page<@NonNull T> page;

        // 循环加载所有分页数据直到没有下一页
        do {
            page = loadFunction.page(pageable);
            if (page != null) {
                // 添加当前页的数据到结果列表中
                if (page.hasContent()) {
                    result.addAll(page.getContent());
                }
                // 如果还有下一页，则更新pageable为下一页的pageable
                if (page.hasNext()) {
                    pageable = page.nextPageable();
                }
            }
        } while (page != null && page.hasNext());
        return result;
    }

    /**
     * 分页加载.
     *
     * @param ids                id集合
     * @param parametersSize     分页大小
     * @param loadFunction       加载函数
     * @param entityToIdFunction 实体转换为id函数
     * @param <T>                实体类型
     * @param <ID>               实体主键类型
     * @return 分页加载结果
     */
    public static <T, ID extends Serializable> Map<ID, T> pageableLoadToMap(Collection<ID> ids,
                                                                            int parametersSize,
                                                                            LoadFunction<T, ID> loadFunction,
                                                                            EntityToIdFunction<T, ID> entityToIdFunction) {
        if (ids == null || ids.isEmpty()) {
            return new HashMap<>();
        }
        int total = ids.size();
        Map<ID, T> objectMap = new HashMap<>(ids.size());
        Pageable pageable = Pageable.ofSize(Math.min(parametersSize, total));
        Page<@NonNull ID> page;
        do {
            // 获取当前页需要处理的ID列表
            List<ID> content = ids.stream()
                    .skip(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .collect(Collectors.toList());
            page = new PageImpl<>(content, pageable, total);
            // 调用加载函数获取实体列表
            List<T> entities = loadFunction.list(content);
            if (entities != null && !entities.isEmpty()) {
                // 将实体按照ID映射关系存入结果Map
                entities.forEach(entity -> objectMap.put(entityToIdFunction.entityId(entity), entity));
            }
            // 判断是否还有下一页，如果有则更新分页参数
            if (page.hasNext()) {
                pageable = page.nextPageable();
            }
        } while (page.hasNext());
        return objectMap;
    }


    /**
     * 分页加载.
     *
     * @param parameters            查询参数集合
     * @param parametersSize        分页大小
     * @param loadFunction          加载函数
     * @param entityToKeyFunction   实体转换为id函数
     * @param entityToValueFunction 实体转换为值函数
     * @param <T>                   实体类型
     * @param <ID>                  实体主键类型
     * @param <V>                   值类型
     * @return 分页加载结果
     */
    public static <T, ID extends Serializable, V> Map<ID, Set<V>> pageableLoadToSetMap(Collection<ID> parameters,
                                                                                       int parametersSize,
                                                                                       LoadFunction<T, ID> loadFunction,
                                                                                       EntityToKeyFunction<T, ID> entityToKeyFunction,
                                                                                       EntityToValueFunction<T, V> entityToValueFunction) {
        if (parameters == null || parameters.isEmpty()) {
            return new HashMap<>();
        }
        int total = parameters.size();
        Map<ID, Set<V>> objectMap = new HashMap<>(parameters.size());
        Pageable pageable = Pageable.ofSize(Math.min(parametersSize, total));
        Page<@NonNull ID> page;
        do {
            List<ID> content = parameters.stream()
                    .skip(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .collect(Collectors.toList());
            page = new PageImpl<>(content, pageable, total);
            List<T> entities = loadFunction.list(content);
            if (entities != null && !entities.isEmpty()) {
                entities.forEach(entity -> {
                    if (objectMap.get(entityToKeyFunction.mapKey(entity)) == null) {
                        objectMap.put(entityToKeyFunction.mapKey(entity), Stream.of(entityToValueFunction.mapValue(entity)).collect(Collectors.toSet()));
                    } else {
                        objectMap.get(entityToKeyFunction.mapKey(entity)).add(entityToValueFunction.mapValue(entity));
                    }
                });
            }
            if (page.hasNext()) {
                pageable = page.nextPageable();
            }
        } while (page.hasNext());
        return objectMap;
    }

    /**
     * 分页加载.
     *
     * @param parameters     查询参数集合
     * @param parametersSize 分页大小
     * @param loadFunction   加载函数
     * @param <T>                   实体类型
     * @param <ID>                  实体主键类型
     * @return 分页加载结果
     */
    public static <T, ID extends Serializable> List<T> pageableLoadToList(Collection<ID> parameters,
                                                                          int parametersSize,
                                                                          LoadFunction<T, ID> loadFunction) {
        if (parameters == null || parameters.isEmpty()) {
            return new ArrayList<>();
        }
        int total = parameters.size();
        List<T> objectList = new ArrayList<>(parameters.size());
        Pageable pageable = Pageable.ofSize(Math.min(parametersSize, total));
        Page<@NonNull ID> page;
        do {
            List<ID> content = parameters.stream()
                    .skip(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .collect(Collectors.toList());
            page = new PageImpl<>(content, pageable, total);
            List<T> entities = loadFunction.list(content);
            if (entities != null && !entities.isEmpty()) {
                objectList.addAll(entities);
            }

            if (page.hasNext()) {
                pageable = page.nextPageable();
            }
        } while (page.hasNext());
        return objectList;
    }

    /**
     * 分页加载.
     *
     * @param parameters     查询参数集合
     * @param parametersSize 分页大小
     * @param loadFunction   加载函数
     * @param <T>            实体类型
     * @param <ID>           实体主键类型
     * @return 分页加载结果
     */
    public static <T, ID extends Serializable> Set<T> pageableLoadToSet(Collection<ID> parameters,
                                                                        int parametersSize,
                                                                        LoadFunction<T, ID> loadFunction) {
        if (parameters == null || parameters.isEmpty()) {
            return new HashSet<>();
        }
        int total = parameters.size();
        Set<T> objectList = new HashSet<>(parameters.size());
        Pageable pageable = Pageable.ofSize(Math.min(parametersSize, total));
        Page<@NonNull ID> page;
        do {
            List<ID> content = parameters.stream()
                    .skip(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .collect(Collectors.toList());
            page = new PageImpl<>(content, pageable, total);
            List<T> entities = loadFunction.list(content);
            if (entities != null && !entities.isEmpty()) {
                objectList.addAll(entities);
            }
            if (page.hasNext()) {
                pageable = page.nextPageable();
            }
        } while (page.hasNext());
        return objectList;
    }

    /**
     * 分页加载数据并消费处理
     * 该方法通过分页方式循环加载数据，对每页数据进行消费处理，直到所有数据处理完毕
     *
     * @param pageSize 每页数据大小
     * @param loadFunction 分页加载函数，用于获取指定页的数据
     * @param consumer 数据消费处理器，用于处理每条数据
     * @param <T> 数据类型泛型
     */
    public static <T> void pageLoadAndConsumer(int pageSize,
                                               PageLoadFunction<T> loadFunction,
                                               Consumer<T> consumer) {
        Pageable pageable = Pageable.ofSize(pageSize);
        Page<@NonNull T> page;
        // 循环分页加载数据，直到没有下一页为止
        do {
            page = loadFunction.page(pageable);
            if (page != null) {
                // 处理当前页的数据
                if (page.hasContent()) {
                    long start = System.currentTimeMillis();
                    for (T entity : page.getContent()) {
                        consumer.accept(entity);
                    }
                    log.info("pageLoadAndConsumer page: {}, cost:{}", page.getNumber(), (System.currentTimeMillis() - start));
                }
                // 准备加载下一页
                if (page.hasNext()) {
                    pageable = page.nextPageable();
                }
            }
        } while (page != null && page.hasNext());
    }


    /**
     * 分页加载数据并消费处理
     * <p>
     * 该方法通过分页方式循环加载数据，每次加载一页数据后立即使用指定的消费者进行处理，
     * 直到所有数据加载完毕。适用于大数据量处理场景，避免一次性加载所有数据导致内存溢出。
     *
     * @param pageSize 每页数据大小
     * @param loadFunction 分页加载函数，负责具体的数据加载逻辑
     * @param consumer 数据消费者，负责处理每页加载的数据
     * @param <T> 数据类型泛型
     */
    public static <T> void pageLoadAndListConsumer(int pageSize,
                                                   PageLoadFunction<T> loadFunction,
                                                   Consumer<List<T>> consumer) {
        Pageable pageable = Pageable.ofSize(pageSize);
        Page<@NonNull T> page;
        // 循环分页加载数据，直到没有下一页为止
        do {
            page = loadFunction.page(pageable);
            if (page != null) {
                // 处理当前页数据
                if (page.hasContent()) {
                    long start = System.currentTimeMillis();
                    consumer.accept(page.getContent());
                    log.info("pageLoadAndConsumer page: {}, cost:{}", page.getNumber(), (System.currentTimeMillis() - start));
                }
                // 准备加载下一页
                if (page.hasNext()) {
                    pageable = page.nextPageable();
                }
            }
        } while (page != null && page.hasNext());
    }

    /**
     * 分页加载.
     * @param <T> 实体类型
     */
    @FunctionalInterface
    public interface PageLoadFunction<T> {
        /**
         * 分页加载数据.
         * @param pageable 分页参数
         * @return 加载结果
         */
        Page<@NonNull T> page(Pageable pageable);
    }

    /**
     * 加载数据.
     * @param <T> 实体类型
     * @param <ID> 实体主键类型
     */
    @FunctionalInterface
    public interface LoadFunction<T, ID extends Serializable> {
        /**
         * 加载数据.
         *
         * @param ids 查询参数集合
         * @return 加载结果
         */
        List<T> list(Collection<ID> ids);
    }

    /**
     * 获取实体主键.
     * @param <T> 实体类型
     * @param <ID> 实体主键类型
     */
    @FunctionalInterface
    public interface EntityToIdFunction<T, ID extends Serializable> {
        /**
         * 获取实体主键.
         * @param entity 实体
         * @return 实体主键
         */
        ID entityId(T entity);
    }

    /**
     * 获取实体映射的key.
     * @param <T> 实体类型
     * @param <ID> 实体主键类型
     */
    @FunctionalInterface
    public interface EntityToKeyFunction<T, ID extends Serializable> {
        /**
         * 获取实体映射的key.
         * @param entity 实体
         * @return 映射的key
         */
        ID mapKey(T entity);
    }

    /**
     * 获取实体映射的value.
     * @param <T> 实体类型
     * @param <V> 映射的value类型
     */
    @FunctionalInterface
    public interface EntityToValueFunction<T, V> {
        /**
         * 获取实体映射的value.
         * @param entity 实体
         * @return 映射的value
         */
        V mapValue(T entity);
    }
}
