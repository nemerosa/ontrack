package net.nemerosa.ontrack.model.exceptions;

public class BranchTemplateDefinitionCannotBeTemplateInstanceException extends InputException {
    public BranchTemplateDefinitionCannotBeTemplateInstanceException(String name) {
        super("Branch %s is a branch template definition, and cannot be made a template instance", name);
    }
}
