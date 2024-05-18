package net.nemerosa.ontrack.extension.gitlab.property

import net.nemerosa.ontrack.extension.gitlab.model.GitLabConfiguration
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.docs.DocumentationType
import net.nemerosa.ontrack.model.support.ConfigurationProperty

/**
 * @property configuration Link to the GitLab configuration
 * @property issueServiceConfigurationIdentifier ID to the [net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration] associated
 * with this repository.
 * @property repository Repository name
 * @property indexationInterval Indexation interval
 */
class GitLabProjectConfigurationProperty(
    @DocumentationType("String", "Name of the GitLab configuration")
    override val configuration: GitLabConfiguration,
    @APIDescription("Issue service identifier")
    val issueServiceConfigurationIdentifier: String?,
    @APIDescription("Repository name")
    val repository: String,
    @APIDescription("How often to index the repository, in minutes. Use 0 to disable indexation.")
    val indexationInterval: Int
) : ConfigurationProperty<GitLabConfiguration>
