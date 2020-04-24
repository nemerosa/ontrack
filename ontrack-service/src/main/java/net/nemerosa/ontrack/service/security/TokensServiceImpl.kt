package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.model.security.Account
import net.nemerosa.ontrack.model.security.AccountService
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.Token
import net.nemerosa.ontrack.model.structure.TokenGenerator
import net.nemerosa.ontrack.model.structure.TokensService
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
            return account?.let { getToken(it) }
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

    override fun getToken(account: Account): Token? = tokensRepository.getForAccount(account)

    override fun findAccountByToken(token: String): Account? {
        // Find the account ID
        val accountId = tokensRepository.findAccountByToken(token)
        // Loads the account
        return accountId?.let { id ->
            securityService.asAdmin {
                accountService.getAccount(ID.of(id))
            }
        }
    }
}