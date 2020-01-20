package net.nemerosa.ontrack.model.exceptions;

import net.nemerosa.ontrack.model.structure.ID;

public class PromotionRunNotFoundException extends NotFoundException {

    public PromotionRunNotFoundException(ID id) {
        super("Promotion run ID not found: %s", id);
    }

}
