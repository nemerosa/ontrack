package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.TokenGenerator
import net.nemerosa.ontrack.model.structure.TokensService
import net.nemerosa.ontrack.repository.TokensRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class TokensServiceImpl(
        private val tokensRepository: TokensRepository,
        private val securityService: SecurityService,
        private val tokenGenerator: TokenGenerator,
        private val passwordEncoder: PasswordEncoder
) : TokensService {

    override fun generateNewToken(): String {
        // Gets the current account
        val account = securityService.currentAccount?.account
                ?: throw TokenGenerationNoAccountException()
        // Generates a new token
        val token = tokenGenerator.generateToken()
        // Encodes the token
        val encodedToken = passwordEncoder.encode(token)
        // Saves the token...
        tokensRepository.save(account.id(), encodedToken, Time.now())
        // ... and returns it
        return token
    }
}