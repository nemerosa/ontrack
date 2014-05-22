package net.nemerosa.ontrack.model.exceptions;

public class ValidationStampNameAlreadyDefinedException extends DuplicationException {

    public ValidationStampNameAlreadyDefinedException(String name) {
        super("Validation stamp name already exists: %s", name);
    }
}
