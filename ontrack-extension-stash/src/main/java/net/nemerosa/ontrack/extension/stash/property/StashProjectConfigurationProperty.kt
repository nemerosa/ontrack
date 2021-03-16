package net.nemerosa.ontrack.extension.stash.property

import net.nemerosa.ontrack.extension.stash.model.StashConfiguration
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
        private val configuration: StashConfiguration,
        val project: String,
        val repository: String,
        val indexationInterval: Int,
        val issueServiceConfigurationIdentifier: String?
) : ConfigurationProperty<StashConfiguration> {

    override fun getConfiguration(): StashConfiguration = configuration

    /**
     * Link to the repository
     */
    val repositoryUrl: String
        get() = if (configuration.isCloud) {
            String.format(
                    "%s/%s/%s",
                    configuration.url,
                    project,
                    repository
            )
        } else {
            String.format(
                    "%s/projects/%s/repos/%s",
                    configuration.url,
                    project,
                    repository
            )
        }

}
