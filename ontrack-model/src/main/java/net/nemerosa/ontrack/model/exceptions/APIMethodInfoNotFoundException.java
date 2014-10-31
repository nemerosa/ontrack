package net.nemerosa.ontrack.model.exceptions;

public class APIMethodInfoNotFoundException extends NotFoundException {
    public APIMethodInfoNotFoundException(String path) {
        super("API information for path [%s] not found.", path);
    }
}
