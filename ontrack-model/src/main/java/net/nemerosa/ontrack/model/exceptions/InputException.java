package net.nemerosa.ontrack.model.exceptions;

public abstract class InputException extends BaseException {
    public InputException(String pattern, Object... parameters) {
        super(pattern, parameters);
    }
}
