module io.geewit.utils.javafx.spring {
    requires io.geewit.utils.core.exceptions;
    requires io.geewit.utils.i18n;
    requires io.geewit.utils.javafx.base;
    requires java.desktop;
    requires java.sql;
    requires java.xml;
    requires jdk.incubator.vector;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires org.jspecify;
    requires spring.beans;
    requires spring.core;
    requires spring.context;
    requires spring.boot.autoconfigure;
    requires transitive org.slf4j;
    requires static lombok;
    exports io.geewit.utils.javafx.spring;
    exports io.geewit.utils.javafx.spring.weaver;
}