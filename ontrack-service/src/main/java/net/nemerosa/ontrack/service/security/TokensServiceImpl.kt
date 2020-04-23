package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.model.security.Account
import net.nemerosa.ontrack.model.security.SecurityService
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
        private val ontrackConfigProperties: OntrackConfigProperties
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
        // Saves the token...
        val time = Time.now()
        tokensRepository.save(account.id(), token, time)
        // ... and returns it
        return Token(token, time, null).validFor(ontrackConfigProperties.security.tokens.validity)
    }

    override fun getToken(account: Account): Token? {
        // Gets the raw token
        val token = tokensRepository.getForAccount(account)
        // Computes the validity date
        return token?.run { validFor(ontrackConfigProperties.security.tokens.validity) }
    }
}