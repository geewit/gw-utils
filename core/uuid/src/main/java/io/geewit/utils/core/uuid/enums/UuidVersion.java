package io.geewit.utils.core.uuid.enums;

import lombok.Getter;

/**
 * UUID versions defined by RFC 9562.
 * <p>
 * List of versions:
 * <ul>
 * <li>{@link UuidVersion#VERSION_UNKNOWN}: 0
 * <li>{@link UuidVersion#VERSION_TIME_ORDERED_EPOCH}: 7
 * </ul>
 * 
 * @see <a href="https://www.rfc-editor.org/rfc/rfc9562.html">RFC 9562</a>
 */
@Getter
public enum UuidVersion {

    /**
     * An unknown version.
     */
    VERSION_UNKNOWN(0),
    /**
     * The time-ordered version with Unix epoch proposed by Peabody and Davis.
     */
    VERSION_TIME_ORDERED_EPOCH(7);

    private final int value;

    UuidVersion(int value) {
        this.value = value;
    }

}
