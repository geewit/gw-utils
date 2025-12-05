package io.geewit.utils.core.enums;

/**
 * 枚举可以实现该接口
 *
 * @author geewit
 * @param <V> 数字类型
 */
@SuppressWarnings({"unused"})
public interface Value<V extends Number> {
    /**
     * 获取数字
     * @return 数字
     */
    V value();
}
