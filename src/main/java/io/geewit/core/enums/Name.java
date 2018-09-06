package io.geewit.core.enums;


/**
 * @author geewit
 */
@SuppressWarnings({"unused"})
public interface Name {
    default String getName() {
        return this.toString();
    }
}
