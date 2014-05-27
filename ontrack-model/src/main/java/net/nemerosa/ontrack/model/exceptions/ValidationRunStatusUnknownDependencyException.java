package net.nemerosa.ontrack.model.exceptions;

public class ValidationRunStatusUnknownDependencyException extends BaseException {
    public ValidationRunStatusUnknownDependencyException(String status, String dependency) {
        super("Status [%s] has an unknown dependency: %s.", status, dependency);
    }
}
