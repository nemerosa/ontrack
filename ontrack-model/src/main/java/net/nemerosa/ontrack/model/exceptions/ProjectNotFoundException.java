package net.nemerosa.ontrack.model.exceptions;

import net.nemerosa.ontrack.model.structure.ID;

public class ProjectNotFoundException extends NotFoundException {

    public ProjectNotFoundException(ID id) {
        super("Project ID not found: %s", id);
    }

    public ProjectNotFoundException(String name) {
        super("Project name not found: %s", name);
    }
    
}
