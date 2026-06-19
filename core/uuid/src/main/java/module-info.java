/**
 * UUID 工具类
 */
module io.geewit.utils.core.uuid {
    requires transitive org.apache.commons.codec;
    requires transitive org.slf4j;
    requires org.jspecify;
    requires static lombok;

    exports io.geewit.utils.core.uuid;
    exports io.geewit.utils.core.uuid.codec;
    exports io.geewit.utils.core.uuid.codec.base;
    exports io.geewit.utils.core.uuid.codec.base.function;
    exports io.geewit.utils.core.uuid.enums;
    exports io.geewit.utils.core.uuid.exception;
    exports io.geewit.utils.core.uuid.factory;
    exports io.geewit.utils.core.uuid.factory.function;
    exports io.geewit.utils.core.uuid.factory.function.impl;
    exports io.geewit.utils.core.uuid.factory.standard;
    exports io.geewit.utils.core.uuid.util;
    exports io.geewit.utils.core.uuid.util.immutable;
}
