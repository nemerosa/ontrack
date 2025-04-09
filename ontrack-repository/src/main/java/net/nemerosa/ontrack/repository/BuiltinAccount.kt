package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.model.security.Account

@Deprecated("Will be removed in V5")
data class BuiltinAccount(
    val account: Account,
    var password: String
)
