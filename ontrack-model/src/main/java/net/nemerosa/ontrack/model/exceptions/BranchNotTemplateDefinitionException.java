package net.nemerosa.ontrack.model.exceptions;

import net.nemerosa.ontrack.model.structure.ID;

public class BranchNotTemplateDefinitionException extends InputException {
    public BranchNotTemplateDefinitionException(ID id) {
        super("Branch %s is not a template definition", id);
    }
}
