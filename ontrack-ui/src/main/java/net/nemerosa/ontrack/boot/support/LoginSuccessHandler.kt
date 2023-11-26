package net.nemerosa.ontrack.boot.support

import net.nemerosa.ontrack.model.structure.TokenOptions
import net.nemerosa.ontrack.model.structure.TokenScope
import net.nemerosa.ontrack.model.structure.TokensService
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.security.web.savedrequest.HttpSessionRequestCache
import org.springframework.security.web.savedrequest.RequestCache
import org.springframework.security.web.savedrequest.SavedRequest
import org.springframework.stereotype.Component
import org.springframework.util.Base64Utils
import org.springframework.web.filter.GenericFilterBean
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class LoginSavedRequestFilter: GenericFilterBean() {
    private val requestCache: RequestCache = HttpSessionRequestCache()

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        if (request is HttpServletRequest && response is HttpServletResponse) {
            if (request.servletPath.startsWith("/login")) {
                requestCache.saveRequest(request, response)
            }
        }
        chain.doFilter(request, response)
    }

}


class LoginSuccessHandler(
    private val tokensService: TokensService,
) : SimpleUrlAuthenticationSuccessHandler() {

    private val requestCache: RequestCache = HttpSessionRequestCache()

    init {
        targetUrlParameter = "targetUrl"
    }

    companion object {
        /**
         * Name of the cookie which contains the hash part to add to the URL
         *
         * See ontrack-ui/src/main/resources/templates/login.html
         */
        const val COOKIE_HASH_PART = "hashPart"

        /**
         * Token call back
         */
        const val PARAM_TOKEN = "token"
        const val PARAM_TOKEN_CALLBACK = "tokenCallback"

        const val PARAM_TOKEN_CALLBACK_TOKEN = "tokenCallbackToken"
        const val DEFAULT_TOKEN_CALLBACK_TOKEN = "token"

        const val PARAM_TOKEN_CALLBACK_HREF = "tokenCallbackHref"
        const val PARAM_TOKEN_CALLBACK_HREF_PARAM = "tokenCallbackHrefParam"
        const val DEFAULT_TOKEN_CALLBACK_HREF_PARAM = "href"
    }

    private fun SavedRequest.getParameter(name: String): String? = getParameterValues(name)?.firstOrNull()

    override fun determineTargetUrl(request: HttpServletRequest, response: HttpServletResponse): String {

        val savedRequest = requestCache.getRequest(request, response)

        // Token callback
        if (savedRequest != null) {
            val token = savedRequest.getParameter(PARAM_TOKEN)?.toBoolean() ?: false
            val tokenCallback: String? = savedRequest.getParameter(PARAM_TOKEN_CALLBACK)
            val tokenCallbackToken: String =
                savedRequest.getParameter(PARAM_TOKEN_CALLBACK_TOKEN) ?: DEFAULT_TOKEN_CALLBACK_TOKEN
            val tokenCallbackHref: String? = savedRequest.getParameter(PARAM_TOKEN_CALLBACK_HREF)
            val tokenCallbackHrefParam =
                savedRequest.getParameter(PARAM_TOKEN_CALLBACK_HREF_PARAM) ?: DEFAULT_TOKEN_CALLBACK_HREF_PARAM
            if (token && tokenCallback != null) {
                // Gets any existing token and deletes it
                val existing = tokensService.getCurrentToken(tokenCallback)
                if (existing != null) {
                    tokensService.revokeToken(tokenCallback)
                }
                // Generates a token for the logged user
                val generatedToken = tokensService.generateNewToken(
                    TokenOptions(
                        name = tokenCallback,
                        scope = TokenScope.NEXT_UI,
                    )
                )
                // Token value
                val tokenValue = generatedToken.value
                // Callback URL
                var url = "${tokenCallback}?${tokenCallbackToken}=$tokenValue"
                if (!tokenCallbackHref.isNullOrBlank()) {
                    url += "&${tokenCallbackHrefParam}=${tokenCallbackHref}"
                }
                // OK
                return url
            }
        }

        // Base URL
        var targetUrl = savedRequest
            ?.redirectUrl
            ?.takeIf { !it.contains("/login") }
            ?: super.determineTargetUrl(request, response)

        // Hash part
        for (cookie in request.cookies) {
            if (cookie.name == COOKIE_HASH_PART) {
                val value = cookie.value
                if (!value.isNullOrBlank()) {
                    val actualValue = String(Base64Utils.decodeFromString(value))
                    targetUrl += actualValue
                }
                cookie.maxAge = 0 // Clears cookie as no longer needed
                response.addCookie(cookie)
                break
            }
        }

        // URL to redirect to
        logger.debug("Login target URL = $targetUrl")
        return targetUrl
    }

}