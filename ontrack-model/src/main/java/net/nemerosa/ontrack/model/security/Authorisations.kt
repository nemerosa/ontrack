package net.nemerosa.ontrack.model.security;

import java.io.Serializable
import kotlin.reflect.KClass

data class Authorisations(
    private val projectFunctions: Set<KClass<out ProjectFunction>> = emptySet(),
    private val globalFunctions: Set<KClass<out GlobalFunction>> = emptySet(),
    private val globalRole: GlobalRole? = null,
    private val projectRoleAssociations: Set<ProjectRoleAssociation> = emptySet()
) : AuthorisationsCheck, Serializable {

    companion object {
        @JvmStatic
        fun none() = Authorisations(
            projectFunctions = emptySet(),
            globalFunctions = emptySet(),
            globalRole = null,
            projectRoleAssociations = emptySet(),
        )
    }

    override fun isGranted(fn: Class<out GlobalFunction>) =
        globalFunctions.map { it.java }.any { fn.isAssignableFrom(it) } ||
                (globalRole != null && globalRole.isGlobalFunctionGranted(fn))

    override fun isGranted(projectId: Int, fn: Class<out ProjectFunction>) =
        (globalRole != null && globalRole.isProjectFunctionGranted(fn))
                || projectFunctions.map { it.java }.any { fn.isAssignableFrom(it) }
                || projectRoleAssociations.any { pa -> pa.projectId == projectId && pa.isGranted(fn) }

    fun withProjectFunctions(projectFunctions: Set<KClass<out ProjectFunction>>) =
        Authorisations(projectFunctions, globalFunctions, globalRole, projectRoleAssociations)

    fun withGlobalFunctions(globalFunctions: Set<KClass<out GlobalFunction>>) =
        Authorisations(projectFunctions, globalFunctions, globalRole, projectRoleAssociations)

    fun withGlobalRole(globalRole: GlobalRole?) =
        Authorisations(projectFunctions, globalFunctions, globalRole, projectRoleAssociations)

    fun withProjectRoles(projectRoleAssociations: Collection<ProjectRoleAssociation>) =
        Authorisations(
            projectFunctions,
            globalFunctions,
            globalRole,
            this.projectRoleAssociations + projectRoleAssociations
        )

    fun withProjectRole(projectRoleAssociation: ProjectRoleAssociation) =
        Authorisations(
            projectFunctions,
            globalFunctions,
            globalRole,
            this.projectRoleAssociations + projectRoleAssociation
        )
}
