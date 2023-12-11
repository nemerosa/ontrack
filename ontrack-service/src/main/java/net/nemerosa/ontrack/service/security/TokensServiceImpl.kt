package net.nemerosa.ontrack.service.security

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.model.security.Account
import net.nemerosa.ontrack.model.security.AccountManagement
import net.nemerosa.ontrack.model.security.AccountService
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.model.structure.TokensService.Companion.DEFAULT_NAME
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import net.nemerosa.ontrack.repository.TokensRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration

@Service
@Transactional
class TokensServiceImpl(
    private val tokensRepository: TokensRepository,
    private val securityService: SecurityService,
    private val tokenGenerator: TokenGenerator,
    private val ontrackConfigProperties: OntrackConfigProperties,
    private val accountService: AccountService
) : TokensService {

    private val cache: Cache<String, Boolean> = Caffeine.newBuilder()
        .maximumSize(ontrackConfigProperties.security.tokens.cache.maxCount)
        .expireAfterAccess(ontrackConfigProperties.security.tokens.cache.validity)
        .build()

    @Deprecated("Use named tokens")
    override val currentToken: Token?
        get() = getCurrentToken(DEFAULT_NAME)

    override fun getCurrentToken(name: String): Token? {
        // Gets the current account
        val account = securityService.currentAccount?.account
        // Gets the token of this account
        return account?.let {
            tokensRepository.getTokenForAccount(account, name)
        }
    }

    @Deprecated("Use token with options")
    override fun generateNewToken(): Token {
        return generateNewToken(
            options = TokenOptions(
                name = DEFAULT_NAME,
                scope = TokenScope.USER,
            )
        )
    }

    override fun generateNewToken(options: TokenOptions): Token {
        // Gets the current account
        val account = securityService.currentAccount?.account
            ?: throw TokenGenerationNoAccountException()
        // Checking if a token with the same name already exists
        val existing = tokensRepository.getTokenForAccount(account, options.name)
        if (existing != null) {
            throw TokenGenerationNameAlreadyExistsException(options.name)
        }
        // Generates a new token
        return securityService.asAdmin { generateToken(account.id(), options) }
    }

    @Deprecated("Use named token")
    override fun generateToken(accountId: Int, validity: Duration?, forceUnlimited: Boolean): Token {
        return generateToken(
            accountId,
            TokenOptions(
                DEFAULT_NAME,
                TokenScope.USER,
                validity,
                forceUnlimited
            )
        )
    }

    override fun generateToken(accountId: Int, options: TokenOptions): Token {
        securityService.checkGlobalFunction(AccountManagement::class.java)
        // Generates a new token
        val token = tokenGenerator.generateToken()
        // Validity
        val systemValidity = ontrackConfigProperties.security.tokens.validity
        val actualValidity = if (options.scope.transient) {
            ontrackConfigProperties.security.tokens.transientValidity
                .takeIf { !it.isZero }
                ?: OntrackConfigProperties.TokensProperties.DEFAULT_TRANSIENT_VALIDITY
        } else if (options.forceUnlimited) {
            options.validity
        } else {
            options.validity ?: systemValidity
        }
        // Token object
        val creation = Time.now()
        val validUntil = if (actualValidity != null && !actualValidity.isZero && !actualValidity.isNegative) {
            creation + actualValidity
        } else {
            null
        }
        val tokenObject = Token(options.name, token, creation, options.scope, validUntil, lastUsed = null)
        // Saves the token...
        tokensRepository.save(accountId, options.name, token, options.scope, tokenObject.creation, tokenObject.validUntil)
        // ... and returns it
        return tokenObject
    }

    @Deprecated("Use named tokens")
    override fun revokeToken() {
        revokeToken(DEFAULT_NAME)
    }

    override fun revokeToken(name: String) {
        // Gets the current account
        val account = securityService.currentAccount?.account
        // Revokes its token
        account?.apply {
            val token = tokensRepository.invalidate(id(), name)
            // Removes any cache token
            token?.let { cache.invalidate(token) }
        }
    }

    @Deprecated("Use list of tokens")
    override fun getToken(account: Account): Token? {
        securityService.checkGlobalFunction(AccountManagement::class.java)
        return tokensRepository.getTokenForAccount(account, DEFAULT_NAME)
    }

    override fun getTokens(account: Account): List<Token> {
        securityService.checkGlobalFunction(AccountManagement::class.java)
        return tokensRepository.getTokens(account)
    }

    @Deprecated("Use list of tokens")
    override fun getToken(accountId: Int): Token? {
        return getToken(accountService.getAccount(ID.of(accountId)))
    }

    override fun getTokens(accountId: Int): List<Token> {
        return getTokens(accountService.getAccount(ID.of(accountId)))
    }

    override fun isValid(token: String): Boolean {
        if (ontrackConfigProperties.security.tokens.cache.enabled) {
            val valid = cache.getIfPresent(token)
            return if (valid != null) {
                valid
            } else {
                val stillValid = internalValidityCheck(token)
                cache.put(token, stillValid)
                return stillValid
            }
        } else {
            return internalValidityCheck(token)
        }
    }

    private fun internalValidityCheck(token: String): Boolean =
        tokensRepository
            .findAccountByToken(token)
            ?.let { (_, result) ->
                result.isValid()
            }
            ?: false

    override fun findAccountByToken(token: String): TokenAccount? {
        // Find the account ID
        val result = tokensRepository.findAccountByToken(token)
        return result?.let { (accountId, token) ->
            // Last used date
            val lastUsed = Time.now()
            // Updating the "last used" date
            tokensRepository.updateLastUsed(token, lastUsed)
            // OK, returning the account AND the token
            TokenAccount(
                securityService.asAdmin {
                    accountService.getAccount(ID.of(accountId))
                },
                token.withLastUsed(lastUsed)
            )
        }
    }

    override fun revokeAll(): Int {
        securityService.checkGlobalFunction(AccountManagement::class.java)
        cache.invalidateAll()
        return tokensRepository.revokeAll()
    }

    @Deprecated("Use named tokens")
    override fun revokeToken(accountId: Int) {
        revokeToken(accountId, DEFAULT_NAME)
    }

    override fun revokeToken(accountId: Int, name: String) {
        securityService.checkGlobalFunction(AccountManagement::class.java)
        val token = tokensRepository.invalidate(accountId, name)
        token?.let { cache.invalidate(token) }
    }

    override fun revokeAllTokens(accountId: Int) {
        securityService.checkGlobalFunction(AccountManagement::class.java)
        val tokens = tokensRepository.invalidateAll(accountId)
        tokens.forEach { token ->
            cache.invalidate(token)
        }
    }
}