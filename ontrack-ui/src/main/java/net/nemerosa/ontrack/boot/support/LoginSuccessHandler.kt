package net.nemerosa.ontrack.boot.support

import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.util.Base64Utils
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


class LoginSuccessHandler : SimpleUrlAuthenticationSuccessHandler() {

    companion object {
        /**
         * Name of the cookie which contains the hash part to add to the URL
         *
         * See ontrack-ui/src/main/resources/templates/login.html
         */
        const val COOKIE_HASH_PART = "hashPart"
    }

    override fun determineTargetUrl(request: HttpServletRequest, response: HttpServletResponse): String {
        var targetUrl = super.determineTargetUrl(request, response)
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
        logger.debug("Login target URL = $targetUrl")
        return targetUrl
    }

}