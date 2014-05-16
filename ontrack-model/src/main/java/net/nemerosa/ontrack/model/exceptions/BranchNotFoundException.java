package net.nemerosa.ontrack.model.exceptions;

import net.nemerosa.ontrack.model.structure.ID;

public class BranchNotFoundException extends NotFoundException {

    public BranchNotFoundException(ID id) {
        super("Branch ID not found: %s", id);
    }
}
