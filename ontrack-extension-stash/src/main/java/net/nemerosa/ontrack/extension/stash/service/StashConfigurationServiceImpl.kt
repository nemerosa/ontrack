package net.nemerosa.ontrack.extension.stash.service

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.stash.model.StashConfiguration
import net.nemerosa.ontrack.extension.support.AbstractConfigurationService
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.model.events.EventPostService
import net.nemerosa.ontrack.model.security.EncryptionService
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.support.ConfigurationRepository
import net.nemerosa.ontrack.model.support.ConnectionResult
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForObject

@Service
@Transactional
class StashConfigurationServiceImpl(
    configurationRepository: ConfigurationRepository,
    securityService: SecurityService,
    encryptionService: EncryptionService,
    eventPostService: EventPostService,
    eventFactory: EventFactory,
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

    override val type: String = "bitbucket-server"

    override fun validate(configuration: StashConfiguration): ConnectionResult {
        return try {
            val client = getHttpClient(configuration)
            client.getForObject<JsonNode>("/projects")
            ConnectionResult.ok()
        } catch (ex: Exception) {
            ConnectionResult.error(ex)
        }
    }

    private fun getHttpClient(configuration: StashConfiguration): RestTemplate =
        RestTemplateBuilder()
            .rootUri(configuration.url)
            .basicAuthentication(configuration.user, configuration.password)
            .build()

}