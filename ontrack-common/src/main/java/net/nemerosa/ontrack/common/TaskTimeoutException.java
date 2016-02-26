package net.nemerosa.ontrack.common;

public class TaskTimeoutException extends BaseException {

    public TaskTimeoutException(String message, long seconds) {
        super("Timeout for the task after %d seconds: %s", seconds, message);
    }

}
