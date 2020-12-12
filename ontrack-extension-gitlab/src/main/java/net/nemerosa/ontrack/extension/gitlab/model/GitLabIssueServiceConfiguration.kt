package net.nemerosa.ontrack.extension.gitlab.model

import net.nemerosa.ontrack.extension.gitlab.GitLabIssueServiceExtension
import net.nemerosa.ontrack.extension.gitlab.property.GitLabGitConfiguration
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration

data class GitLabIssueServiceConfiguration(
        val configuration: GitLabConfiguration,
        val repository: String
) : IssueServiceConfiguration {

    override fun getServiceId(): String {
        return GitLabIssueServiceExtension.GITLAB_SERVICE_ID
    }

    override fun getName(): String = "${configuration.name}${GitLabGitConfiguration.CONFIGURATION_REPOSITORY_SEPARATOR}$repository"

}