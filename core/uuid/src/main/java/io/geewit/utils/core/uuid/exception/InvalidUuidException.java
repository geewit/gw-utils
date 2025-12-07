package io.geewit.utils.core.uuid.exception;

import java.util.Arrays;

/**
 * Runtime exception to be used when an invalid UUID is received as argument.
 */
public final class InvalidUuidException extends RuntimeException {

    @java.io.Serial
    private static final long serialVersionUID = 1L;

    /**
     * Default constructor with a message.
     * 
     * @param message a message
     */
    public InvalidUuidException(String message) {
        super(message);
    }

    /**
     * Default constructor with a message and the cause.
     * 
     * @param message a message
     * @param cause   the cause
     */
    public InvalidUuidException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Factory method for creating a runtime exception.
     * 
     * @param obj an object that can, for example, a string of a char array.
     * @return a runtime exception
     */
    public static InvalidUuidException newInstance(Object obj) {

        String string;
        if (obj == null) {
            string = null;
        } else if (obj instanceof char[]) {
            string = String.valueOf((char[]) obj);
        } else if (obj.getClass().isArray()) {
            string = Arrays.toString((byte[]) obj);
        } else {
            string = String.valueOf(obj);
        }

        if (string != null) {
            string = "\"" + string + "\"";
        }

        return new InvalidUuidException("Invalid UUID: " + string);
    }
}
