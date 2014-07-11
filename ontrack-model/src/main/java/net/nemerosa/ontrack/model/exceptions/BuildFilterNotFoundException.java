package net.nemerosa.ontrack.model.exceptions;

public class BuildFilterNotFoundException extends NotFoundException {
    public BuildFilterNotFoundException(String name) {
        super("Build filter not defined: %s", name);
    }
}
