package net.nemerosa.ontrack.extension.github

import net.nemerosa.ontrack.extension.github.client.OntrackGitHubClientFactory
import net.nemerosa.ontrack.extension.github.model.GitHubEngineConfiguration
import net.nemerosa.ontrack.extension.support.ConfigurationConnectorStatusIndicator
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.support.ConfigurationService
import net.nemerosa.ontrack.model.support.Connector
import net.nemerosa.ontrack.model.support.ConnectorDescription
import org.springframework.stereotype.Component

/**
 * Well, we do not claim to check if GitHub is down or up, but just to see if we can connect
 * to it...
 */
@Component
class GitHubConnectorStatusIndicator(
        configurationService: ConfigurationService<GitHubEngineConfiguration>,
        securityService: SecurityService,
        private val gitHubClientFactory: OntrackGitHubClientFactory
) : ConfigurationConnectorStatusIndicator<GitHubEngineConfiguration>(configurationService, securityService) {

    override val type: String = "github"

    override fun connect(config: GitHubEngineConfiguration) {
        gitHubClientFactory.create(config).repositories
    }

    override fun connectorDescription(config: GitHubEngineConfiguration) = ConnectorDescription(
            connector = Connector(type, config.name),
            connection = config.url
    )
}
