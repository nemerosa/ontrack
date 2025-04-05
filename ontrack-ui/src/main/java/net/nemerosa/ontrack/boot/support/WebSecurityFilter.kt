package net.nemerosa.ontrack.boot.support

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import net.nemerosa.ontrack.model.security.*
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class WebSecurityFilter(
    private val accountLoginService: AccountLoginService,
    private val accountACLService: AccountACLService,
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authentication = SecurityContextHolder.getContext().authentication
        if (authentication != null && authentication.isAuthenticated) {
            val account = when (authentication) {
                is JwtAuthenticationToken -> accountFromJwt(authentication)
                // TODO is ApiTokenAuthentication -> authentication.name  // Extracted email from your API token
                else -> null
            }
            if (account != null) {
                val enrichedAuth = AuthenticatedUserAuthentication(
                    authenticatedUser = createAuthenticatedUser(account),
                    authorities = AuthorityUtils.createAuthorityList(SecurityRole.USER.name)
                )
                SecurityContextHolder.getContext().authentication = enrichedAuth
            }
        }
        filterChain.doFilter(request, response)
    }

    private fun createAuthenticatedUser(account: Account): AccountAuthenticatedUser {
        return AccountAuthenticatedUser(
            account = account,
            authorisations = accountACLService.getAuthorizations(account),
            groups = accountACLService.getGroups(account),
        )
    }

    private fun accountFromJwt(jwtAuthenticationToken: JwtAuthenticationToken): Account? {
        val email = jwtAuthenticationToken.token.getClaim<String>("email")
        if (email.isNullOrBlank()) {
            return null
        } else {
            var fullName = jwtAuthenticationToken.token.getClaim<String>("name")
            if (fullName.isNullOrBlank()) {
                val givenName = jwtAuthenticationToken.token.getClaim<String>("given_name")
                val familyName = jwtAuthenticationToken.token.getClaim<String>("family_name")
                if (!givenName.isNullOrBlank() && !familyName.isNullOrBlank()) {
                    fullName = "$givenName $familyName"
                }
            }
            if (fullName.isNullOrBlank()) {
                fullName = email
            }
            return accountLoginService.login(email, fullName)
        }
    }
}