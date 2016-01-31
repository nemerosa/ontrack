package net.nemerosa.ontrack.common;

public class TaskInterruptedException extends BaseException {

    public TaskInterruptedException(String message) {
        super("Task was interrupted: %s", message);
    }

}
