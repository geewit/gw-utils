package io.geewit.utils.javafx.spring.constants;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("FxConstants Tests")
class FxConstantsTest {

    @Nested
    @DisplayName("constant values")
    class ConstantValues {

        @Test
        @DisplayName("should have correct PAGE_SIZES")
        void shouldHaveCorrectPageSizes() {
            assertThat(FxConstants.PAGE_SIZES).containsExactly(10, 20, 30, 50, 100);
        }

        @Test
        @DisplayName("should have correct GLOBAL_CONFIG_PAGE_SIZE_PREFIX")
        void shouldHaveCorrectGlobalConfigPageSizePrefix() {
            assertThat(FxConstants.GLOBAL_CONFIG_PAGE_SIZE_PREFIX).isEqualTo("PAGE_SIZE.");
        }

        @Test
        @DisplayName("should have correct DATE_PATTERN")
        void shouldHaveCorrectDatePattern() {
            assertThat(FxConstants.DATE_PATTERN).isEqualTo("yyyy-MM-dd");
        }

        @Test
        @DisplayName("should have correct DATETIME_PATTERN")
        void shouldHaveCorrectDatetimePattern() {
            assertThat(FxConstants.DATETIME_PATTERN).isEqualTo("yyyy-MM-dd HH:mm:ss");
        }

        @Test
        @DisplayName("should have correct DATE_FORMATTER")
        void shouldHaveCorrectDateFormatter() {
            assertThat(FxConstants.DATE_FORMATTER.toString()).isEqualTo(DateTimeFormatter.ofPattern("yyyy-MM-dd").toString());
        }

        @Test
        @DisplayName("should have correct DATE_TIME_FORMATTER")
        void shouldHaveCorrectDateTimeFormatter() {
            assertThat(FxConstants.DATE_TIME_FORMATTER.toString()).isEqualTo(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").toString());
        }

        @Test
        @DisplayName("should have correct ZH_DATE_TIME_FORMATTER")
        void shouldHaveCorrectZhDateTimeFormatter() {
            assertThat(FxConstants.ZH_DATE_TIME_FORMATTER.toString()).isEqualTo(DateTimeFormatter.ofPattern("yyyy年MM月dd日").toString());
        }

        @Test
        @DisplayName("should have correct FXML_EXTENSION")
        void shouldHaveCorrectFxmlExtension() {
            assertThat(FxConstants.FXML_EXTENSION).isEqualTo(".fxml");
        }

        @Test
        @DisplayName("should have correct STYLE_CENTER")
        void shouldHaveCorrectStyleCenter() {
            assertThat(FxConstants.STYLE_CENTER).isEqualTo("-fx-alignment: CENTER;");
        }

        @Test
        @DisplayName("should have correct STYLE_CENTER_LEFT")
        void shouldHaveCorrectStyleCenterLeft() {
            assertThat(FxConstants.STYLE_CENTER_LEFT).isEqualTo("-fx-alignment: CENTER_LEFT;");
        }

        @Test
        @DisplayName("should have correct WINDOW_SIZE_KEY")
        void shouldHaveCorrectWindowSizeKey() {
            assertThat(FxConstants.WINDOW_SIZE_KEY).isEqualTo("window.primary.size");
        }

        @Test
        @DisplayName("should have correct WINDOW_POSITION_KEY")
        void shouldHaveCorrectWindowPositionKey() {
            assertThat(FxConstants.WINDOW_POSITION_KEY).isEqualTo("window.primary.position");
        }
    }

    @Nested
    @DisplayName("CANDIDATE_LOCALES")
    class CandidateLocalesTests {

        @Test
        @DisplayName("should not be null")
        void shouldNotBeNull() {
            assertThat(FxConstants.CANDIDATE_LOCALES).isNotNull();
        }

        @Test
        @DisplayName("should contain default locale")
        void shouldContainDefaultLocale() {
            Locale defaultLocale = Locale.getDefault();
            Map<String, Locale> map = FxConstants.CANDIDATE_LOCALES.get();
            assertThat(map.values()).contains(defaultLocale);
        }
    }

    @Nested
    @DisplayName("defaultMap()")
    class DefaultMapMethod {

        @Test
        @DisplayName("should return map with default language")
        void shouldReturnMapWithDefaultLanguage() {
            Map<String, Locale> map = FxConstants.defaultMap();
            assertThat(map).isNotEmpty();
        }

        @Test
        @DisplayName("should use current default locale")
        void shouldUseCurrentDefaultLocale() {
            Map<String, Locale> map = FxConstants.defaultMap();
            Locale defaultLocale = Locale.getDefault();
            assertThat(map).containsKey(defaultLocale.getLanguage());
        }

        @Test
        @DisplayName("should return immutable map")
        void shouldReturnImmutableMap() {
            Map<String, Locale> map = FxConstants.defaultMap();
            assertThat(map).isUnmodifiable();
        }
    }
}