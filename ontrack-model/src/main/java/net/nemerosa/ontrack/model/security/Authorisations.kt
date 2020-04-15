package net.nemerosa.ontrack.model.security;

import java.io.Serializable
import kotlin.reflect.KClass

data class Authorisations(
        private val projectFunctions: Set<KClass<out ProjectFunction>> = emptySet(),
        private val globalRole: GlobalRole? = null,
        private val projectRoleAssociations: Set<ProjectRoleAssociation> = emptySet()
) : AuthorisationsCheck, Serializable {

    companion object {
        @JvmStatic
        fun none() = Authorisations(emptySet(), null, emptySet())
    }


    override fun isGranted(fn: Class<out GlobalFunction>) = globalRole != null && globalRole.isGlobalFunctionGranted(fn)

    override fun isGranted(projectId: Int, fn: Class<out ProjectFunction>) =
            (globalRole != null && globalRole.isProjectFunctionGranted(fn))
                    || fn in projectFunctions.map { it.java }
                    || projectRoleAssociations.any { pa -> pa.projectId == projectId && pa.isGranted(fn) }

    fun withProjectFunctions(projectFunctions: Set<KClass<out ProjectFunction>>) =
            Authorisations(projectFunctions, globalRole, projectRoleAssociations)

    fun withGlobalRole(globalRole: GlobalRole?) = Authorisations(projectFunctions, globalRole, projectRoleAssociations)

    fun withProjectRoles(projectRoleAssociations: Collection<ProjectRoleAssociation>) =
            Authorisations(projectFunctions, globalRole, this.projectRoleAssociations + projectRoleAssociations)

    fun withProjectRole(projectRoleAssociation: ProjectRoleAssociation) =
            Authorisations(projectFunctions, globalRole, this.projectRoleAssociations + projectRoleAssociation)
}
