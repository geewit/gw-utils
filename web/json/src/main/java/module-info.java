/**
 * JSON support utilities for web layer.
 * 提供 Web 层使用的 JSON 工具与配置。
 */
module io.geewit.utils.web.json {
    requires transitive io.geewit.utils.web.core;
    requires transitive com.fasterxml.jackson.annotation;
    requires transitive tools.jackson.core;
    requires transitive tools.jackson.databind;
    requires transitive org.slf4j;
    requires spring.web;

    exports io.geewit.utils.web.json;
}
