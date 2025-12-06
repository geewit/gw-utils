/**
 * 反射工具类
 */
module io.geewit.utils.core.reflection {
    requires transitive org.apache.commons.lang3;
    requires transitive org.slf4j;
    requires spring.beans;
    requires spring.core;

    exports io.geewit.utils.core.reflection;
}
