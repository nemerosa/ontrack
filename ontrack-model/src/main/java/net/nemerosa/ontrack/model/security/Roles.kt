package net.nemerosa.ontrack.model.security

/**
 * List of predefined roles
 */
interface Roles {

    companion object {
        /**
         * The project owner is allowed to all functions in a project, but for its deletion.
         */
        const val PROJECT_OWNER = "OWNER"
        /**
         * A participant in a project is allowed to change statuses in validation runs.
         */
        const val PROJECT_PARTICIPANT = "PARTICIPANT"
        /**
         * The validation manager can manage the validation stamps.
         */
        const val PROJECT_VALIDATION_MANAGER = "VALIDATION_MANAGER"
        /**
         * The promoter can promote existing builds.
         */
        const val PROJECT_PROMOTER = "PROMOTER"
        /**
         * The project manager can promote existing builds, manage the validation stamps,
         * manage the shared build filters and edit some properties.
         */
        const val PROJECT_MANAGER = "PROJECT_MANAGER"
        /**
         * This role grants a read-only access to all components of the projects.
         */
        const val PROJECT_READ_ONLY = "READ_ONLY"

        @JvmStatic
        val PROJECT_ROLES: Set<String> = setOf(
                PROJECT_OWNER,
                PROJECT_PARTICIPANT,
                PROJECT_VALIDATION_MANAGER,
                PROJECT_PROMOTER,
                PROJECT_MANAGER,
                PROJECT_READ_ONLY
        )

        /**
         * List of global roles
         */

        const val GLOBAL_ADMINISTRATOR = "ADMINISTRATOR"
        const val GLOBAL_CREATOR = "CREATOR"
        const val GLOBAL_AUTOMATION = "AUTOMATION"
        const val GLOBAL_CONTROLLER = "CONTROLLER"
        const val GLOBAL_PARTICIPANT = "PARTICIPANT"
        const val GLOBAL_READ_ONLY = "READ_ONLY"
        /**
         * The global validation manager can manage the validation stamps across all projects.
         */
        const val GLOBAL_VALIDATION_MANAGER = "GLOBAL_VALIDATION_MANAGER"

        @JvmStatic
        val GLOBAL_ROLES: Set<String> = setOf(
                GLOBAL_ADMINISTRATOR,
                GLOBAL_CREATOR,
                GLOBAL_AUTOMATION,
                GLOBAL_CONTROLLER,
                GLOBAL_READ_ONLY,
                GLOBAL_PARTICIPANT,
                GLOBAL_VALIDATION_MANAGER
        )
    }
}
