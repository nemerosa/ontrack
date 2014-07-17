package net.nemerosa.ontrack.model.exceptions;

public class AccountGroupNameAlreadyDefinedException extends DuplicationException {
    public AccountGroupNameAlreadyDefinedException(String name) {
        super("An account group with name %s already exists.");
    }
}
