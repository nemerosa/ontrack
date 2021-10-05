package net.nemerosa.ontrack.extension.github.service

import net.nemerosa.ontrack.extension.github.GitHubIssueServiceExtension
import net.nemerosa.ontrack.extension.github.model.GitHubEngineConfiguration
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration

data class GitHubIssueServiceConfiguration(
    val configuration: GitHubEngineConfiguration,
    val repository: String
) : IssueServiceConfiguration {

    override val serviceId: String = GitHubIssueServiceExtension.GITHUB_SERVICE_ID
    override val name: String = "${configuration.name}:$repository"

}