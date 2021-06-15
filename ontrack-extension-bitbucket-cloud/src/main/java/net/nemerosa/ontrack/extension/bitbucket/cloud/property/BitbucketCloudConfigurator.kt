package net.nemerosa.ontrack.extension.bitbucket.cloud.property

import net.nemerosa.ontrack.extension.git.model.GitConfiguration
import net.nemerosa.ontrack.extension.git.model.GitConfigurator
import net.nemerosa.ontrack.extension.git.model.GitPullRequest
import net.nemerosa.ontrack.extension.issues.IssueServiceRegistry
import net.nemerosa.ontrack.extension.issues.model.ConfiguredIssueService
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.PropertyService
import org.springframework.stereotype.Component

@Component
class BitbucketCloudConfigurator(
    private val propertyService: PropertyService,
    private val issueServiceRegistry: IssueServiceRegistry,
): GitConfigurator {

    override fun isProjectConfigured(project: Project): Boolean =
        propertyService.hasProperty(project, BitbucketCloudProjectConfigurationPropertyType::class.java)

    override fun getConfiguration(project: Project): GitConfiguration? =
        propertyService.getProperty(project, BitbucketCloudProjectConfigurationPropertyType::class.java)
            .value
            ?.run {
                BitbucketCloudGitConfiguration(
                    this,
                    getConfiguredIssueService(this)
                )
            }

    override fun getPullRequest(configuration: GitConfiguration, id: Int): GitPullRequest? {
        TODO("Not yet implemented")
    }

    private fun getConfiguredIssueService(property: BitbucketCloudProjectConfigurationProperty): ConfiguredIssueService? =
        property.issueServiceConfigurationIdentifier
            ?.takeIf { it.isNotBlank() }
            ?.let { issueServiceRegistry.getConfiguredIssueService(it) }
}