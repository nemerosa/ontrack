package net.nemerosa.ontrack.extension.gitlab.property

import net.nemerosa.ontrack.extension.gitlab.model.GitLabConfiguration
import net.nemerosa.ontrack.model.support.ConfigurationProperty

/**
 * @property configuration Link to the GitLab configuration
 * @property issueServiceConfigurationIdentifier ID to the [net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration] associated
 * with this repository.
 * @property repository Repository name
 * @property indexationInterval Indexation interval
 */
class GitLabProjectConfigurationProperty(
        private val configuration: GitLabConfiguration,
        val issueServiceConfigurationIdentifier: String?,
        val repository: String,
        val indexationInterval: Int
) : ConfigurationProperty<GitLabConfiguration> {
    override fun getConfiguration(): GitLabConfiguration = configuration
}
