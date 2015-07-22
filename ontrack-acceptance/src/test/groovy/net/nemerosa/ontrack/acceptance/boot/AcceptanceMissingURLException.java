package net.nemerosa.ontrack.acceptance.boot;

public class AcceptanceMissingURLException extends RuntimeException {

    public AcceptanceMissingURLException() {
        super("The `ontrack.url` configuration property must be defined.");
    }
}
