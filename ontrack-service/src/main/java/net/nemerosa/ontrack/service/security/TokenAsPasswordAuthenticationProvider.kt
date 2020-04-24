package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.model.security.AccountService
import net.nemerosa.ontrack.model.structure.TokensService
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication

class TokenAsPasswordAuthenticationProvider(
        private val tokensService: TokensService,
        private val accountService: AccountService,
        private val ontrackConfigProperties: OntrackConfigProperties
) : AuthenticationProvider {

    override fun authenticate(authentication: Authentication): Authentication? {
        return if (authentication is UsernamePasswordAuthenticationToken && ontrackConfigProperties.security.tokens.password) {
            val token = authentication.credentials?.toString() ?: ""
            val tokenAccount = tokensService.findAccountByToken(token)
            if (tokenAccount != null) {
                if (tokenAccount.account.name != authentication.name) {
                    throw TokenNameMismatchException()
                } else {
                    // Validity of the token
                    val tokenValid = tokenAccount.token.isValid()
                    // Wrapping the account
                    val user = AccountOntrackUser(tokenAccount.account, credentialNonExpired = tokenValid)
                    // Provides the ACL
                    val authenticatedUser = accountService.withACL(user)
                    // Authentication OK
                    UsernamePasswordAuthenticationToken(
                            authenticatedUser,
                            "", // Remove the token
                            user.authorities
                    )
                }
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
            UsernamePasswordAuthenticationToken::class.java.isAssignableFrom(authentication)
}