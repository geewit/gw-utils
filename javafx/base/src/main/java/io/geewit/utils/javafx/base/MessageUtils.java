package io.geewit.utils.javafx.base;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class MessageUtils {
    public static String getMessage(ResourceBundle resources,
                                    String key,
                                    String fallback,
                                    Object... args) {
        String pattern = fallback;
        if (resources != null && resources.containsKey(key)) {
            pattern = resources.getString(key);
        }
        if (pattern == null) {
            pattern = "";
        }
        if (args == null || args.length == 0) {
            return pattern;
        }
        return MessageFormat.format(pattern, args);
    }
}
