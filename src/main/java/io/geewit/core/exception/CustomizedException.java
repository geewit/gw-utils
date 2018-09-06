package io.geewit.core.exception;

import org.springframework.http.HttpStatus;

/**
 * @author geewit
 * @since  2017/4/15
 */
@SuppressWarnings({"unused"})
public class CustomizedException extends RuntimeException {
    public CustomizedException(String message) {
        super(message);
        this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public CustomizedException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }


    private HttpStatus httpStatus;

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
