package net.nemerosa.ontrack.model.exceptions;

public class BranchClassicCannotBeTemplateInstanceException extends InputException {
    public BranchClassicCannotBeTemplateInstanceException(String name) {
        super("Branch %s is a normal branch, and cannot be made a template instance", name);
    }
}
