package net.nemerosa.ontrack.model.security

import java.io.Serializable

/**
 * A global role defines the association between a name, a set of
 * [global functions][net.nemerosa.ontrack.model.security.GlobalFunction]
 * and a set of [project functions][net.nemerosa.ontrack.model.security.ProjectFunction]
 * that are attributed for all projects.
 */
class GlobalRole(
        /**
         * Global role's identifier
         */
        val id: String,
        /**
         * Global role's name
         */
        val name: String,
        /**
         * Description of the role
         */
        val description: String,
        /**
         * Global functions
         */
        val globalFunctions: Set<Class<out GlobalFunction>>,
        /**
         * Project functions to grant for all projects
         */
        val projectFunctions: Set<Class<out ProjectFunction>>
) : Serializable {

    fun isGlobalFunctionGranted(fn: Class<out GlobalFunction>): Boolean = globalFunctions.contains(fn)

    fun isProjectFunctionGranted(fn: Class<out ProjectFunction>): Boolean =
            projectFunctions.any { fn.isAssignableFrom(it) }

}