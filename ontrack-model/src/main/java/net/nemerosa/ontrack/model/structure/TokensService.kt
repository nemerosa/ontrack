package net.nemerosa.ontrack.model.structure

import net.nemerosa.ontrack.model.security.Account

/**
 * Management of account tokens.
 */
interface TokensService {

    /**
     * Gets the token of the current user
     */
    val currentToken: Token?

    /**
     * Generates a new token for the current user
     */
    fun generateNewToken(): Token

    /**
     * Revokes the token of the current user
     */
    fun revokeToken()

    /**
     * Gets the token of an account
     */
    fun getToken(account: Account): Token?

}
