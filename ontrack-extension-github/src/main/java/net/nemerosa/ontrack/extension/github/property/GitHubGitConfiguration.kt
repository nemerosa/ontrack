package net.nemerosa.ontrack.extension.github.property

import net.nemerosa.ontrack.extension.git.model.GitConfiguration
import net.nemerosa.ontrack.extension.issues.model.ConfiguredIssueService
import net.nemerosa.ontrack.git.GitRepositoryAuthenticator

class GitHubGitConfiguration(
    val property: GitHubProjectConfigurationProperty,
    override val configuredIssueService: ConfiguredIssueService?,
    override val authenticator: GitRepositoryAuthenticator?,
) : GitConfiguration {

    companion object {
        const val CONFIGURATION_REPOSITORY_SEPARATOR = ":"
    }

    override val type: String = "github"

    override val name: String = property.configuration.name

    override val remote: String = "${property.configuration.url}/${property.repository}.git"

    override val commitLink: String = "${property.configuration.url}/${property.repository}/commit/{commit}"

    override val fileAtCommitLink: String = "${property.configuration.url}/${property.repository}/blob/{commit}/{path}"

    override val indexationInterval: Int = property.indexationInterval

}
