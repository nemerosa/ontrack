package net.nemerosa.ontrack.extension.sonarqube

import net.nemerosa.ontrack.extension.sonarqube.configuration.SonarQubeConfiguration
import net.nemerosa.ontrack.extension.sonarqube.configuration.SonarQubeConfigurationService
import net.nemerosa.ontrack.extension.support.ConfigurationConnectorStatusIndicator
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.support.ConnectionResult
import net.nemerosa.ontrack.model.support.Connector
import net.nemerosa.ontrack.model.support.ConnectorDescription
import org.springframework.stereotype.Component

@Component
class SonarQubeConnectorStatusIndicator(
        private val configurationService: SonarQubeConfigurationService,
        securityService: SecurityService
) : ConfigurationConnectorStatusIndicator<SonarQubeConfiguration>(configurationService, securityService) {

    override fun connect(config: SonarQubeConfiguration) {
        val result = configurationService.test(config)
        if (result.type != ConnectionResult.ConnectionResultType.OK) {
            throw IllegalStateException(result.message)
        }
    }

    override fun connectorDescription(config: SonarQubeConfiguration) = ConnectorDescription(
            connector = Connector(type, config.name),
            connection = config.url
    )

    override val type: String = "sonarqube"
}