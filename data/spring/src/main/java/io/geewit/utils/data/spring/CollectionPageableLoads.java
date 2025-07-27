package io.geewit.utils.data.spring;

import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class CollectionPageableLoads {

    public static <T> List<T> pageLoadToList(int pageSize,
                                             PageLoadFunction<T> loadFunction) {
        Pageable pageable = Pageable.ofSize(pageSize);
        List<T> result = new ArrayList<>();
        Page<T> page;
        do {
            page = loadFunction.page(pageable);
            if (page != null) {
                if (page.hasContent()) {
                    result.addAll(page.getContent());
                }
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
        Page<ID> page;
        do {
            List<ID> content = ids.stream()
                    .skip(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .collect(Collectors.toList());
            page = new PageImpl<>(content, pageable, total);
            List<T> entities = loadFunction.list(content);
            if (entities != null && !entities.isEmpty()) {
                entities.forEach(entity -> objectMap.put(entityToIdFunction.entityId(entity), entity));
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
     * @param parameters          查询参数集合
     * @param parametersSize      分页大小
     * @param loadFunction        加载函数
     * @param entityToKeyFunction 实体转换为id函数
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
        Page<ID> page;
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
        Page<ID> page;
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
        Page<ID> page;
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


    public static <T> void pageLoadAndConsumer(int pageSize,
                                               PageLoadFunction<T> loadFunction,
                                               Consumer<T> consumer) {
        Pageable pageable = Pageable.ofSize(pageSize);
        Page<T> page;
        do {
            page = loadFunction.page(pageable);
            if (page != null) {
                if (page.hasContent()) {
                    long start = System.currentTimeMillis();
                    for (T entity : page.getContent()) {
                        consumer.accept(entity);
                    }
                    log.info("pageLoadAndConsumer page: {}, cost:{}", page.getNumber(), (System.currentTimeMillis() - start));
                }
                if (page.hasNext()) {
                    pageable = page.nextPageable();
                }
            }
        } while (page != null && page.hasNext());
    }

    public static <T> void pageLoadAndListConsumer(int pageSize,
                                                   PageLoadFunction<T> loadFunction,
                                                   Consumer<List<T>> consumer) {
        Pageable pageable = Pageable.ofSize(pageSize);
        Page<T> page;
        do {
            page = loadFunction.page(pageable);
            if (page != null) {
                if (page.hasContent()) {
                    long start = System.currentTimeMillis();
                    consumer.accept(page.getContent());
                    log.info("pageLoadAndConsumer page: {}, cost:{}", page.getNumber(), (System.currentTimeMillis() - start));
                }
                if (page.hasNext()) {
                    pageable = page.nextPageable();
                }
            }
        } while (page != null && page.hasNext());
    }

    @FunctionalInterface
    public interface PageLoadFunction<T> {
        Page<T> page(Pageable pageable);
    }

    @FunctionalInterface
    public interface LoadFunction<T, ID extends Serializable> {
        List<T> list(Collection<ID> ids);
    }

    @FunctionalInterface
    public interface EntityToIdFunction<T, ID extends Serializable> {
        ID entityId(T entity);
    }

    @FunctionalInterface
    public interface EntityToKeyFunction<T, ID extends Serializable> {
        ID mapKey(T entity);
    }

    @FunctionalInterface
    public interface EntityToValueFunction<T, V> {
        V mapValue(T entity);
    }
}
