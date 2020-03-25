package net.nemerosa.ontrack.model.security

import java.io.Serializable

/**
 * Association of a project role and a projet ID.
 */
data class ProjectRoleAssociation(val projectId: Int, val projectRole: ProjectRole) : Serializable {

    fun isGranted(fn: Class<out ProjectFunction>): Boolean = projectRole.isGranted(fn)

}