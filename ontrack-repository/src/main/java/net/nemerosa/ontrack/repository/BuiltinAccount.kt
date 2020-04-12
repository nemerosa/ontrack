package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.model.security.SecurityRole

data class BuiltinAccount(
        val id: Int,
        val name: String,
        val fullName: String,
        val email: String,
        val password: String,
        val role: SecurityRole
)
