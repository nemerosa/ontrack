package net.nemerosa.ontrack.model.exceptions;

public class ProjectNameAlreadyDefinedException extends DuplicationException {

    public ProjectNameAlreadyDefinedException(String name) {
        super("Project name already exists: %s", name);
    }
}
