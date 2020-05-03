package net.nemerosa.ontrack.extension.api

import net.nemerosa.ontrack.model.extension.Extension
import org.springframework.security.config.web.servlet.HttpSecurityDsl
import org.springframework.security.web.authentication.AuthenticationSuccessHandler

/**
 * Allows to customize the security settings for the UI.
 */
interface UISecurityExtension : Extension {

    /**
     * Extending the HTTP security for the UI.
     */
    fun configure(httpSecurityDsl: HttpSecurityDsl, successHandler: AuthenticationSuccessHandler)

}
