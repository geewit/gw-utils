module io.geewit.utils.core.jackson {
    requires transitive io.geewit.utils.core.enums;
    requires transitive com.fasterxml.jackson.annotation;
    requires transitive com.fasterxml.jackson.core;
    requires transitive com.fasterxml.jackson.databind;
    requires transitive org.slf4j;
    requires transitive spring.boot;
    requires transitive spring.boot.autoconfigure;
    requires transitive spring.context;
    requires transitive spring.core;
    requires transitive spring.beans;
    requires transitive spring.data.commons;

    exports io.geewit.utils.core.jackson.config;
    exports io.geewit.utils.core.jackson.databind.deserializer;
    exports io.geewit.utils.core.jackson.databind.serializer;
    exports io.geewit.utils.core.jackson.view;

    opens io.geewit.utils.core.jackson.config to spring.core, spring.beans, spring.context, spring.boot.autoconfigure;
    opens io.geewit.utils.core.jackson.databind.deserializer to spring.core, spring.beans, spring.context;
    opens io.geewit.utils.core.jackson.databind.serializer to spring.core, spring.beans, spring.context;
}
