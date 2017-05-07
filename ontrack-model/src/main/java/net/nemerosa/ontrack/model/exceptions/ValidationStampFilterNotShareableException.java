package net.nemerosa.ontrack.model.exceptions;

public class ValidationStampFilterNotShareableException extends InputException {
    public ValidationStampFilterNotShareableException(String name, String reason) {
        super("Validation stamp filter with name \"%s\" cannot be shared: %s", name, reason);
    }
}
