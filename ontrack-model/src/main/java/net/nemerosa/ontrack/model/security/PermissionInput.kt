package net.nemerosa.ontrack.model.security

class PermissionInput(
        /**
         * Role ID
         */
        val role: String
) {

    companion object {
        /**
         * Builder
         */
        fun of(role: String): PermissionInput {
            return PermissionInput(role)
        }
    }

}