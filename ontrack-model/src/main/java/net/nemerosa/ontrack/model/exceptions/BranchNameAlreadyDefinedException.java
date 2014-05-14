package net.nemerosa.ontrack.model.exceptions;

public class BranchNameAlreadyDefinedException extends DuplicationException {

    public BranchNameAlreadyDefinedException(String name) {
        super("Branch name already exists: %s", name);
    }
}
