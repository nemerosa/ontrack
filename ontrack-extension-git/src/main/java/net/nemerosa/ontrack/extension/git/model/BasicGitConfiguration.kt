package net.nemerosa.ontrack.extension.git.model

import com.fasterxml.jackson.annotation.JsonIgnore
import net.nemerosa.ontrack.git.GitRepository
import net.nemerosa.ontrack.git.UsernamePasswordGitRepositoryAuthenticator
import net.nemerosa.ontrack.model.support.UserPasswordConfiguration

/**
 * Git configuration based on direct definition.
 *
 * @property name Name of this configuration
 * @property remote Remote path to the source repository
 * @property user User name
 * @property password User password
 * @property commitLink Link to a commit, using {commit} as placeholder
 * @property fileAtCommitLink Link to a file at a given commit, using {commit} and {path} as placeholders
 * @property indexationInterval Indexation interval
 * @property indexationInterval Indexation interval
 * @property issueServiceConfigurationIdentifier ID to the
 *  [net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration] associated
 *  with this repository.
 */
class BasicGitConfiguration(
    name: String,
    val remote: String,
    user: String?,
    password: String?,
    val commitLink: String?,
    val fileAtCommitLink: String?,
    val indexationInterval: Int,
    val issueServiceConfigurationIdentifier: String?
) : UserPasswordConfiguration<BasicGitConfiguration>(name, user, password) {

    fun withUser(user: String?) = BasicGitConfiguration(
        name,
        remote,
        user,
        password,
        commitLink,
        fileAtCommitLink,
        indexationInterval,
        issueServiceConfigurationIdentifier
    )

    override fun withPassword(password: String?) = BasicGitConfiguration(
        name,
        remote,
        user,
        password,
        commitLink,
        fileAtCommitLink,
        indexationInterval,
        issueServiceConfigurationIdentifier
    )

    fun withName(name: String) = BasicGitConfiguration(
        name,
        remote,
        user,
        password,
        commitLink,
        fileAtCommitLink,
        indexationInterval,
        issueServiceConfigurationIdentifier
    )

    fun withRemote(remote: String) = BasicGitConfiguration(
        name,
        remote,
        user,
        password,
        commitLink,
        fileAtCommitLink,
        indexationInterval,
        issueServiceConfigurationIdentifier
    )

    fun withIssueServiceConfigurationIdentifier(issueServiceConfigurationIdentifier: String?) = BasicGitConfiguration(
        name,
        remote,
        user,
        password,
        commitLink,
        fileAtCommitLink,
        indexationInterval,
        issueServiceConfigurationIdentifier
    )

    val gitRepository: GitRepository
        @JsonIgnore
        get() {
            return GitRepository(
                TYPE,
                name,
                remote,
                user?.takeIf { it.isNotBlank() }?.let {
                    UsernamePasswordGitRepositoryAuthenticator(it, password ?: "")
                },
            )
        }

    override fun obfuscate(): BasicGitConfiguration {
        return withPassword("")
    }

    companion object {

        const val TYPE = "basic"

        @JvmStatic
        fun empty(): BasicGitConfiguration {
            return BasicGitConfiguration(
                "",
                "",
                "",
                "",
                "",
                "",
                0,
                ""
            )
        }
    }
}
