package net.nemerosa.ontrack.model.exceptions;

public class BuildFilterNotLoggedException extends NotFoundException {
    public BuildFilterNotLoggedException() {
        super("Cannot access build filters when not logged.");
    }
}
