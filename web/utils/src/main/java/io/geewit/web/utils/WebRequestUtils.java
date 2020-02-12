package io.geewit.web.utils;

import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author geewit
 * @since  2016-12-13
 */
@SuppressWarnings({"unused"})
public class WebRequestUtils {

    public static boolean isIE(HttpServletRequest request) {
        String userAgent = request.getHeader(HttpHeaders.USER_AGENT);
        if(userAgent == null) {
            return false;
        }
        userAgent = userAgent.toLowerCase();
        return (userAgent.indexOf("msie") > 0 || userAgent.indexOf("rv:11.0") > 0 || userAgent.indexOf("edge") > 0);
    }
}
