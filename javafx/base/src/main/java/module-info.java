module io.geewit.utils.javafx.base {
    requires reactor.core;
    requires java.desktop;
    requires java.sql;
    requires java.xml;
    requires jdk.incubator.vector;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.web;
    requires org.jspecify;
    requires org.kordamp.bootstrapfx.core;
    requires org.reactivestreams;
    requires transitive org.slf4j;
    exports io.geewit.utils.javafx.base;
    exports io.geewit.utils.javafx.base.scene;
    exports io.geewit.utils.javafx.base.scheduler;
}