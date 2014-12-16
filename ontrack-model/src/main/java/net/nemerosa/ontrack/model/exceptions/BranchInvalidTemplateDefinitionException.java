package net.nemerosa.ontrack.model.exceptions;

public class BranchInvalidTemplateDefinitionException extends InputException {

    public BranchInvalidTemplateDefinitionException() {
        super("A template definition must contain at least either a template parameter or a synchronisation source.");
    }
}
