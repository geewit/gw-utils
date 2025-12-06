/**
 * web converter
 * @author geewit
 */
module io.geewit.utils.web.core {
    requires transitive org.slf4j;
    requires spring.context;
    requires spring.beans;

    exports io.geewit.utils.web.core;

    opens io.geewit.utils.web.core to spring.core, spring.beans, spring.context;
}
