package net.nemerosa.ontrack.extension.jenkins

import net.nemerosa.ontrack.extension.jenkins.client.JenkinsClientFactory
import net.nemerosa.ontrack.extension.support.ConfigurationConnectorStatusIndicator
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.support.ConfigurationService
import net.nemerosa.ontrack.model.support.Connector
import net.nemerosa.ontrack.model.support.ConnectorDescription
import org.springframework.stereotype.Component

@Component
class JenkinsConnectorStatusIndicator(
        configurationService: ConfigurationService<JenkinsConfiguration>,
        securityService: SecurityService,
        private val jenkinsClientFactory: JenkinsClientFactory
) : ConfigurationConnectorStatusIndicator<JenkinsConfiguration>(configurationService, securityService) {

    override val type: String = "jenkins"

    override fun connect(config: JenkinsConfiguration) {
        jenkinsClientFactory.getClient(config).info
    }

    override fun connectorDescription(config: JenkinsConfiguration) = ConnectorDescription(
            connector = Connector(type, config.name),
            connection = config.url
    )
}
