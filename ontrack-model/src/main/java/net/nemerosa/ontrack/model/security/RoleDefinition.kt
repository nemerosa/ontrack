package net.nemerosa.ontrack.model.security

class RoleDefinition(
        val id: String,
        val name: String,
        val description: String,
        /**
         * Link to a parent built-in role, to inherited functions from. The parent role must be
         * either `null` or defined.
         */
        val parent: String? = null
)
