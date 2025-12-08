package io.geewit.utils.i18n;

import org.springframework.context.i18n.LocaleContextHolder;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Utility methods for retrieving localized messages from the default message bundle.
 */
public final class I18nSupport {

    private static final String BASENAME = "i18n/messages";

    private I18nSupport() {
    }

    public static String message(String key, Object... args) {
        return message(null, key, args);
    }

    public static String message(ResourceBundle resources, String key, Object... args) {
        ResourceBundle bundle = resources != null ? resources : resolveBundle();
        String pattern;
        try {
            pattern = bundle.getString(key);
        } catch (MissingResourceException ex) {
            pattern = key;
        }
        if (args == null || args.length == 0) {
            return pattern;
        }
        return MessageFormat.format(pattern, args);
    }

    public static String defaultMessage(ResourceBundle resources, String key, String defaultValue) {
        if (resources != null && resources.containsKey(key)) {
            return resources.getString(key);
        }
        return defaultValue;
    }

    private static ResourceBundle resolveBundle() {
        Locale locale;
        try {
            locale = LocaleContextHolder.getLocale();
        } catch (Exception ignored) {
            locale = Locale.getDefault();
        }
        if (locale == null) {
            locale = Locale.getDefault();
        }
        return ResourceBundle.getBundle(BASENAME, locale);
    }
}


