package net.nemerosa.ontrack.model.security

import net.nemerosa.ontrack.model.structure.ID

/**
 * Definition of a permission on a project: a [net.nemerosa.ontrack.model.security.PermissionTarget]
 * gets associated with a [net.nemerosa.ontrack.model.security.ProjectRole].
 */
class ProjectPermission(val projectId: ID, val target: PermissionTarget, val role: ProjectRole)