package net.nemerosa.ontrack.extension.scm.model;

import net.nemerosa.ontrack.model.exceptions.NotFoundException;

public class SCMDocumentNotFoundException extends NotFoundException {

    public SCMDocumentNotFoundException(String path) {
        super("Path %s not found", path);
    }

}
