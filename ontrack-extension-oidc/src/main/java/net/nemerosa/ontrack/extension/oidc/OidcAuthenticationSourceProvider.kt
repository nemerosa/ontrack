package net.nemerosa.ontrack.extension.oidc

import net.nemerosa.ontrack.extension.oidc.settings.OIDCSettingsService
import net.nemerosa.ontrack.model.security.AuthenticationSource
import net.nemerosa.ontrack.model.security.AuthenticationSourceProvider
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.stereotype.Component

@Component
class OidcAuthenticationSourceProvider(
        private val oidcSettingsService: OIDCSettingsService
) : AuthenticationSourceProvider {

    override val id: String = ID

    override val sources: List<AuthenticationSource>
        get() = oidcSettingsService.cachedProviderNames.map {
            AuthenticationSource(
                    provider = ID,
                    key = it.name,
                    name = it.description ?: it.name,
                    isEnabled = true,
                    isAllowingPasswordChange = false,
                    isGroupMappingSupported = true
            )
        }

    companion object {
        fun asSource(clientRegistration: OntrackClientRegistration) = AuthenticationSource(
                provider = ID,
                key = clientRegistration.registrationId,
                name = clientRegistration.clientName
        )

        const val ID = "oidc"
    }

}