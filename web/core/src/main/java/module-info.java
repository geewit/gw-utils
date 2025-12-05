module io.geewit.utils.web.core {
    requires transitive org.slf4j;
    requires transitive spring.context;
    requires transitive spring.beans;

    exports io.geewit.utils.web.core;

    opens io.geewit.utils.web.core to spring.core, spring.beans, spring.context;
}
