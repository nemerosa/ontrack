package net.nemerosa.ontrack.common;

public class TaskNotScheduledException extends BaseException {

    public TaskNotScheduledException(String message) {
        super("Task was not launched in the background: %s", message);
    }

}
