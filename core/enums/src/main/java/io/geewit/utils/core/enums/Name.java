package io.geewit.utils.core.enums;


/**
 * 枚举可以实现该接口
 * @author geewit
 */
@SuppressWarnings({"unused"})
public interface Name {
    /**
     * 获取枚举名称
     * @return 枚举名称
     */
    default String getName() {
        return this.toString();
    }
}
