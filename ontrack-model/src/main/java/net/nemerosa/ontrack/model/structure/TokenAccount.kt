package net.nemerosa.ontrack.model.structure

import net.nemerosa.ontrack.model.security.Account

/**
 * Association of an [Account] and its [Token].
 */
class TokenAccount(
        val account: Account,
        val token: Token
)