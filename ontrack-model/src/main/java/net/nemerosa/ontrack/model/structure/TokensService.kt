package net.nemerosa.ontrack.model.structure

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.model.security.Account
import java.time.Duration
import java.time.LocalDateTime

/**
 * Management of account tokens.
 */
interface TokensService {

    companion object {
        /**
         * Default token used for migration of unique tokens
         */
        @Deprecated("Use named tokens. Will be removed in V5.")
        const val DEFAULT_NAME = "default"
    }

    /**
     * Gets the token of the current user
     */
    @Deprecated("Use named tokens")
    val currentToken: Token?

    /**
     * Gets an existing token for the current user
     *
     * @param name Name of the token
     * @return Any token if any
     */
    fun getCurrentToken(name: String): Token?

    /**
     * Generates a new token for the current user
     */
    @Deprecated("Use token with options")
    fun generateNewToken(): Token

    /**
     * Generates a new token for the current user with the given options
     */
    fun generateNewToken(options: TokenOptions): Token

    /**
     * Revokes the token of the current user
     */
    @Deprecated("Use named tokens")
    fun revokeToken()

    /**
     * Revokes the token of the current user
     */
    fun revokeToken(name: String)

    /**
     * Gets the token of an account
     */
    @Deprecated("Use list of tokens")
    fun getToken(account: Account): Token?

    /**
     * Gets the tokens of an account
     */
    fun getTokens(account: Account): List<Token>

    /**
     * Gets the token of an account using its ID
     */
    @Deprecated("Use list of tokens")
    fun getToken(accountId: Int): Token?

    /**
     * Gets the tokens of an account using its ID
     */
    fun getTokens(accountId: Int): List<Token>

    /**
     * Sets a different validity for a given token. Generates the token if needed.
     *
     * @param accountId ID of teh account to generate an account for
     * @param forceUnlimited If [validity] is `null`, forces this value
     */
    @Deprecated("Use named token")
    fun generateToken(accountId: Int, validity: Duration?, forceUnlimited: Boolean): Token

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
    @Deprecated("Use named tokens")
    fun revokeToken(accountId: Int)

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

}
