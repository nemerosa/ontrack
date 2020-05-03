package net.nemerosa.ontrack.extension.oidc

import net.nemerosa.ontrack.extension.api.UISecurityExtension
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.security.AccountService
import net.nemerosa.ontrack.model.security.ProvidedGroupsService
import net.nemerosa.ontrack.model.security.SecurityService
import org.springframework.security.config.web.servlet.HttpSecurityDsl
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component

@Component
class OIDCUISecurityExtension(
        extensionFeature: OIDCExtensionFeature,
        private val accountService: AccountService,
        private val securityService: SecurityService,
        private val providedGroupsService: ProvidedGroupsService
) : AbstractExtension(extensionFeature), UISecurityExtension {

    override fun configure(httpSecurityDsl: HttpSecurityDsl, successHandler: AuthenticationSuccessHandler) {
        // OAuth setup
        httpSecurityDsl.oauth2Login {
            loginPage = "/login"
            permitAll()
            authenticationSuccessHandler = successHandler
            userInfoEndpoint {
                oidcUserService = OntrackOidcUserService(accountService, securityService, providedGroupsService)
            }
        }
    }

}