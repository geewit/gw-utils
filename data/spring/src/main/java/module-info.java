module io.geewit.utils.data.spring {
    requires transitive org.slf4j;
    requires transitive spring.data.commons;
    requires static org.jspecify;

    exports io.geewit.utils.data.spring;
}
