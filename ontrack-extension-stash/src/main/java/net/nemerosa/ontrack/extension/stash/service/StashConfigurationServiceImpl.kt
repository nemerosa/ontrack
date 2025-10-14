package net.nemerosa.ontrack.extension.stash.service

import net.nemerosa.ontrack.extension.stash.client.BitbucketClientFactory
import net.nemerosa.ontrack.extension.stash.model.StashConfiguration
import net.nemerosa.ontrack.extension.support.AbstractConfigurationService
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
        private val bitbucketClientFactory: BitbucketClientFactory,
        ontrackConfigProperties: OntrackConfigProperties
) : AbstractConfigurationService<StashConfiguration>(
    configurationClass = StashConfiguration::class.java,
    configurationRepository = configurationRepository,
    securityService = securityService,
    encryptionService = encryptionService,
    eventPostService = eventPostService,
    eventFactory = eventFactory,
    ontrackConfigProperties = ontrackConfigProperties
), StashConfigurationService {

    override val type: String = "bitbucket-server"

    override fun validate(configuration: StashConfiguration): ConnectionResult {
        return try {
            val client = bitbucketClientFactory.getBitbucketClient(configuration)
            client.projects
            ConnectionResult.ok()
        } catch (ex: Exception) {
            ConnectionResult.error(ex)
        }
    }

}