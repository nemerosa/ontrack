package net.nemerosa.ontrack.extension.oidc

import net.nemerosa.ontrack.extension.oidc.settings.OIDCSettingsService
import net.nemerosa.ontrack.model.security.AuthenticationSource
import net.nemerosa.ontrack.model.security.AuthenticationSourceProvider
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest
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
        fun asSource(userRequest: OidcUserRequest): AuthenticationSource {
            return AuthenticationSource(
                    provider = ID,
                    key = userRequest.clientRegistration.registrationId,
                    name = userRequest.clientRegistration.clientName
            )
        }

        const val ID = "oidc"
    }

}