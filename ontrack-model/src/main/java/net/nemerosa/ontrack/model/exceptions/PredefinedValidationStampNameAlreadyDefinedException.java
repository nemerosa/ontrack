package net.nemerosa.ontrack.model.exceptions;

public class PredefinedValidationStampNameAlreadyDefinedException extends DuplicationException {

    public PredefinedValidationStampNameAlreadyDefinedException(String name) {
        super("Predefined validation stamp name already exists: %s", name);
    }
}
