package net.nemerosa.ontrack.extension.git.model

import com.fasterxml.jackson.annotation.JsonIgnore
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfigurationRepresentation
import net.nemerosa.ontrack.git.GitRepository
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.Form.defaultNameField
import net.nemerosa.ontrack.model.form.Password
import net.nemerosa.ontrack.model.form.Selection
import net.nemerosa.ontrack.model.form.Text
import net.nemerosa.ontrack.model.support.ConfigurationDescriptor
import net.nemerosa.ontrack.model.support.UserPassword
import net.nemerosa.ontrack.model.support.UserPasswordConfiguration
import java.util.*
import java.util.function.Function

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
        private val name: String,
        val remote: String,
        private val user: String?,
        private val password: String?,
        val commitLink: String?,
        val fileAtCommitLink: String?,
        val indexationInterval: Int,
        val issueServiceConfigurationIdentifier: String?
) : UserPasswordConfiguration<BasicGitConfiguration> {

    override fun getName(): String = name

    override fun getUser(): String? = user

    override fun getPassword(): String? = password

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
            val credentials = credentials
            return GitRepository(
                    TYPE,
                    name,
                    remote,
                    credentials.map { it.user }.orElse(""),
                    credentials.map { it.password }.orElse("")
            )
        }

    @JsonIgnore
    override fun getCredentials(): Optional<UserPassword> {
        return if (user != null && user.isNotBlank())
            Optional.of(UserPassword(user, password ?: ""))
        else
            Optional.empty()
    }

    override fun obfuscate(): BasicGitConfiguration {
        return withPassword("")
    }

    @JsonIgnore
    override fun getDescriptor(): ConfigurationDescriptor {
        return ConfigurationDescriptor(
                name,
                String.format("%s (%s)", name, remote)
        )
    }

    override fun clone(targetConfigurationName: String, replacementFunction: Function<String, String>): BasicGitConfiguration {
        return BasicGitConfiguration(
                targetConfigurationName,
                replacementFunction.apply(remote),
                user?.let { replacementFunction.apply(it) },
                password,
                commitLink?.let { replacementFunction.apply(it) },
                fileAtCommitLink?.let { replacementFunction.apply(it) },
                indexationInterval,
                issueServiceConfigurationIdentifier
        )
    }

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
