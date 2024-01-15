package net.nemerosa.ontrack.extension.tfc.config

import net.nemerosa.ontrack.extension.support.AbstractConfigurationService
import net.nemerosa.ontrack.extension.tfc.client.TFCClientFactory
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.model.events.EventPostService
import net.nemerosa.ontrack.model.security.EncryptionService
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.support.ConfigurationRepository
import net.nemerosa.ontrack.model.support.ConnectionResult
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import org.springframework.stereotype.Service

@Service
class TFCConfigurationServiceImpl(
    configurationRepository: ConfigurationRepository,
    securityService: SecurityService,
    encryptionService: EncryptionService,
    eventPostService: EventPostService,
    eventFactory: EventFactory,
    ontrackConfigProperties: OntrackConfigProperties,
    private val tfcClientFactory: TFCClientFactory,
) : AbstractConfigurationService<TFCConfiguration>(
    TFCConfiguration::class.java,
    configurationRepository,
    securityService,
    encryptionService,
    eventPostService,
    eventFactory,
    ontrackConfigProperties
), TFCConfigurationService {

    override fun findConfigurationByURL(url: String): TFCConfiguration? =
        configurations.find { url.startsWith(it.url) }

    override fun validate(configuration: TFCConfiguration): ConnectionResult {
        val client = tfcClientFactory.createClient(configuration)
        return try {
            client.organizations
            ConnectionResult.ok()
        } catch (any: Exception) {
            ConnectionResult.error(any)
        }
    }

}