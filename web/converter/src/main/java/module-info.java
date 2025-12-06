/**
 * web converter
 */
module io.geewit.utils.web.converter {
    requires transitive io.geewit.utils.core.enums;
    requires transitive org.apache.commons.lang3;
    requires transitive org.slf4j;
    requires spring.core;
    requires spring.context;

    exports io.geewit.utils.web.converter;
}
