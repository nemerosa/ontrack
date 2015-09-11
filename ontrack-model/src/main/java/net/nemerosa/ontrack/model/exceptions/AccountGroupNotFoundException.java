package net.nemerosa.ontrack.model.exceptions;

import net.nemerosa.ontrack.model.structure.ID;

public class AccountGroupNotFoundException extends NotFoundException {
    public AccountGroupNotFoundException(ID id) {
        super("Account group ID not found: %s", id);
    }
}
