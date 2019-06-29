package net.nemerosa.ontrack.extension.artifactory

import net.nemerosa.ontrack.extension.artifactory.client.ArtifactoryClientFactory
import net.nemerosa.ontrack.extension.artifactory.configuration.ArtifactoryConfiguration
import net.nemerosa.ontrack.extension.support.ConfigurationConnectorStatusIndicator
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.support.ConfigurationService
import net.nemerosa.ontrack.model.support.ConnectorDescription
import org.springframework.stereotype.Component

@Component
class ArtifactoryConnectorStatusIndicator(
        configurationService: ConfigurationService<ArtifactoryConfiguration>,
        securityService: SecurityService,
        private val clientFactory: ArtifactoryClientFactory
) : ConfigurationConnectorStatusIndicator<ArtifactoryConfiguration>(configurationService, securityService) {

    override fun connect(config: ArtifactoryConfiguration) {
        clientFactory.getClient(config).buildNames
    }

    override fun connectorDescription(config: ArtifactoryConfiguration) = ConnectorDescription(
            type = "artifactory",
            name = config.name,
            connection = config.url
    )

}
