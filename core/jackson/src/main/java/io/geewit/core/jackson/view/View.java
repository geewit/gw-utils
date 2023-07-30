package io.geewit.core.jackson.view;

/**
 * @see com.fasterxml.jackson.annotation.JsonView
 * @author geewit
 */
@SuppressWarnings({"unused"})
public interface View {
    interface Page extends View {}

    interface List extends View {}

    interface Tree extends View {}

    interface Info extends List, Page, Tree {}

    interface Public extends View {}

    interface Internal extends Public {}
}
