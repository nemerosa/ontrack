package net.nemerosa.ontrack.extension.oidc

import net.nemerosa.ontrack.extension.oidc.settings.OIDCSettingsService
import net.nemerosa.ontrack.extension.oidc.settings.OntrackOIDCProvider
import net.nemerosa.ontrack.model.security.SecurityService
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientPropertiesRegistrationAdapter
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.stereotype.Component

@Component
class OntrackClientRegistrationRepository(
        private val oidcSettingsService: OIDCSettingsService,
        private val securityService: SecurityService
) : ClientRegistrationRepository, Iterable<ClientRegistration> {

    private val registrations: Map<String, ClientRegistration>
        get() = securityService.asAdmin {
            toRegistrations(oidcSettingsService.cachedProviders)
        }

    private fun toRegistrations(providers: List<OntrackOIDCProvider>): Map<String, ClientRegistration> {
        val properties = OAuth2ClientProperties()
        providers.forEach { settings ->
            properties.provider[settings.id] = OAuth2ClientProperties.Provider().apply {
                issuerUri = settings.issuerId
            }
            properties.registration[settings.id] = OAuth2ClientProperties.Registration().apply {
                provider = settings.id
                clientName = settings.name
                clientId = settings.clientId
                clientSecret = settings.clientSecret
            }
        }
        return OAuth2ClientPropertiesRegistrationAdapter.getClientRegistrations(properties)
    }

    override fun findByRegistrationId(registrationId: String): ClientRegistration? {
        return registrations[registrationId]
    }

    override fun iterator(): Iterator<ClientRegistration> {
        return registrations.values.iterator()
    }

    companion object {
        /**
         * Cache unique key
         */
        private const val CACHE_KEY = "0"
    }
}