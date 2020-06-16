package io.geewit.web.utils;

import org.springframework.http.HttpHeaders;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

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

    public static Map<String, String> getCookies(HttpServletRequest request, String... names) {
        Assert.notNull(request, "Request must not be null");
        Cookie[] cookies = request.getCookies();
        Map<String, String> result = new HashMap<>();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                for(String name : names) {
                    if (name.equals(cookie.getName()) && !StringUtils.isEmpty(cookie.getValue())) {
                        result.put(name, cookie.getValue());
                    }
                }

            }
        }
        return result;
    }

    public static Map<String, Enumeration<String>> getHeaders(HttpServletRequest request, String... names) {
        Assert.notNull(request, "Request must not be null");
        Map<String, Enumeration<String>> result = new HashMap<>();
        for(String name : names) {
            Enumeration<String> headers = request.getHeaders(name);
            result.put(name, headers);
        }

        return result;
    }
}
