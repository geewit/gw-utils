package io.geewit.core.utils;



import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;

/**
 时间转换工具类
 @author gelif
 @since  2015-5-18
 */
@SuppressWarnings({"unchecked", "unused"})
public class DateUtils {

    private static final String[] PATTERNS = {"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd", "HH:mm:ss"};

    public static Date date(String date) throws ParseException {
        return org.apache.commons.lang3.time.DateUtils.parseDateStrictly(date, PATTERNS);
    }

    /**
     * 获取最早时间
     * @param dates
     * @return
     */
    public static Date earliest(Date... dates) {
        if(dates == null || dates.length == 0) {
            return null;
        }
        Arrays.parallelSort(dates);
        return dates[0];
    }

    /**
     * 获取最晚时间
     * @param dates
     * @return
     */
    public static Date latest(Date... dates) {
        if(dates == null || dates.length == 0) {
            return null;
        }
        Arrays.parallelSort(dates);
        return dates[dates.length - 1];
    }
}













