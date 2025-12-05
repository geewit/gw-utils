package io.geewit.utils.core.date;


import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;

/**
 时间转换工具类
 @author geewit
 @since  2015-05-18
 */
@SuppressWarnings({"unused"})
public class DateUtils {
    private DateUtils() {
    }

    /**
     * 日期格式
     */
    private static final String[] PATTERNS = {"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd", "HH:mm:ss"};

    /**
     * 字符串转换为日期
     * @param date 日期字符串
     * @return 日期
     */
    public static Date date(String date) throws ParseException {
        return org.apache.commons.lang3.time.DateUtils.parseDateStrictly(date, PATTERNS);
    }

    /**
     * 获取最早时间
     * @param dates 日期数组
     * @return 最早时间
     */
    public static Date earliest(Date... dates) {
        if(dates == null || dates.length == 0) {
            return null;
        }
        Arrays.sort(dates);
        return dates[0];
    }

    /**
     * 获取最晚时间
     * @param dates 日期数组
     * @return 最晚时间
     */
    public static Date latest(Date... dates) {
        if(dates == null || dates.length == 0) {
            return null;
        }
        Arrays.sort(dates);
        return dates[dates.length - 1];
    }
}













