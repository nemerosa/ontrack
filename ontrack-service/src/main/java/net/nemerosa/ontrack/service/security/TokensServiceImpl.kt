package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.model.security.*
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.model.structure.TokensService.Companion.DEFAULT_NAME
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import net.nemerosa.ontrack.repository.TokensRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.LocalDateTime

@Service
@Transactional
class TokensServiceImpl(
    private val tokensRepository: TokensRepository,
    private val securityService: SecurityService,
    private val tokenGenerator: TokenGenerator,
    private val ontrackConfigProperties: OntrackConfigProperties,
    private val accountService: AccountService
) : TokensService {

    @Deprecated("Will be removed in V5. Use named tokens")
    override val currentToken: Token?
        get() = getCurrentToken(DEFAULT_NAME)

    override fun getCurrentToken(name: String): Token? {
        // Gets the current account
        val account = securityService.currentAccount?.account
        // Gets the token of this account
        return account?.let {
            tokensRepository.getTokenForAccount(account.id(), name)
        }
    }

    @Deprecated("Will be removed in V5. Use token with options")
    override fun generateNewToken(userContext: UserContext): Token {
        return generateNewToken(
            userContext = userContext,
            options = TokenOptions(
                name = DEFAULT_NAME,
                scope = TokenScope.USER,
            )
        )
    }

    override fun generateNewToken(userContext: UserContext, options: TokenOptions): Token {
        // Checking if a token with the same name already exists
        val existing = tokensRepository.getTokenForAccount(userContext.id, options.name)
        if (existing != null) {
            throw TokenGenerationNameAlreadyExistsException(options.name)
        }
        // Generates a new token
        return securityService.asAdmin { generateToken(userContext.id, options) }
    }

    @Deprecated("Will be removed in V5. Use named token")
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

    private fun transientValidity() = ontrackConfigProperties.security.tokens.transientValidity
        .takeIf { !it.isZero }
        ?: OntrackConfigProperties.TokensProperties.DEFAULT_TRANSIENT_VALIDITY

    override fun generateToken(accountId: Int, options: TokenOptions): Token {
        securityService.checkGlobalFunction(AccountManagement::class.java)
        // Generates a new token
        val token = tokenGenerator.generateToken()
        // Validity
        val systemValidity = ontrackConfigProperties.security.tokens.validity
        val actualValidity = if (options.scope.transient) {
            transientValidity()
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
        tokensRepository.save(
            accountId,
            options.name,
            token,
            options.scope,
            tokenObject.creation,
            tokenObject.validUntil
        )
        // ... and returns it
        return tokenObject
    }

    @Deprecated("Will be removed in V5. Use named tokens")
    override fun revokeToken() {
        revokeToken(DEFAULT_NAME)
    }

    override fun revokeToken(name: String) {
        // Gets the current account
        val account = securityService.currentAccount?.account
        // Revokes its token
        account?.apply {
            tokensRepository.invalidate(id(), name)
        }
    }

    @Deprecated("Will be removed in V5. Use list of tokens")
    override fun getToken(account: Account): Token? {
        securityService.checkGlobalFunction(AccountManagement::class.java)
        return tokensRepository.getTokenForAccount(account.id(), DEFAULT_NAME)
    }

    override fun getTokens(account: Account): List<Token> {
        securityService.checkGlobalFunction(AccountManagement::class.java)
        return tokensRepository.getTokens(account)
    }

    @Deprecated("Will be removed in V5. Use list of tokens")
    override fun getToken(accountId: Int): Token? {
        return getToken(accountService.getAccount(ID.of(accountId)))
    }

    override fun getTokens(accountId: Int): List<Token> {
        return getTokens(accountService.getAccount(ID.of(accountId)))
    }

    override fun isValid(token: String): Boolean = tokensRepository
        .findAccountByToken(token)
        ?.let { (_, result) ->
            result.isValid()
        }
        ?: false

    override fun findAccountByToken(token: String, refTime: LocalDateTime): TokenAccount? {
        // Find the account ID
        val result = tokensRepository.findAccountByToken(token)
        return result?.let { (accountId, token) ->
            // Updating the "last used" date
            tokensRepository.updateLastUsed(token, refTime)
            // If transient token, increases the validity
            if (token.scope.transient) {
                tokensRepository.updateValidUntil(token, refTime + transientValidity())
            }
            // OK, returning the account AND the token
            TokenAccount(
                securityService.asAdmin {
                    accountService.getAccount(ID.of(accountId))
                },
                token.withLastUsed(refTime)
            )
        }
    }

    override fun revokeAll(): Int {
        securityService.checkGlobalFunction(AccountManagement::class.java)
        return tokensRepository.revokeAll()
    }

    @Deprecated("Will be removed in V5. Use named tokens")
    override fun revokeToken(accountId: Int) {
        revokeToken(accountId, DEFAULT_NAME)
    }

    override fun revokeToken(accountId: Int, name: String) {
        securityService.checkGlobalFunction(AccountManagement::class.java)
        tokensRepository.invalidate(accountId, name)
    }

    override fun revokeAllTokens(accountId: Int) {
        securityService.checkGlobalFunction(AccountManagement::class.java)
        tokensRepository.invalidateAll(accountId)
    }
}