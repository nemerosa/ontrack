package net.nemerosa.ontrack.model.structure

import net.nemerosa.ontrack.model.security.Account
import java.time.Duration

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

    /**
     * Gets the token of an account using its ID
     */
    fun getToken(accountId: Int): Token?

    /**
     * Sets a different validity for a given token. Generates the token if needed.
     *
     * @param accountId ID of teh account to generate an account for
     * @param forceUnlimited If [validity] is `null`, forces this value
     */
    fun generateToken(accountId: Int, validity: Duration?, forceUnlimited: Boolean): Token

    /**
     * Gets the account which is associated with this token, if any.
     *
     * @return Association of the actual token and its account.
     */
    fun findAccountByToken(token: String): TokenAccount?

    /**
     * Revokes all tokens
     *
     * @return Number of tokens having been revoked
     */
    fun revokeAll(): Int

    /**
     * Revokes the token for a given account
     */
    fun revokeToken(accountId: Int)

    /**
     * Checks if this token is still valid. This method must return fast
     * because it's used to quickly check the validity of tokens
     * upon authentication.
     */
    fun isValid(token: String): Boolean

}
