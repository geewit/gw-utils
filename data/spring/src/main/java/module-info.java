/**
 * spring data 数据工具类
 */
module io.geewit.utils.data.spring {
    requires transitive org.slf4j;
    requires spring.data.commons;
    requires static org.jspecify;

    exports io.geewit.utils.data.spring;
}
