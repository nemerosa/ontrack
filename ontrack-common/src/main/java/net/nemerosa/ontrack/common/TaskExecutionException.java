package net.nemerosa.ontrack.common;

public class TaskExecutionException extends BaseException {

    public TaskExecutionException(String message, Throwable cause) {
        super("Task was interrupted: %s", message);
        initCause(cause);
    }

}
