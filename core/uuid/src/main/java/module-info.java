/**
 * UUID 工具类
 */
module io.geewit.utils.core.uuid {
    requires transitive org.apache.commons.codec;
    requires transitive org.slf4j;
    requires org.jspecify;
    requires static lombok;

    exports io.geewit.utils.core.uuid;
}
