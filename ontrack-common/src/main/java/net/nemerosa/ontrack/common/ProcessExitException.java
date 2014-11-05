package net.nemerosa.ontrack.common;

public class ProcessExitException extends BaseException {

    private final int exit;

    public ProcessExitException(int exit, String error) {
        super(error);
        this.exit = exit;
    }

    public int getExit() {
        return exit;
    }

}
