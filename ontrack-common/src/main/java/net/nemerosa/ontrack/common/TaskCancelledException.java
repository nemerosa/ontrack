package net.nemerosa.ontrack.common;

public class TaskCancelledException extends BaseException {

    public TaskCancelledException(String message) {
        super("Task was cancelled: %s", message);
    }

}
