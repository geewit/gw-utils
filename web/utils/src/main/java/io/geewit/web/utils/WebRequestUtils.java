package io.geewit.web.utils;

import org.springframework.http.HttpHeaders;
import org.springframework.util.Assert;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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

    public static Map<String, Cookie> getCookies(HttpServletRequest request, String... names) {
        Assert.notNull(request, "Request must not be null");
        Cookie[] cookies = request.getCookies();
        Map<String, Cookie> result = new HashMap<>();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                for(String name : names) {
                    if (name.equals(cookie.getName())) {
                        result.put(name, cookie);
                    }
                }

            }
        }
        return result;
    }
}
