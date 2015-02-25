package net.nemerosa.ontrack.extension.scm;

import net.nemerosa.ontrack.model.exceptions.NotFoundException;

public class SCMFileChangeFilterNotFound extends NotFoundException {
    public SCMFileChangeFilterNotFound(String name) {
        super(String.format("Change log file filter with name %s cannot be found."));
    }
}
