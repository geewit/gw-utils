package io.geewit.utils.i18n;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public interface I18nConstants {
    static Map<String, Locale> defaultMap() {
        Locale defaultLocale = Locale.getDefault();
        return Map.of(defaultLocale.getLanguage(), defaultLocale); // 不可变&线程安全发布
    }

    AtomicReference<Map<String, Locale>> CANDIDATE_LOCALES = new AtomicReference<>(defaultMap());
}
