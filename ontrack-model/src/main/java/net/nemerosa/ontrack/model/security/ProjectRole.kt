package net.nemerosa.ontrack.model.security

import java.io.Serializable

/**
 * A project role is the association between an identifier, a name and a set of
 * [project functions][net.nemerosa.ontrack.model.security.ProjectFunction].
 */
class ProjectRole(
        /**
         * Project role's identifier
         */
        val id: String,
        /**
         * Project role's name
         */
        val name: String,
        /**
         * Description
         */
        val description: String,
        /**
         * Associated set of project functions
         */
        val functions: Set<Class<out ProjectFunction>>) : Serializable {

    fun isGranted(functionToCheck: Class<out ProjectFunction>): Boolean =
            functions.any { functionToCheck.isAssignableFrom(it) }

}