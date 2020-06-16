package io.geewit.web.utils;

import org.springframework.http.HttpHeaders;
import org.springframework.util.Assert;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

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

    public static Cookie[] getCookie(HttpServletRequest request, String... names) {
        Assert.notNull(request, "Request must not be null");
        Cookie[] cookies = request.getCookies();
        Cookie[] result = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                for(String name : names) {
                    if (name.equals(cookie.getName())) {
                        if(result == null) {
                            result = new Cookie[]{cookie};
                        } else {
                            result = Arrays.copyOf(result, result.length + 1);
                            result[result.length - 1] = cookie;
                        }
                    }
                }

            }
        }
        return result;
    }
}
