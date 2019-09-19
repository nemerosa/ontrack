package net.nemerosa.ontrack.extension.github.property

import net.nemerosa.ontrack.extension.github.model.GitHubEngineConfiguration
import net.nemerosa.ontrack.model.support.ConfigurationProperty

/**
 * @property configuration Link to the GitHub configuration
 * @property repository Repository name
 * @property indexationInterval Indexation interval
 * @property issueServiceConfigurationIdentifier ID to the [net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration] associated
 * with this repository.
 */
class GitHubProjectConfigurationProperty(
        private val configuration: GitHubEngineConfiguration,
        val repository: String,
        val indexationInterval: Int,
        val issueServiceConfigurationIdentifier: String?
) : ConfigurationProperty<GitHubEngineConfiguration> {
    override fun getConfiguration(): GitHubEngineConfiguration = configuration
}
