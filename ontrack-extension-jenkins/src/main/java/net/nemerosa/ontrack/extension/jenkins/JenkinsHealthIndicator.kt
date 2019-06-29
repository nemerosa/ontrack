package net.nemerosa.ontrack.extension.jenkins

import net.nemerosa.ontrack.extension.jenkins.client.JenkinsClientFactory
import net.nemerosa.ontrack.extension.support.ConfigurationConnectorStatusIndicator
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.support.ConfigurationService
import net.nemerosa.ontrack.model.support.ConnectorDescription
import net.nemerosa.ontrack.model.support.ConnectorStatus
import org.springframework.stereotype.Component

@Component
class JenkinsHealthIndicator(
        configurationService: ConfigurationService<JenkinsConfiguration>,
        securityService: SecurityService,
        private val jenkinsClientFactory: JenkinsClientFactory
) : ConfigurationConnectorStatusIndicator<JenkinsConfiguration>(configurationService, securityService) {

    override fun getConnectorStatus(config: JenkinsConfiguration): ConnectorStatus {
        return try {
            jenkinsClientFactory.getClient(config).info
            ConnectorStatus.ok(connectorDescription(config))
        } catch (ex: Exception) {
            ConnectorStatus.error(connectorDescription(config), ex)
        }

    }

    private fun connectorDescription(config: JenkinsConfiguration) = ConnectorDescription(
            type = "jenkins",
            name = config.name,
            connection = config.url
    )
}
