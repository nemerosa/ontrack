package net.nemerosa.ontrack.service.security

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.model.security.Account
import net.nemerosa.ontrack.model.security.AccountManagement
import net.nemerosa.ontrack.model.security.AccountService
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.*
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

    override val currentToken: Token?
        get() {
            // Gets the current account
            val account = securityService.currentAccount?.account
            // Gets the token of this account
            return account?.let {
                tokensRepository.getForAccount(account)
            }
        }

    override fun generateNewToken(validity: Duration?): Token {
        // Gets the current account
        val account = securityService.currentAccount?.account
                ?: throw TokenGenerationNoAccountException()
        // Generates a new token
        val token = tokenGenerator.generateToken()
        val time = Time.now()
        // Gets the validity
        val systemValidity = ontrackConfigProperties.security.tokens.validity
        val actualValidity = if (systemValidity.isNegative || systemValidity.isZero) {
            validity
        } else if (validity != null) {
            // Checks that the validity is <= system validity
            check(validity <= systemValidity) { "The validity must be lower or equal to the system validity." }
            validity
        } else {
            throw IllegalStateException("Cannot set an unlimited validity.")
        }
        // Token object
        val tokenObject = Token(token, time, null).validFor(actualValidity)
        // Saves the token...
        tokensRepository.save(account.id(), token, tokenObject.creation, tokenObject.validUntil)
        // ... and returns it
        return tokenObject
    }

    override fun revokeToken() {
        // Gets the current account
        val account = securityService.currentAccount?.account
        // Revokes its token
        account?.apply {
            val token = tokensRepository.invalidate(id())
            // Removes any cache token
            token?.let { cache.invalidate(token) }
        }
    }

    override fun getToken(account: Account): Token? {
        securityService.checkGlobalFunction(AccountManagement::class.java)
        return tokensRepository.getForAccount(account)
    }

    override fun getToken(accountId: Int): Token? {
        return getToken(accountService.getAccount(ID.of(accountId)))
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
            TokenAccount(
                    securityService.asAdmin {
                        accountService.getAccount(ID.of(accountId))
                    },
                    token
            )
        }
    }

    override fun revokeAll(): Int {
        securityService.checkGlobalFunction(AccountManagement::class.java)
        cache.invalidateAll()
        return tokensRepository.revokeAll()
    }

    override fun revokeToken(accountId: Int) {
        securityService.checkGlobalFunction(AccountManagement::class.java)
        val token = tokensRepository.invalidate(accountId)
        token?.let { cache.invalidate(token) }
    }
}