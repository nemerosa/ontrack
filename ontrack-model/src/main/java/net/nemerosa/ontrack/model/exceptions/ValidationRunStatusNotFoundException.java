package net.nemerosa.ontrack.model.exceptions;

import net.nemerosa.ontrack.common.BaseException;

public class ValidationRunStatusNotFoundException extends BaseException {
    public ValidationRunStatusNotFoundException(String status) {
        super("Status [%s] is not defined.", status);
    }
}
