package net.nemerosa.ontrack.model.exceptions;

public class BuildNameAlreadyDefinedException extends DuplicationException {

    public BuildNameAlreadyDefinedException(String name) {
        super("Build name already exists: %s", name);
    }
}
