package net.nemerosa.ontrack.extension.oidc.settings

import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.ui.resource.*
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on

@Component
class OntrackOIDCProviderResourceDecorator(
        private val oidcSettingsService: OIDCSettingsService
) : AbstractLinkResourceDecorator<OntrackOIDCProvider>(OntrackOIDCProvider::class.java) {

    override fun getLinkDefinitions(): List<LinkDefinition<OntrackOIDCProvider>> = listOf(
            Link.DELETE linkTo { provider: OntrackOIDCProvider ->
                on(OIDCSettingsController::class.java).deleteProvider(provider.id)
            } linkIfGlobal GlobalSettings::class,
            Link.UPDATE linkTo { provider: OntrackOIDCProvider ->
                on(OIDCSettingsController::class.java).getUpdateForm(provider.id)
            } linkIfGlobal GlobalSettings::class,
            Link.IMAGE_LINK linkTo { provider: OntrackOIDCProvider ->
                on(OIDCSettingsController::class.java).getProviderImage(null, provider.id)
            } linkIf { provider, _ ->
                oidcSettingsService.hasProviderImage(provider.id)
            }
    )

}