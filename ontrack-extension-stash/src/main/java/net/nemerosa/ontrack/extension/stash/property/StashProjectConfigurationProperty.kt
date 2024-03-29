package net.nemerosa.ontrack.extension.stash.property

import net.nemerosa.ontrack.extension.stash.model.StashConfiguration
import net.nemerosa.ontrack.extension.stash.model.getRepositoryUrl
import net.nemerosa.ontrack.model.support.ConfigurationProperty

/**
 * @property configuration Link to the Bitbucket configuration
 * @property project Project in Bitbucket
 * @property repository Repository in the project
 * @property indexationInterval Indexation interval
 * @property issueServiceConfigurationIdentifier ID to the [net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration] associated
 * with this repository.
 */
class StashProjectConfigurationProperty(
    override val configuration: StashConfiguration,
    val project: String,
    val repository: String,
    val indexationInterval: Int,
    val issueServiceConfigurationIdentifier: String?
) : ConfigurationProperty<StashConfiguration> {

    /**
     * Link to the repository
     */
    val repositoryUrl: String
        get() = getRepositoryUrl(configuration, project, repository)

}
