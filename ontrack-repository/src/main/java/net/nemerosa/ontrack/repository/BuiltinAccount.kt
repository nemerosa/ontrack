package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.model.security.Account
import net.nemerosa.ontrack.model.security.SecurityRole

data class BuiltinAccount(
        val id: Int,
        val name: String,
        val fullName: String,
        val email: String,
        val password: String,
        val role: SecurityRole
) {
    constructor(account: Account, password: String) : this(
            id = account.id(),
            name = account.name,
            fullName = account.fullName,
            email = account.email,
            password = password,
            role = account.role
    )
}
