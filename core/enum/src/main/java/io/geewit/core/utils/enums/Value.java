package io.geewit.core.utils.enums;

/**
 * 枚举可以实现该接口
 * @author geewit
 */
@SuppressWarnings({"unused"})
public interface Value<V extends Number> {
    /**
     * @return 数字
     */
    V value();
}
