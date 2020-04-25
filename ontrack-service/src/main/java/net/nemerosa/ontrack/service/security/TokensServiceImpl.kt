package net.nemerosa.ontrack.service.security

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

@Service
@Transactional
class TokensServiceImpl(
        private val tokensRepository: TokensRepository,
        private val securityService: SecurityService,
        private val tokenGenerator: TokenGenerator,
        private val ontrackConfigProperties: OntrackConfigProperties,
        private val accountService: AccountService
) : TokensService {

    override val currentToken: Token?
        get() {
            // Gets the current account
            val account = securityService.currentAccount?.account
            // Gets the token of this account
            return account?.let {
                tokensRepository.getForAccount(account)
            }
        }

    override fun generateNewToken(): Token {
        // Gets the current account
        val account = securityService.currentAccount?.account
                ?: throw TokenGenerationNoAccountException()
        // Generates a new token
        val token = tokenGenerator.generateToken()
        val time = Time.now()
        // Token object
        val tokenObject = Token(token, time, null).validFor(ontrackConfigProperties.security.tokens.validity)
        // Saves the token...
        tokensRepository.save(account.id(), token, tokenObject.creation, tokenObject.validUntil)
        // ... and returns it
        return tokenObject
    }

    override fun revokeToken() {
        // Gets the current account
        val account = securityService.currentAccount?.account
        // Revokes its token
        account?.apply { tokensRepository.invalidate(id()) }
    }

    override fun getToken(account: Account): Token? {
        securityService.checkGlobalFunction(AccountManagement::class.java)
        return tokensRepository.getForAccount(account)
    }

    override fun getToken(accountId: Int): Token? {
        return getToken(accountService.getAccount(ID.of(accountId)))
    }

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
        return tokensRepository.revokeAll()
    }

    override fun revokeToken(accountId: Int) {
        securityService.checkGlobalFunction(AccountManagement::class.java)
        tokensRepository.invalidate(accountId)
    }
}