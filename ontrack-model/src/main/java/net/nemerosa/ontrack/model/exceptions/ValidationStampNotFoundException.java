package net.nemerosa.ontrack.model.exceptions;

import net.nemerosa.ontrack.model.structure.ID;

public class ValidationStampNotFoundException extends NotFoundException {

    public ValidationStampNotFoundException(ID id) {
        super("Validation stamp ID not found: %s", id);
    }
}
