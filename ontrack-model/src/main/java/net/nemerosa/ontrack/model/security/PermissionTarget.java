package net.nemerosa.ontrack.model.security;

import lombok.Data;

@Data
public class PermissionTarget {

    private final PermissionTargetType type;
    private final int id;
    private final String name;
    private final String description;

}
