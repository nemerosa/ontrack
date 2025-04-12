package net.nemerosa.ontrack.boot.support

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import net.nemerosa.ontrack.model.security.*
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class WebSecurityFilter(
    private val ontrackConfigProperties: OntrackConfigProperties,
    private val accountLoginService: AccountLoginService,
    private val accountACLService: AccountACLService,
) : OncePerRequestFilter() {

    private val log: Logger = LoggerFactory.getLogger(WebSecurityFilter::class.java)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authentication = SecurityContextHolder.getContext().authentication
        if (authentication != null && authentication.isAuthenticated) {
            val account = when (authentication) {
                is JwtAuthenticationToken -> accountFromJwt(authentication)
                is TokenAuthenticationToken -> accountFromToken(authentication)
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

    private fun accountFromToken(authentication: TokenAuthenticationToken): Account =
        authentication.account

    private fun createAuthenticatedUser(account: Account): AccountAuthenticatedUser {
        return AccountAuthenticatedUser(
            account = account,
            authorisations = accountACLService.getAuthorizations(account),
            groups = accountACLService.getGroups(account),
        )
    }

    private fun accountFromJwt(jwtAuthenticationToken: JwtAuthenticationToken): Account? {
        val debug = ontrackConfigProperties.authorization.jwt.debug
        val email = getClaim(
            jwtAuthenticationToken,
            defaultClaimName = "email",
            customClaimName = ontrackConfigProperties.authorization.jwt.claims.email,
        )
        if (debug) log.debug("JWT email {}", email)
        if (email.isNullOrBlank()) {
            if (debug) log.debug("JWT email not set - not authenticated")
            return null
        } else {
            var fullName = jwtAuthenticationToken.token.getClaim<String>("name")
            if (debug) log.debug("JWT full name {}", fullName)
            if (fullName.isNullOrBlank()) {
                val givenName = jwtAuthenticationToken.token.getClaim<String>("given_name")
                val familyName = jwtAuthenticationToken.token.getClaim<String>("family_name")
                if (debug) log.debug("JWT given name {}", givenName)
                if (debug) log.debug("JWT family name {}", familyName)
                if (!givenName.isNullOrBlank() && !familyName.isNullOrBlank()) {
                    fullName = "$givenName $familyName"
                }
            }
            if (fullName.isNullOrBlank()) {
                if (debug) log.debug("JWT no name found - using email")
                fullName = email
            }
            if (debug) log.debug("JWT full name {}", fullName)
            return accountLoginService.login(email, fullName)
        }
    }

    private fun getClaim(
        jwtAuthenticationToken: JwtAuthenticationToken,
        defaultClaimName: String,
        customClaimName: String? = null,
    ): String? {
        val value: String? = jwtAuthenticationToken.token.getClaim<String>(defaultClaimName)
        return if (value.isNullOrBlank()) {
            if (!customClaimName.isNullOrBlank()) {
                jwtAuthenticationToken.token.getClaim<String>(customClaimName)
            } else {
                null
            }
        } else {
            null
        }
    }
}