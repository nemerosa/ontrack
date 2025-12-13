package net.nemerosa.ontrack.extension.artifactory.configuration

import net.nemerosa.ontrack.extension.artifactory.client.ArtifactoryClientFactory
import net.nemerosa.ontrack.extension.support.AbstractConfigurationService
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.model.events.EventPostService
import net.nemerosa.ontrack.model.security.EncryptionService
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.support.ConfigurationRepository
import net.nemerosa.ontrack.model.support.ConnectionResult
import net.nemerosa.ontrack.model.support.ConnectionResult.Companion.ok
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ArtifactoryConfigurationServiceImpl(
    configurationRepository: ConfigurationRepository,
    securityService: SecurityService,
    encryptionService: EncryptionService,
    eventPostService: EventPostService,
    eventFactory: EventFactory,
    private val artifactoryClientFactory: ArtifactoryClientFactory,
    ontrackConfigProperties: OntrackConfigProperties
) : AbstractConfigurationService<ArtifactoryConfiguration>(
    ArtifactoryConfiguration::class.java,
    configurationRepository,
    securityService,
    encryptionService,
    eventPostService,
    eventFactory,
    ontrackConfigProperties
), ArtifactoryConfigurationService {

    override val type: String = "artifactory"

    override fun validate(configuration: ArtifactoryConfiguration): ConnectionResult {
        try {
            val client = artifactoryClientFactory.getClient(configuration)
            // Gets the basic info
            client.buildNames
            // OK
            return ok()
        } catch (ex: Exception) {
            return ConnectionResult.error(ex.message!!)
        }
    }
}
