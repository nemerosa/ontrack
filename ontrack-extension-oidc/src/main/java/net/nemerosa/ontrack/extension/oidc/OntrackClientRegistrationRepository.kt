package net.nemerosa.ontrack.extension.oidc

import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientPropertiesRegistrationAdapter
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.stereotype.Component

@Component
class OntrackClientRegistrationRepository : ClientRegistrationRepository, Iterable<ClientRegistration> {

    private val registrations: Map<String, ClientRegistration>

    init {
        val properties = OAuth2ClientProperties()
        properties.provider["okta"] = OAuth2ClientProperties.Provider().apply {
            issuerUri = "https://dev-991108.okta.com/oauth2/default"
        }
        properties.registration["okta"] = OAuth2ClientProperties.Registration().apply {
            provider = "okta"
            clientName = "Okta"
            clientId = "0oa3prwngqXvuHmcJ357"
            clientSecret = "Ym7SFacOkH9ARisax5IpqVXhiW4m9GnFs6pRHV2J"
        }
        registrations = OAuth2ClientPropertiesRegistrationAdapter.getClientRegistrations(properties)
    }

    override fun findByRegistrationId(registrationId: String): ClientRegistration? {
        return registrations[registrationId]
    }

    override fun iterator(): Iterator<ClientRegistration> {
        return registrations.values.iterator()
    }
}