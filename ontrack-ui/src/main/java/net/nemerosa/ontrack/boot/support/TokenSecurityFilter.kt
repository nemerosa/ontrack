package net.nemerosa.ontrack.boot.support

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import net.nemerosa.ontrack.model.structure.TokensConstants
import net.nemerosa.ontrack.model.structure.TokensService
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class TokenSecurityFilter(
    private val tokensService: TokensService,
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val token = request.getHeader(TokensConstants.HTTP_ONTRACK_TOKEN)
        if (!token.isNullOrBlank()) {
            val success = tokensService.useTokenForSecurityContext(token)
            if (!success) {
                response.status = HttpServletResponse.SC_UNAUTHORIZED
                response.writer.write("Invalid API token")
                return
            }
        }
        filterChain.doFilter(request, response)
    }

}