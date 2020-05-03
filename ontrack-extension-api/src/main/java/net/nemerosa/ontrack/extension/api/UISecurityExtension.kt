package net.nemerosa.ontrack.extension.api

import org.springframework.security.config.web.servlet.HttpSecurityDsl
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component

/**
 * Allows to customize the security settings for the UI.
 */
interface UISecurityExtension {

    /**
     * Extending the HTTP security for the UI.
     */
    fun configure(httpSecurityDsl: HttpSecurityDsl, successHandler: AuthenticationSuccessHandler)

}

@Component
class NOPUISecurityExtension : UISecurityExtension {
    override fun configure(httpSecurityDsl: HttpSecurityDsl, successHandler: AuthenticationSuccessHandler) {
    }
}