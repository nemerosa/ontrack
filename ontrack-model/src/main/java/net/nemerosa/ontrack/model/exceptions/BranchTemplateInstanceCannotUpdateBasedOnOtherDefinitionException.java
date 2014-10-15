package net.nemerosa.ontrack.model.exceptions;

public class BranchTemplateInstanceCannotUpdateBasedOnOtherDefinitionException extends InputException {
    public BranchTemplateInstanceCannotUpdateBasedOnOtherDefinitionException(String name) {
        super("Branch %s is based on another branch template definition, and cannot be updated.", name);
    }
}
