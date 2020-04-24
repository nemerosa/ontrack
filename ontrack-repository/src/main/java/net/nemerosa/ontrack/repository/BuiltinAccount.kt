package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.model.security.Account

data class BuiltinAccount(
        val account: Account,
        var password: String
)
