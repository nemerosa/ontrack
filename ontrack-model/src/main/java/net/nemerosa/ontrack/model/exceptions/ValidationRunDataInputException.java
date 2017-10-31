package net.nemerosa.ontrack.model.exceptions;

public class ValidationRunDataInputException extends InputException {
    public ValidationRunDataInputException(String pattern) {
        super(pattern);
    }
}
