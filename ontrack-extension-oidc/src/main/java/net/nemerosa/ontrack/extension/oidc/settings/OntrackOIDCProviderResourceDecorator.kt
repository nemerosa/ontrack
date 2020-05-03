package net.nemerosa.ontrack.extension.oidc.settings

import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.ui.resource.*
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on

@Component
class OntrackOIDCProviderResourceDecorator : AbstractLinkResourceDecorator<OntrackOIDCProvider>(OntrackOIDCProvider::class.java) {

    override fun getLinkDefinitions(): List<LinkDefinition<OntrackOIDCProvider>> = listOf(
            Link.DELETE linkTo { provider: OntrackOIDCProvider ->
                on(OIDCSettingsController::class.java).deleteProvider(provider.id)
            } linkIfGlobal GlobalSettings::class
    )

}