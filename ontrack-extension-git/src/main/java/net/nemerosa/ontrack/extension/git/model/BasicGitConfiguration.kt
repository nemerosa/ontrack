package net.nemerosa.ontrack.extension.git.model

import com.fasterxml.jackson.annotation.JsonIgnore
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfigurationRepresentation
import net.nemerosa.ontrack.git.GitRepository
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.Form.Companion.defaultNameField
import net.nemerosa.ontrack.model.form.Password
import net.nemerosa.ontrack.model.form.Selection
import net.nemerosa.ontrack.model.form.Text
import net.nemerosa.ontrack.model.support.ConfigurationDescriptor
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
// TODO #532 Workaround
open class BasicGitConfiguration(
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
                user ?: "",
                password ?: "",
            )
        }

    override fun obfuscate(): BasicGitConfiguration {
        return withPassword("")
    }

    override val descriptor: ConfigurationDescriptor
        get() = ConfigurationDescriptor(
            name,
            "$name ($remote)"
        )

    fun asForm(availableIssueServiceConfigurations: List<IssueServiceConfigurationRepresentation>): Form {
        return form(availableIssueServiceConfigurations)
            .with(defaultNameField().readOnly().value(name))
            .fill("remote", remote)
            .fill("user", user)
            .fill("password", "")
            .fill("commitLink", commitLink)
            .fill("fileAtCommitLink", fileAtCommitLink)
            .fill("indexationInterval", indexationInterval)
            .fill("issueServiceConfigurationIdentifier", issueServiceConfigurationIdentifier)
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

        @JvmStatic
        fun form(availableIssueServiceConfigurations: List<IssueServiceConfigurationRepresentation>): Form {
            return Form.create()
                .with(defaultNameField())
                .with(
                    Text.of("remote")
                        .label("Remote")
                        .help("Remote path to the source repository")
                )
                .with(
                    Text.of("user")
                        .label("User")
                        .length(16)
                        .optional()
                )
                .with(
                    Password.of("password")
                        .label("Password")
                        .length(40)
                        .optional()
                )
                .with(
                    Text.of("commitLink")
                        .label("Commit link")
                        .length(250)
                        .optional()
                        .help("Link to a commit, using {commit} as placeholder")
                )
                .with(
                    Text.of("fileAtCommitLink")
                        .label("File at commit link")
                        .length(250)
                        .optional()
                        .help("Link to a file at a given commit, using {commit} and {path} as placeholders")
                )
                .with(
                    net.nemerosa.ontrack.model.form.Int.of("indexationInterval")
                        .label("Indexation interval")
                        .min(0)
                        .max(60 * 24)
                        .value(0)
                        .help("@file:extension/git/help.net.nemerosa.ontrack.extension.git.model.GitConfiguration.indexationInterval.tpl.html")
                )
                .with(
                    Selection.of("issueServiceConfigurationIdentifier")
                        .label("Issue configuration")
                        .help("Select an issue service that is sued to associate tickets and issues to the source.")
                        .optional()
                        .items(availableIssueServiceConfigurations)
                )
        }
    }
}
