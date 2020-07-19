package net.nemerosa.ontrack.extension.gitlab.property

import net.nemerosa.ontrack.extension.git.model.GitConfiguration
import net.nemerosa.ontrack.extension.git.model.GitConfigurator
import net.nemerosa.ontrack.extension.git.model.GitPullRequest
import net.nemerosa.ontrack.extension.gitlab.GitLabIssueServiceExtension
import net.nemerosa.ontrack.extension.gitlab.client.OntrackGitLabClientFactory
import net.nemerosa.ontrack.extension.gitlab.model.GitLabIssueServiceConfiguration
import net.nemerosa.ontrack.extension.issues.IssueServiceRegistry
import net.nemerosa.ontrack.extension.issues.model.ConfiguredIssueService
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfigurationRepresentation.Companion.isSelf
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.PropertyService
import org.springframework.stereotype.Component

@Component
class GitLabConfigurator(
        private val propertyService: PropertyService,
        private val issueServiceRegistry: IssueServiceRegistry,
        private val issueServiceExtension: GitLabIssueServiceExtension,
        private val ontrackGitLabClientFactory: OntrackGitLabClientFactory
) : GitConfigurator {

    override fun isProjectConfigured(project: Project): Boolean {
        return propertyService.hasProperty(project, GitLabProjectConfigurationPropertyType::class.java)
    }

    override fun getConfiguration(project: Project): GitConfiguration? {
        return propertyService.getProperty(project, GitLabProjectConfigurationPropertyType::class.java)
                .value
                ?.run { getGitConfiguration(this) }
    }

    override fun getPullRequest(configuration: GitConfiguration, id: Int): GitPullRequest? =
            if (configuration is GitLabGitConfiguration) {
                val client = ontrackGitLabClientFactory.create(configuration.property.configuration)
                client.getPullRequest(
                        configuration.property.repository,
                        id
                )
            } else {
                null
            }

    private fun getGitConfiguration(property: GitLabProjectConfigurationProperty): GitConfiguration {
        return GitLabGitConfiguration(
                property,
                getConfiguredIssueService(property)
        )
    }

    private fun getConfiguredIssueService(property: GitLabProjectConfigurationProperty): ConfiguredIssueService {
        val identifier = property.issueServiceConfigurationIdentifier
        return if (identifier.isNullOrBlank() || isSelf(identifier)) {
            ConfiguredIssueService(
                    issueServiceExtension,
                    GitLabIssueServiceConfiguration(
                            property.configuration,
                            property.repository
                    )
            )
        } else {
            issueServiceRegistry.getConfiguredIssueService(identifier)
        }
    }

}