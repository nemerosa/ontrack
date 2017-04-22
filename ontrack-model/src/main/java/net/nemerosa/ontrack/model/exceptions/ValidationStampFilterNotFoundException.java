package net.nemerosa.ontrack.model.exceptions;

import net.nemerosa.ontrack.model.structure.ID;

public class ValidationStampFilterNotFoundException extends NotFoundException {
    public ValidationStampFilterNotFoundException(ID filterId) {
        super("Validation stamp filter with ID %s not found", filterId);
    }
}
