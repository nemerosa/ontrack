package net.nemerosa.ontrack.model.exceptions;

import net.nemerosa.ontrack.model.structure.ID;

public class BranchNotTemplateInstanceException extends InputException {
    public BranchNotTemplateInstanceException(ID id) {
        super("Branch %s is not a template instance", id);
    }
}
