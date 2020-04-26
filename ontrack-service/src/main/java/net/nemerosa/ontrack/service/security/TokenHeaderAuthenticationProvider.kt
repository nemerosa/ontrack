package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.model.security.AccountOntrackUser
import net.nemerosa.ontrack.model.security.AccountService
import net.nemerosa.ontrack.model.security.TokenAuthenticationToken
import net.nemerosa.ontrack.model.structure.TokensService
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.CredentialsExpiredException
import org.springframework.security.core.Authentication

class TokenHeaderAuthenticationProvider(
        private val tokensService: TokensService,
        private val accountService: AccountService
) : AuthenticationProvider {

    override fun authenticate(authentication: Authentication): Authentication? {
        return if (authentication is TokenAuthenticationToken) {
            val token = authentication.credentials.toString()
            val tokenAccount = tokensService.findAccountByToken(token)
            if (tokenAccount != null) {
                // Validity of the token
                val tokenValid = tokenAccount.token.isValid()
                if (!tokenValid) {
                    throw CredentialsExpiredException("Token is expired.")
                }
                // Wrapping the account
                val user = AccountOntrackUser(tokenAccount.account)
                // Provides the ACL
                val authenticatedUser = accountService.withACL(user)
                // Authentication OK
                TokenAuthenticationToken(
                        token = token,
                        authorities = user.authorities,
                        principal = authenticatedUser
                )
            } else {
                // No account linked to this token, not failing on this since
                // this token could be picked by another provider
                null
            }
        } else {
            // Not a username / password
            null
        }
    }

    override fun supports(authentication: Class<*>): Boolean =
            TokenAuthenticationToken::class.java.isAssignableFrom(authentication)
}