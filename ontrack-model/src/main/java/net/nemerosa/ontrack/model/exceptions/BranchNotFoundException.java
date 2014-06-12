package net.nemerosa.ontrack.model.exceptions;

import net.nemerosa.ontrack.model.structure.ID;

public class BranchNotFoundException extends NotFoundException {

    public BranchNotFoundException(ID id) {
        super("Branch ID not found: %s", id);
    }

    public BranchNotFoundException(String project, String branch) {
        super("Branch not found: %s/%s", project, branch);
    }

}
