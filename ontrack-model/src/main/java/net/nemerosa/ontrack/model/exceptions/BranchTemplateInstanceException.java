package net.nemerosa.ontrack.model.exceptions;

public class BranchTemplateInstanceException extends InputException {
    public BranchTemplateInstanceException(String name) {
        super("Branch %s is a template instance, and cannot be made a template definition", name);
    }
}
