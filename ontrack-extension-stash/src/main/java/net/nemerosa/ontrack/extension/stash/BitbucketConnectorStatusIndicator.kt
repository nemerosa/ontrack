package net.nemerosa.ontrack.extension.stash

import net.nemerosa.ontrack.extension.stash.model.StashConfiguration
import net.nemerosa.ontrack.extension.stash.service.StashConfigurationService
import net.nemerosa.ontrack.extension.support.ConfigurationConnectorStatusIndicator
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.support.ConnectionResult
import net.nemerosa.ontrack.model.support.Connector
import net.nemerosa.ontrack.model.support.ConnectorDescription
import org.springframework.stereotype.Component

@Component
class BitbucketConnectorStatusIndicator(
        private val configurationService: StashConfigurationService,
        securityService: SecurityService
) : ConfigurationConnectorStatusIndicator<StashConfiguration>(configurationService, securityService) {

    override fun connect(config: StashConfiguration) {
        val result = configurationService.test(config)
        if (result.type != ConnectionResult.ConnectionResultType.OK) {
            throw IllegalStateException(result.message)
        }
    }

    override fun connectorDescription(config: StashConfiguration) = ConnectorDescription(
            connector = Connector(type, config.name),
            connection = config.url
    )

    override val type: String = "bitbucket"
}