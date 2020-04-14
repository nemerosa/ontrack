package net.nemerosa.ontrack.model.security;

import java.io.Serializable

data class Authorisations(
        private val globalRole: GlobalRole? = null,
        private val projectRoleAssociations: Set<ProjectRoleAssociation> = emptySet()
) : AuthorisationsCheck, Serializable {

    companion object {
        @JvmStatic
        fun none() = Authorisations(null, emptySet())
    }


    override fun isGranted(fn: Class<out GlobalFunction>) = globalRole != null && globalRole.isGlobalFunctionGranted(fn)

    override fun isGranted(projectId: Int, fn: Class<out ProjectFunction>) =
            (globalRole != null && globalRole.isProjectFunctionGranted(fn))
                    || projectRoleAssociations.any { pa -> pa.projectId == projectId && pa.isGranted(fn) }

    fun withGlobalRole(globalRole: GlobalRole?) = Authorisations(globalRole, projectRoleAssociations)

    fun withProjectRoles(projectRoleAssociations: Collection<ProjectRoleAssociation>) =
            Authorisations(globalRole, this.projectRoleAssociations + projectRoleAssociations)

    fun withProjectRole(projectRoleAssociation: ProjectRoleAssociation) =
            Authorisations(globalRole, this.projectRoleAssociations + projectRoleAssociation)
}
