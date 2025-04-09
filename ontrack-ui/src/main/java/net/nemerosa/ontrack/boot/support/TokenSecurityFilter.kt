package net.nemerosa.ontrack.boot.support

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import net.nemerosa.ontrack.model.structure.TokensService
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class TokenSecurityFilter(
    private val tokensService: TokensService,
) : OncePerRequestFilter() {

    companion object {
        const val HEADER = "X-Ontrack-Token"
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val token = request.getHeader(HEADER)
        if (!token.isNullOrBlank()) {
            val tokenAccount = tokensService.findAccountByToken(token)
            if (tokenAccount == null || !tokenAccount.token.valid) {
                response.status = HttpServletResponse.SC_UNAUTHORIZED
                response.writer.write("Invalid API token")
                return
            } else {
                val authentication = TokenAuthenticationToken(
                    token = tokenAccount.token.value,
                    account = tokenAccount.account
                )
                SecurityContextHolder.getContext().authentication = authentication
            }
        }
        filterChain.doFilter(request, response)
    }

}