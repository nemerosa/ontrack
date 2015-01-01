package net.nemerosa.ontrack.extension.api.model;

import net.nemerosa.ontrack.model.exceptions.InputException;

public class BuildValidationException extends InputException {

    public BuildValidationException(String message) {
        super(message);
    }

}
