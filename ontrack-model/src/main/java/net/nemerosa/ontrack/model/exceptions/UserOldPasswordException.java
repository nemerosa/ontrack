package net.nemerosa.ontrack.model.exceptions;

public class UserOldPasswordException extends InputException {
    public UserOldPasswordException() {
        super("Old password is incorrect.");
    }
}
