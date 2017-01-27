package net.nemerosa.ontrack.model.exceptions;

public class RoleNotFoundException extends NotFoundException {
    public RoleNotFoundException(String name) {
        super("Role not found: %s", name);
    }
}
