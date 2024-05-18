package net.nemerosa.ontrack.extension.github.property

import net.nemerosa.ontrack.extension.github.model.GitHubEngineConfiguration
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.docs.DocumentationType
import net.nemerosa.ontrack.model.support.ConfigurationProperty

/**
 * @property configuration Link to the GitHub configuration
 * @property repository Repository name
 * @property indexationInterval Indexation interval
 * @property issueServiceConfigurationIdentifier ID to the [net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration] associated
 * with this repository.
 */
class GitHubProjectConfigurationProperty(
    @DocumentationType("String", description = "Name of the configuration")
    override val configuration: GitHubEngineConfiguration,
    @APIDescription("GitHub repository, ie. org/name")
    val repository: String,
    @APIDescription("How often to index the repository, in minutes. Use 0 to disable indexation.")
    val indexationInterval: Int,
    @APIDescription("Identifier for the issue service")
    val issueServiceConfigurationIdentifier: String?
) : ConfigurationProperty<GitHubEngineConfiguration>

