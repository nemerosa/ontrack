package net.nemerosa.ontrack.boot.support

import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.security.web.savedrequest.HttpSessionRequestCache
import org.springframework.security.web.savedrequest.RequestCache
import org.springframework.util.Base64Utils
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


class LoginSuccessHandler : SimpleUrlAuthenticationSuccessHandler() {

    private val requestCache: RequestCache = HttpSessionRequestCache()

    companion object {
        /**
         * Name of the cookie which contains the hash part to add to the URL
         *
         * See ontrack-ui/src/main/resources/templates/login.html
         */
        const val COOKIE_HASH_PART = "hashPart"
    }

    override fun determineTargetUrl(request: HttpServletRequest, response: HttpServletResponse): String {
        val savedRequest = requestCache.getRequest(request, response)

        // Base URL
        var targetUrl = savedRequest
                ?.redirectUrl
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