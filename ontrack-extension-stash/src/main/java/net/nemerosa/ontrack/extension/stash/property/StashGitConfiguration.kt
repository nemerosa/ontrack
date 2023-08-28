package net.nemerosa.ontrack.extension.stash.property

import net.nemerosa.ontrack.extension.git.model.GitConfiguration
import net.nemerosa.ontrack.extension.issues.model.ConfiguredIssueService
import net.nemerosa.ontrack.extension.stash.model.StashConfiguration
import net.nemerosa.ontrack.git.GitRepositoryAuthenticator
import net.nemerosa.ontrack.git.UsernamePasswordGitRepositoryAuthenticator

class StashGitConfiguration(
    val configuration: StashConfiguration,
    val project: String,
    val repository: String,
    override val indexationInterval: Int,
    override val configuredIssueService: ConfiguredIssueService?,
) : GitConfiguration {

    override val type: String = "stash"
    override val name: String = configuration.name

    /**
     * Checks if this configuration denotes any Bitbucket Cloud instance
     */
    @Deprecated("See StashConfiguration")
    val isCloud: Boolean = configuration.isCloud

    override val remote: String
        get() = String.format(
            remoteFormat,
            configuration.url,
            project,
            repository
        )
    override val authenticator: GitRepositoryAuthenticator?
        get() = if (configuration.user != null && configuration.password != null)
            UsernamePasswordGitRepositoryAuthenticator(
                configuration.user!!,
                configuration.password!!,
            ) else null

    override val commitLink: String
        get() = String.format(
            commitLinkFormat,
            configuration.url,
            project,
            repository
        )
    override val fileAtCommitLink: String
        get() = String.format(
            fileAtCommitLinkFormat,
            configuration.url,
            project,
            repository
        )

    private val fileAtCommitLinkFormat: String =
        if (isCloud) "%s/%s/%s/src/{commit}/{path}" else "%s/projects/%s/repos/%s/browse/{path}?at={commit}"
    private val commitLinkFormat: String =
        if (isCloud) "%s/%s/%s/commits/{commit}" else "%s/projects/%s/repos/%s/commits/{commit}"
    private val remoteFormat: String = if (isCloud) "%s/%s/%s.git" else "%s/scm/%s/%s.git"

}
