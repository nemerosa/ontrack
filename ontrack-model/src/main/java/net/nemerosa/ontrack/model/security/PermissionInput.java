package net.nemerosa.ontrack.model.security;

import lombok.Data;

@Data
public class PermissionInput {

    /**
     * Role ID
     */
    private final String role;

    /**
     * Builder
     */
    public static PermissionInput of(String role) {
        return new PermissionInput(role);
    }

}
