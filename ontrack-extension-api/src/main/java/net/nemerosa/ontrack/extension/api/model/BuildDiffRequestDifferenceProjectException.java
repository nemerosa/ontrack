package net.nemerosa.ontrack.extension.api.model;

import net.nemerosa.ontrack.model.exceptions.InputException;

public class BuildDiffRequestDifferenceProjectException extends InputException {
    public BuildDiffRequestDifferenceProjectException() {
        super("A diff between two builds can only be done on the same project.");
    }
}
