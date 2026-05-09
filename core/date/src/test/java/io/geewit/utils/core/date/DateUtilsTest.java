package io.geewit.utils.core.date;

import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class DateUtilsTest {

    @Test
    void date_validString_parsesCorrectly() throws ParseException {
        Date date = DateUtils.date("2023-05-18 10:30:00");
        assertNotNull(date);
    }

    @Test
    void date_dateOnly_parsesCorrectly() throws ParseException {
        Date date = DateUtils.date("2023-05-18");
        assertNotNull(date);
    }

    @Test
    void date_timeOnly_parsesCorrectly() throws ParseException {
        Date date = DateUtils.date("10:30:00");
        assertNotNull(date);
    }

    @Test
    void date_invalidString_throwsParseException() {
        assertThrows(ParseException.class, () -> DateUtils.date("invalid"));
    }

    @Test
    void earliest_multipleDates_returnsEarliest() throws ParseException {
        Date d1 = DateUtils.date("2023-01-01 00:00:00");
        Date d2 = DateUtils.date("2023-06-01 00:00:00");
        Date d3 = DateUtils.date("2023-03-01 00:00:00");
        assertEquals(d1, DateUtils.earliest(d1, d2, d3));
    }

    @Test
    void earliest_nullArray_returnsNull() {
        assertNull(DateUtils.earliest((Date[]) null));
    }

    @Test
    void earliest_emptyArray_returnsNull() {
        assertNull(DateUtils.earliest(new Date[0]));
    }

    @Test
    void latest_multipleDates_returnsLatest() throws ParseException {
        Date d1 = DateUtils.date("2023-01-01 00:00:00");
        Date d2 = DateUtils.date("2023-06-01 00:00:00");
        Date d3 = DateUtils.date("2023-03-01 00:00:00");
        assertEquals(d2, DateUtils.latest(d1, d2, d3));
    }

    @Test
    void latest_nullArray_returnsNull() {
        assertNull(DateUtils.latest((Date[]) null));
    }

    @Test
    void latest_emptyArray_returnsNull() {
        assertNull(DateUtils.latest(new Date[0]));
    }
}
