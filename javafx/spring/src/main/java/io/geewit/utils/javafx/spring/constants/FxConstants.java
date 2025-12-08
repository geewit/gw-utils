package io.geewit.utils.javafx.spring.constants;

import javafx.util.converter.LocalDateStringConverter;
import org.springframework.core.io.ResourceLoader;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public interface FxConstants {

    String GLOBAL_CONFIG_PAGE_SIZE_PREFIX = "PAGE_SIZE.";

    List<Integer> PAGE_SIZES = List.of(10, 20, 30, 50, 100);

    String DATE_PATTERN = "yyyy-MM-dd";
    String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);
    LocalDateStringConverter DATE_CONVERTER =
            new LocalDateStringConverter(DATE_FORMATTER, DATE_FORMATTER);

    DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATETIME_PATTERN);

    DateTimeFormatter ZH_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy年MM月dd日");

    String FXML_EXTENSION = ".fxml";
    String FXML_RESOURCE_LOCATION = ResourceLoader.CLASSPATH_URL_PREFIX + "fxml/";

    String STYLE_CENTER = "-fx-alignment: CENTER;";

    String STYLE_CENTER_LEFT = "-fx-alignment: CENTER_LEFT;";

    AtomicReference<Map<String, Locale>> CANDIDATE_LOCALES = new AtomicReference<>(defaultMap());
    String WINDOW_SIZE_KEY = "window.primary.size";
    String WINDOW_POSITION_KEY = "window.primary.position";

    static Map<String, Locale> defaultMap() {
        Locale defaultLocale = Locale.getDefault();
        return Map.of(defaultLocale.getLanguage(), defaultLocale); // 不可变&线程安全发布
    }
}
