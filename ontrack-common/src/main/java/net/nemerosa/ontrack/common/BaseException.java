package net.nemerosa.ontrack.common;

public abstract class BaseException extends RuntimeException {

    public BaseException(String message) {
        super(message);
    }

    public BaseException(String pattern, Object... parameters) {
        this(String.format(pattern, parameters));
    }

    public BaseException(Exception ex, String pattern, Object... parameters) {
        super(String.format(pattern, parameters), ex);
    }
}
