package net.nemerosa.ontrack.model.exceptions;

public class AccountGroupMappingNameAlreadyDefinedException extends DuplicationException {
    public AccountGroupMappingNameAlreadyDefinedException(String name) {
        super("Another account group mapping with name %s already exists.", name);
    }
}
