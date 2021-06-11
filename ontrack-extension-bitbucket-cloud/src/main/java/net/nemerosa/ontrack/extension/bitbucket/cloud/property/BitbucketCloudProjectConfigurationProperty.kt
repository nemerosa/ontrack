package net.nemerosa.ontrack.extension.bitbucket.cloud.property

import net.nemerosa.ontrack.extension.bitbucket.cloud.configuration.BitbucketCloudConfiguration
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
    private val configuration: BitbucketCloudConfiguration,
    val repository: String,
    val indexationInterval: Int,
    val issueServiceConfigurationIdentifier: String?
) : ConfigurationProperty<BitbucketCloudConfiguration> {

    override fun getConfiguration(): BitbucketCloudConfiguration = configuration

}
