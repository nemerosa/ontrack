package net.nemerosa.ontrack.model.exceptions;

public abstract class DuplicationException extends InputException {
    public DuplicationException(String pattern, Object... parameters) {
        super(pattern, parameters);
    }
}
