package net.nemerosa.ontrack.model.exceptions;

public abstract class BaseException extends RuntimeException {

    public BaseException(String pattern, Object... parameters) {
        super(String.format(pattern, parameters));
    }

    public BaseException(Exception ex, String pattern, Object... parameters) {
        super(String.format(pattern, parameters), ex);
    }
}
