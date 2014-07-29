package net.nemerosa.ontrack.extension.scm.model;

import net.nemerosa.ontrack.model.exceptions.InputException;

public class SCMChangeLogUUIDException extends InputException {
    public SCMChangeLogUUIDException(String uuid) {
        super("The change log with UUID %s has been requested but not found. " +
                "It was either invalid or not valid any longer.", uuid);
    }
}
