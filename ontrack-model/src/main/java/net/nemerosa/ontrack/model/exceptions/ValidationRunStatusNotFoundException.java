package net.nemerosa.ontrack.model.exceptions;

public class ValidationRunStatusNotFoundException extends BaseException {
    public ValidationRunStatusNotFoundException(String status) {
        super("Status [%s] is not defined.", status);
    }
}
