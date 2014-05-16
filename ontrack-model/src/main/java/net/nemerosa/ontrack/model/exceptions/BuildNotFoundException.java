package net.nemerosa.ontrack.model.exceptions;

import net.nemerosa.ontrack.model.structure.ID;

public class BuildNotFoundException extends NotFoundException {

    public BuildNotFoundException(ID id) {
        super("Build ID not found: %s", id);
    }
}
