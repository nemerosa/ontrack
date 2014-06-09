package net.nemerosa.ontrack.model.exceptions;

import net.nemerosa.ontrack.common.BaseException;

public class ValidationRunStatusChangeForbiddenException extends BaseException {
    public ValidationRunStatusChangeForbiddenException(String from, String to) {
        super("[%s] --> [%s] change is not allowed.", from, to);
    }
}
