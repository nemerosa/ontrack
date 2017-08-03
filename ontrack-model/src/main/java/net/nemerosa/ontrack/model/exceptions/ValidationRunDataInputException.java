package net.nemerosa.ontrack.model.exceptions;

public class ValidationRunDataInputException extends InputException {
    public ValidationRunDataInputException(String pattern, Object... parameters) {
        super(pattern, parameters);
    }
}
