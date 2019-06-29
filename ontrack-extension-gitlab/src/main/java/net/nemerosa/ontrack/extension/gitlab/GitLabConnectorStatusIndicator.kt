package net.nemerosa.ontrack.extension.gitlab

import net.nemerosa.ontrack.extension.gitlab.client.OntrackGitLabClientFactory
import net.nemerosa.ontrack.extension.gitlab.model.GitLabConfiguration
import net.nemerosa.ontrack.extension.support.ConfigurationConnectorStatusIndicator
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.support.ConfigurationService
import net.nemerosa.ontrack.model.support.ConnectorDescription
import org.springframework.stereotype.Component

/**
 * Well, we do not claim to check if GitLab is down or up, but just to see if we can connect
 * to it...
 */
@Component
class GitLabConnectorStatusIndicator(
        configurationService: ConfigurationService<GitLabConfiguration>,
        securityService: SecurityService,
        private val gitLabClientFactory: OntrackGitLabClientFactory
) : ConfigurationConnectorStatusIndicator<GitLabConfiguration>(configurationService, securityService) {

    override fun connect(config: GitLabConfiguration) {
        gitLabClientFactory.create(config).repositories
    }

    override fun connectorDescription(config: GitLabConfiguration) = ConnectorDescription(
            type = "gitlab",
            name = config.name,
            connection = config.url
    )
}
