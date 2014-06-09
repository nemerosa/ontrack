package net.nemerosa.ontrack.model.exceptions;

import net.nemerosa.ontrack.common.BaseException;

public class ValidationRunStatusUnknownDependencyException extends BaseException {
    public ValidationRunStatusUnknownDependencyException(String status, String dependency) {
        super("Status [%s] has an unknown dependency: %s.", status, dependency);
    }
}
