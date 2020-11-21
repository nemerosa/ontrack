package net.nemerosa.ontrack.extension.git.support

import net.nemerosa.ontrack.extension.git.model.BasicGitActualConfiguration
import net.nemerosa.ontrack.extension.git.model.GitConfiguration
import net.nemerosa.ontrack.extension.git.model.GitConfigurator
import net.nemerosa.ontrack.extension.git.model.GitPullRequest
import net.nemerosa.ontrack.extension.git.property.GitProjectConfigurationProperty
import net.nemerosa.ontrack.extension.git.property.GitProjectConfigurationPropertyType
import net.nemerosa.ontrack.extension.issues.IssueServiceRegistry
import net.nemerosa.ontrack.extension.issues.model.ConfiguredIssueService
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.PropertyService
import org.springframework.stereotype.Component

@Component
class BasicGitConfigurator(
        private val propertyService: PropertyService,
        private val issueServiceRegistry: IssueServiceRegistry
) : GitConfigurator {

    override fun isProjectConfigured(project: Project): Boolean {
        return propertyService.hasProperty(project, GitProjectConfigurationPropertyType::class.java)
    }

    override fun getConfiguration(project: Project): GitConfiguration? {
        return propertyService.getProperty(project, GitProjectConfigurationPropertyType::class.java)
                .value
                ?.run {
                    getGitConfiguration(this)
                }
    }

    /**
     * Pull requests are not supported by basic Git.
     */
    override fun toPullRequestID(key: String): Int? = null

    /**
     * Pull requests are not supported by basic Git.
     */
    override fun getPullRequest(configuration: GitConfiguration, id: Int): GitPullRequest? = null

    private fun getGitConfiguration(property: GitProjectConfigurationProperty): GitConfiguration {
        return BasicGitActualConfiguration(
                property.configuration,
                getConfiguredIssueService(property)
        )
    }

    private fun getConfiguredIssueService(property: GitProjectConfigurationProperty): ConfiguredIssueService? {
        val identifier = property.configuration.issueServiceConfigurationIdentifier?.takeIf { it.isNotBlank() }
        return identifier?.let { issueServiceRegistry.getConfiguredIssueService(it) }
    }

}