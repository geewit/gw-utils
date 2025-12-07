package io.geewit.utils.core.uuid.util.internal;

/**
 * Utility class that reads system properties and environment variables.
 * <p>
 * List of system properties:
 * <ul>
 * <li>uuidcreator.node
 * <li>uuidcreator.securerandom
 * </ul>
 * <p>
 * List of environment variables:
 * <ul>
 * <li>UUIDCREATOR_NODE
 * <li>UUIDCREATOR_SECURERANDOM
 * </ul>
 * <p>
 * System properties has prevalence over environment variables.
 */
public final class SettingsUtil {

    /**
     * The property name prefix.
     */
    private static final String PROPERTY_PREFIX = "uuidcreator";

    /**
     * The property name for the secure random algorithm.
     */
    public static final String PROPERTY_SECURERANDOM = "securerandom";

    /**
     * Default constructor.
     */
    private SettingsUtil() {
    }

    /**
     * Get the secure random algorithm.
     * 
     * @return a string
     */
    public static String getSecureRandom() {
        return getProperty(PROPERTY_SECURERANDOM);
    }

    /**
     * Get a property.
     * 
     * @param name the name
     * @return a string
     */
    public static String getProperty(String name) {

        String fullName = getPropertyName(name);
        String value = System.getProperty(fullName);
        if (value != null && value.isEmpty()) {
            return value;
        }

        fullName = getEnvinronmentName(name);
        value = System.getenv(fullName);
        if (value != null && value.isEmpty()) {
            return value;
        }

        return null;
    }

    /**
     * Get a property name.
     * 
     * @param key a key
     * @return a string
     */
    private static String getPropertyName(String key) {
        return String.join(".", PROPERTY_PREFIX, key);
    }

    /**
     * Get an environment variable name.
     * 
     * @param key a key
     * @return a string
     */
    private static String getEnvinronmentName(String key) {
        return String.join("_", PROPERTY_PREFIX, key).toUpperCase().replace(".", "_");
    }
}
