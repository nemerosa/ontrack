package net.nemerosa.ontrack.extension.oidc

import net.nemerosa.ontrack.extension.api.UILogin
import net.nemerosa.ontrack.extension.api.UILoginExtension
import net.nemerosa.ontrack.extension.oidc.settings.OIDCSettingsService
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.security.SecurityService
import org.springframework.stereotype.Component

@Component
class OIDCUILoginExtension(
        extensionFeature: OIDCExtensionFeature,
        private val oidcSettingsService: OIDCSettingsService,
        private val securityService: SecurityService
) : AbstractExtension(extensionFeature), UILoginExtension {

    override val contributions: List<UILogin>
        get() = securityService.asAdmin {
            oidcSettingsService.providers.map { provider ->
                UILogin(
                        id = provider.id,
                        link = "/oauth2/authorization/${provider.id}",
                        name = provider.name
                )
            }
        }
}