package net.nemerosa.ontrack.model.security;

import lombok.Data;

@Data
public class GlobalPermission {

    private final PermissionTarget target;
    private final GlobalRole role;

}
