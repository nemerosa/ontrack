package net.nemerosa.ontrack.extension.stash.service

import net.nemerosa.ontrack.client.OTHttpClient
import net.nemerosa.ontrack.client.ResponseParser
import net.nemerosa.ontrack.extension.stash.model.StashConfiguration
import net.nemerosa.ontrack.extension.support.AbstractConfigurationService
import net.nemerosa.ontrack.extension.support.client.ClientConnection
import net.nemerosa.ontrack.extension.support.client.ClientFactory
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.model.events.EventPostService
import net.nemerosa.ontrack.model.security.EncryptionService
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.support.ConfigurationRepository
import net.nemerosa.ontrack.model.support.ConnectionResult
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class StashConfigurationServiceImpl(
        configurationRepository: ConfigurationRepository,
        securityService: SecurityService,
        encryptionService: EncryptionService,
        eventPostService: EventPostService,
        eventFactory: EventFactory,
        private val clientFactory: ClientFactory,
        ontrackConfigProperties: OntrackConfigProperties
) : AbstractConfigurationService<StashConfiguration>(
        StashConfiguration::class.java,
        configurationRepository,
        securityService,
        encryptionService,
        eventPostService,
        eventFactory,
        ontrackConfigProperties
), StashConfigurationService {

    override fun validate(configuration: StashConfiguration): ConnectionResult {
        return try {
            val client = getHttpClient(configuration)
            if (client.get(ResponseParser { _: String? -> true }, "projects")) {
                ConnectionResult.ok()
            } else {
                ConnectionResult.error("Cannot get the content of the Stash home page")
            }
        } catch (ex: Exception) {
            ConnectionResult.error(ex.message)
        }
    }

    private fun getHttpClient(configuration: StashConfiguration): OTHttpClient = clientFactory.getHttpClient(
            ClientConnection(
                    configuration.url,
                    configuration.user,
                    configuration.password
            )
    )

}