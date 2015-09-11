package net.nemerosa.ontrack.model.exceptions;

import net.nemerosa.ontrack.model.structure.ID;

public class AccountGroupMappingNotFoundException extends NotFoundException {
    public AccountGroupMappingNotFoundException(ID id) {
        super("Account group mapping ID not found: %s", id);
    }
}
