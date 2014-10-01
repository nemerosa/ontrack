package net.nemerosa.ontrack.service;

import net.nemerosa.ontrack.model.exceptions.InputException;

public class CannotCopyItselfException extends InputException {
    public CannotCopyItselfException() {
        super("Cannot copy the branch on itself.");
    }
}
