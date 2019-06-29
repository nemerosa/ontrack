package net.nemerosa.ontrack.extension.git

import net.nemerosa.ontrack.extension.git.model.BasicGitConfiguration
import net.nemerosa.ontrack.extension.support.ConfigurationConnectorStatusIndicator
import net.nemerosa.ontrack.git.GitRepositoryClientFactory
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.support.ConfigurationService
import net.nemerosa.ontrack.model.support.ConnectorDescription
import org.springframework.stereotype.Component

@Component
class GitConnectorStatusIndicator(
        configurationService: ConfigurationService<BasicGitConfiguration>,
        securityService: SecurityService,
        private val repositoryClientFactory: GitRepositoryClientFactory
) : ConfigurationConnectorStatusIndicator<BasicGitConfiguration>(configurationService, securityService) {

    override fun connect(config: BasicGitConfiguration) {
        repositoryClientFactory.getClient(config.gitRepository).remoteBranches
    }

    override fun connectorDescription(config: BasicGitConfiguration) = ConnectorDescription(
            type = "git",
            name = config.name,
            connection = config.remote
    )
}
