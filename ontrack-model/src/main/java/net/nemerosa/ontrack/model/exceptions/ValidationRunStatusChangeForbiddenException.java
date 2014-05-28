package net.nemerosa.ontrack.model.exceptions;

public class ValidationRunStatusChangeForbiddenException extends BaseException {
    public ValidationRunStatusChangeForbiddenException(String from, String to) {
        super("[%s] --> [%s] change is not allowed.", from, to);
    }
}
