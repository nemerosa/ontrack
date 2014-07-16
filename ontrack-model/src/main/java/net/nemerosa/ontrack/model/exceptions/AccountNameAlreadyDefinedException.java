package net.nemerosa.ontrack.model.exceptions;

public class AccountNameAlreadyDefinedException extends DuplicationException {
    public AccountNameAlreadyDefinedException(String name) {
        super("Another account with name %s already exists.", name);
    }
}
