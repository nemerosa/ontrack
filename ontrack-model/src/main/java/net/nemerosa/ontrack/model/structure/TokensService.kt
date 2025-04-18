package net.nemerosa.ontrack.model.structure

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.model.security.Account
import java.time.LocalDateTime

/**
 * Management of account tokens.
 */
interface TokensService {

    /**
     * Gets an existing token for the current user
     *
     * @param name Name of the token
     * @return Any token if any
     */
    fun getCurrentToken(name: String): Token?

    /**
     * Generates a new token for the current user with the given options
     */
    fun generateNewToken(options: TokenOptions): Token

    /**
     * Revokes the token of the current user
     */
    fun revokeToken(name: String)

    /**
     * Gets the tokens of an account
     */
    fun getTokens(account: Account): List<Token>

    /**
     * Gets the tokens of an account using its ID
     */
    fun getTokens(accountId: Int): List<Token>

    /**
     * Sets a different validity for a given token. Generates the token if needed.
     *
     * @param accountId ID of teh account to generate an account for
     * @param options Token options
     */
    fun generateToken(accountId: Int, options: TokenOptions): Token

    /**
     * Gets the account which is associated with this token, if any.
     *
     * @return Association of the actual token and its account.
     */
    fun findAccountByToken(token: String, refTime: LocalDateTime = Time.now()): TokenAccount?

    /**
     * Revokes all tokens
     *
     * @return Number of tokens having been revoked
     */
    fun revokeAll(): Int

    /**
     * Revokes a token for a given account
     */
    fun revokeToken(accountId: Int, name: String)

    /**
     * Revokes all tokens for a given account
     */
    fun revokeAllTokens(accountId: Int)

    /**
     * Checks if this token is still valid. This method must return fast
     * because it's used to quickly check the validity of tokens
     * upon authentication.
     */
    fun isValid(token: String): Boolean

    /**
     * Uses a token for the security context
     *
     * @param token Token to use
     * @return True is the token was valid and could be used
     */
    fun useTokenForSecurityContext(token: String): Boolean

}
