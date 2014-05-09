package net.nemerosa.ontrack.model.exceptions;

public abstract class NotFoundException extends InputException {
    public NotFoundException(String pattern, Object... parameters) {
        super(pattern, parameters);
    }
}
