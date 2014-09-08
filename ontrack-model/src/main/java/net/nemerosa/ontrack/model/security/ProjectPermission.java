package net.nemerosa.ontrack.model.security;

import lombok.Data;

/**
 * Definition of a permission on a project: a {@link net.nemerosa.ontrack.model.security.PermissionTarget}
 * gets associated with a {@link net.nemerosa.ontrack.model.security.ProjectRole}.
 */
@Data
public class ProjectPermission {

    private final PermissionTarget target;
    private final ProjectRole role;

}
