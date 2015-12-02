package net.nemerosa.ontrack.extension.svn.model;

import net.nemerosa.ontrack.model.exceptions.InputException;

public class BranchPathRequiredException extends InputException {

    public BranchPathRequiredException() {
        super("Branch path is required");
    }

}
