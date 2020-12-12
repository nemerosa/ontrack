package net.nemerosa.ontrack.extension.github.service

import net.nemerosa.ontrack.extension.github.GitHubIssueServiceExtension
import net.nemerosa.ontrack.extension.github.model.GitHubEngineConfiguration
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration

data class GitHubIssueServiceConfiguration(
        val configuration: GitHubEngineConfiguration,
        val repository: String
) : IssueServiceConfiguration {

    override fun getServiceId(): String = GitHubIssueServiceExtension.GITHUB_SERVICE_ID

    override fun getName(): String = "${configuration.name}:$repository"

}