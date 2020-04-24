package net.nemerosa.ontrack.boot.support

import net.nemerosa.ontrack.model.security.TokenAuthenticationToken
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.authentication.NullRememberMeServices
import org.springframework.security.web.authentication.RememberMeServices
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Processes a HTTP request `X-Ontrack-Token` header.
 */
class TokenHeaderAuthenticationFilter(
        private val authenticationManager: AuthenticationManager,
        private val headerName: String = "X-Ontrack-Token",
        private val authenticationEntryPoint: AuthenticationEntryPoint? = null,
        private val rememberMeServices: RememberMeServices = NullRememberMeServices(),
        private val isIgnoreFailure: Boolean = false
) : OncePerRequestFilter() {

    override fun afterPropertiesSet() {
        if (!isIgnoreFailure) {
            checkNotNull(authenticationEntryPoint) { "An AuthenticationEntryPoint is required" }
        }
    }

    override fun doFilterInternal(request: HttpServletRequest,
                                  response: HttpServletResponse, chain: FilterChain) {
        val debug = logger.isDebugEnabled
        try {
            val token: String? = request.getHeader(headerName)
            if (token.isNullOrBlank()) {
                chain.doFilter(request, response)
                return
            }
            if (authenticationIsRequired(token)) {
                val authRequest = TokenAuthenticationToken(token)
                val authResult = authenticationManager.authenticate(authRequest)
                if (debug) {
                    logger.debug("Authentication success: $authResult")
                }
                SecurityContextHolder.getContext().authentication = authResult
                rememberMeServices.loginSuccess(request, response, authResult)
            }
        } catch (failed: AuthenticationException) {
            SecurityContextHolder.clearContext()
            if (debug) {
                logger.debug("Authentication request for failed!", failed)
            }
            rememberMeServices.loginFail(request, response)
            if (isIgnoreFailure) {
                chain.doFilter(request, response)
            } else {
                authenticationEntryPoint?.apply { commence(request, response, failed) }
            }
            return
        }
        chain.doFilter(request, response)
    }

    private fun authenticationIsRequired(token: String): Boolean {

        // Only reauthenticate if token does not match the digest
        val existingAuth = SecurityContextHolder.getContext().authentication
        if (existingAuth == null || !existingAuth.isAuthenticated) {
            return true
        }

        // Only reauthenticate if token does not match the digest
        if (existingAuth is TokenAuthenticationToken
                && !existingAuth.matches(token)) {
            return true
        }

        // OK
        return false
    }

}