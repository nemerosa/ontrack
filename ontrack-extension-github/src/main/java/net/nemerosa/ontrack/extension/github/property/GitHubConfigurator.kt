package net.nemerosa.ontrack.extension.github.property

import net.nemerosa.ontrack.extension.git.model.GitConfiguration
import net.nemerosa.ontrack.extension.git.model.GitConfigurator
import net.nemerosa.ontrack.extension.git.model.GitPullRequest
import net.nemerosa.ontrack.extension.github.GitHubIssueServiceExtension
import net.nemerosa.ontrack.extension.github.client.OntrackGitHubClientFactory
import net.nemerosa.ontrack.extension.github.service.GitHubIssueServiceConfiguration
import net.nemerosa.ontrack.extension.issues.IssueServiceRegistry
import net.nemerosa.ontrack.extension.issues.model.ConfiguredIssueService
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfigurationRepresentation.Companion.isSelf
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.PropertyService
import org.springframework.stereotype.Component

@Component
class GitHubConfigurator(
        private val propertyService: PropertyService,
        private val issueServiceRegistry: IssueServiceRegistry,
        private val issueServiceExtension: GitHubIssueServiceExtension,
        private val ontrackGitHubClientFactory: OntrackGitHubClientFactory
) : GitConfigurator {

    override fun isProjectConfigured(project: Project): Boolean {
        return propertyService.hasProperty(project, GitHubProjectConfigurationPropertyType::class.java)
    }

    override fun getConfiguration(project: Project): GitConfiguration? {
        return propertyService.getProperty(project, GitHubProjectConfigurationPropertyType::class.java)
                .value
                ?.run { getGitConfiguration(this) }
    }

    override fun getPullRequest(configuration: GitConfiguration, id: Int): GitPullRequest? =
            if (configuration is GitHubGitConfiguration) {
                val client = ontrackGitHubClientFactory.create(configuration.property.configuration)
                client.getPullRequest(
                        configuration.property.repository,
                        id
                )
            } else {
                null
            }

    private fun getGitConfiguration(property: GitHubProjectConfigurationProperty): GitConfiguration {
        return GitHubGitConfiguration(
                property,
                getConfiguredIssueService(property)
        )
    }

    private fun getConfiguredIssueService(property: GitHubProjectConfigurationProperty): ConfiguredIssueService {
        val identifier = property.issueServiceConfigurationIdentifier
        return if (identifier.isNullOrBlank() || isSelf(identifier)) {
            ConfiguredIssueService(
                    issueServiceExtension,
                    GitHubIssueServiceConfiguration(
                            property.configuration,
                            property.repository
                    )
            )
        } else {
            issueServiceRegistry.getConfiguredIssueService(identifier)
        }
    }

}