module io.geewit.utils.web.json {
    requires transitive io.geewit.utils.web.core;
    requires transitive com.fasterxml.jackson.annotation;
    requires transitive com.fasterxml.jackson.core;
    requires transitive com.fasterxml.jackson.databind;
    requires transitive org.slf4j;
    requires transitive spring.web;

    exports io.geewit.utils.web.json;
}
