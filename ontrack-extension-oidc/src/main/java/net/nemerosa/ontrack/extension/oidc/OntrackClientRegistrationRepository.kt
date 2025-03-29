package net.nemerosa.ontrack.extension.oidc

import net.nemerosa.ontrack.extension.oidc.settings.OIDCSettingsListener
import net.nemerosa.ontrack.extension.oidc.settings.OIDCSettingsService
import net.nemerosa.ontrack.extension.oidc.settings.OntrackOIDCProvider
import net.nemerosa.ontrack.model.security.SecurityService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
@Deprecated("To be replaced by the external authentication")
final class OntrackClientRegistrationRepository(
        private val oidcSettingsService: OIDCSettingsService,
        private val securityService: SecurityService
) : ClientRegistrationRepository, Iterable<ClientRegistration>, OIDCSettingsListener {

    private val logger: Logger = LoggerFactory.getLogger(OntrackClientRegistrationRepository::class.java)

    init {
        logger.debug("Registering as a OIDC settings listener")
        oidcSettingsService.addOidcSettingsListener(this)
    }

    private val registrationsCache = ConcurrentHashMap<String, Map<String, ClientRegistration>>()

    internal val registrations: Map<String, ClientRegistration>
        get() = registrationsCache.getOrPut(CACHE_KEY) {
            val map = securityService.asAdmin {
                toRegistrations(oidcSettingsService.cachedProviders)
            }
            logger.debug("Loading OIDC registrations from OIDC settings: {}", map.keys)
            map
        }

    override fun onOidcSettingsChanged() {
        logger.debug("Forcing the load of OIDC registrations from the OIDC settings")
        // Forces a reload of the registrations
        registrationsCache.clear()
    }

    private fun toRegistrations(providers: List<OntrackOIDCProvider>): Map<String, ClientRegistration> {
        val properties = OAuth2ClientProperties()
        providers.filter { !it.disabled }.forEach { settings ->
            properties.provider[settings.id] = OAuth2ClientProperties.Provider().apply {
                issuerUri = settings.issuerId
            }
            properties.registration[settings.id] = OAuth2ClientProperties.Registration().apply {
                provider = settings.id
                clientName = settings.name
                clientId = settings.clientId
                clientSecret = settings.clientSecret
                // See https://github.com/spring-projects/spring-security/issues/8514
                scope = setOf("openid", "profile", "email")
                // Forcing HTTPS
                if (settings.forceHttps) {
                    redirectUri = "https://{baseHost}{basePort}{basePath}/{action}/oauth2/code/{registrationId}"
                }
            }
        }
        TODO("Not used any longer")
        // return OAuth2ClientPropertiesRegistrationAdapter.getClientRegistrations(properties)
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