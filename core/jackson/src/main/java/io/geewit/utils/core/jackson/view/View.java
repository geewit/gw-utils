package io.geewit.utils.core.jackson.view;

/**
 * 默认视图接口
 * @see com.fasterxml.jackson.annotation.JsonView
 * @author geewit
 */
@SuppressWarnings({"unused"})
public interface View {
    /**
     * 默认视图
     */
    interface Page extends View {}

    /**
     * 默认视图
     */
    interface List extends View {}

    /**
     * 默认视图
     */
    interface Tree extends View {}

    /**
     * 默认视图
     */
    interface Info extends List, Page, Tree {}

    /**
     * 默认视图
     */
    interface Public extends View {}

    /**
     * 默认视图
     */
    interface Internal extends Public {}
}
