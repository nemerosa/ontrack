package net.nemerosa.ontrack.model.exceptions;

public class BranchTemplateCannotHaveBuildException extends InputException {
    public BranchTemplateCannotHaveBuildException(String name) {
        super("Branch %s is a template definition and cannot have builds.");
    }
}
