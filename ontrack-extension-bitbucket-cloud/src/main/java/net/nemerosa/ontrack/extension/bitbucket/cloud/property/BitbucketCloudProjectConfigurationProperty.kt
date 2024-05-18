package net.nemerosa.ontrack.extension.bitbucket.cloud.property

import net.nemerosa.ontrack.extension.bitbucket.cloud.configuration.BitbucketCloudConfiguration
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.docs.DocumentationIgnore
import net.nemerosa.ontrack.model.docs.DocumentationType
import net.nemerosa.ontrack.model.support.ConfigurationProperty

/**
 * Link between a project and a Bitbucket Cloud repository.
 *
 * @property configuration Link to the Bitbucket Cloud configuration
 * @property repository Repository in Bitbucket Cloud
 * @property indexationInterval Indexation interval
 * @property issueServiceConfigurationIdentifier ID to the [net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration] associated
 * with this repository.
 */
class BitbucketCloudProjectConfigurationProperty(
    @DocumentationType("String", "Name of the Bitbucket Cloud configuration")
    override val configuration: BitbucketCloudConfiguration,
    @APIDescription("Name of the repository")
    val repository: String,
    @APIDescription("How often to index the repository, in minutes. Use 0 to disable indexation.")
    val indexationInterval: Int,
    @APIDescription("Identifier for the issue service")
    val issueServiceConfigurationIdentifier: String?
) : ConfigurationProperty<BitbucketCloudConfiguration> {

    /**
     * Gets the URL to the repository
     */
    @DocumentationIgnore
    val repositoryUrl: String get() = "https://bitbucket.org/${configuration.workspace}/$repository"

}
