package io.geewit.core.utils.lang.enums;


/**
 * 枚举可以实现该接口
 * @author geewit
 */
@SuppressWarnings({"unused"})
public interface Name {
    default String getName() {
        return this.toString();
    }
}
