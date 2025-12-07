/**
 * UUID 工具类
 */
module io.geewit.utils.core.uuid {
    requires transitive org.apache.commons.codec;
    requires transitive org.slf4j;
    requires org.jspecify;
    exports io.geewit.utils.core.uuid;

    requires static lombok;
}
