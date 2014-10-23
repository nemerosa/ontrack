package net.nemerosa.ontrack.model.exceptions;

public class BranchTemplateHasBuildException extends InputException {
    public BranchTemplateHasBuildException(String name) {
        super("Branch %s has existing builds, and cannot be made a template definition", name);
    }
}
