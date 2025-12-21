module io.geewit.utils.javafx.control {
    requires java.desktop;
    requires java.sql;
    requires java.xml;
    requires jdk.incubator.vector;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.graphics;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires transitive org.slf4j;
    requires static lombok;
    exports io.geewit.utils.javafx.control;
    exports io.geewit.utils.javafx.control.paged;

    uses org.kordamp.ikonli.javafx.FontIcon;
}