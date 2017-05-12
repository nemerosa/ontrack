package net.nemerosa.ontrack.model.exceptions;

public class ValidationStampFilterNameAlreadyDefinedException extends InputException {
    public ValidationStampFilterNameAlreadyDefinedException(String name) {
        super("Validation stamp filter with name \"%s\" already exists in its context", name);
    }
}
