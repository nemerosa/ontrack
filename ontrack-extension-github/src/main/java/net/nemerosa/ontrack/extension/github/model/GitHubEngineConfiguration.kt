package net.nemerosa.ontrack.extension.github.model

import com.fasterxml.jackson.annotation.JsonIgnore
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.Form.defaultNameField
import net.nemerosa.ontrack.model.form.Password
import net.nemerosa.ontrack.model.form.Text
import net.nemerosa.ontrack.model.support.ConfigurationDescriptor
import net.nemerosa.ontrack.model.support.UserPassword
import net.nemerosa.ontrack.model.support.UserPasswordConfiguration
import java.beans.ConstructorProperties
import java.lang.String.format
import java.util.*
import java.util.function.Function

/**
 * Configuration for accessing a GitHub engine, github.com or GitHub enterprise.
 *
 * @property name Name of this configuration
 * @property url End point
 * @property user User name
 * @property password User password
 * @property oauth2Token OAuth2 token
 */
open class GitHubEngineConfiguration
@ConstructorProperties("name", "url", "user", "password", "oauth2Token")
constructor(
        private val name: String,
        url: String?,
        private val user: String?,
        private val password: String?,
        val oauth2Token: String?
) : UserPasswordConfiguration<GitHubEngineConfiguration> {

    override fun getName(): String = name

    override fun getUser(): String? = user

    override fun getPassword(): String? = password



    /**
     * End point
     */
    val url: String

    init {
        this.url = if (url.isNullOrBlank()) GITHUB_COM else url
    }

    @JsonIgnore
    override fun getDescriptor(): ConfigurationDescriptor {
        return ConfigurationDescriptor(
                name,
                format("%s (%s)", name, url)
        )
    }

    override fun obfuscate(): GitHubEngineConfiguration {
        return withPassword("").withNoOauth2Token()
    }

    private fun withNoOauth2Token(): GitHubEngineConfiguration {
        return GitHubEngineConfiguration(
                name,
                url,
                user,
                password,
                ""
        )
    }

    override fun withPassword(password: String?): GitHubEngineConfiguration {
        return GitHubEngineConfiguration(
                name,
                url,
                user,
                password,
                oauth2Token
        )
    }

    fun asForm(): Form {
        return form()
                .with(defaultNameField().readOnly().value(name))
                .fill("url", url)
                .fill("user", user)
                .fill("password", "")
                .fill("oauth2Token", oauth2Token)
    }

    override fun clone(targetConfigurationName: String, replacementFunction: Function<String, String>): GitHubEngineConfiguration {
        return GitHubEngineConfiguration(
                targetConfigurationName,
                replacementFunction.apply(url),
                user?.let { replacementFunction.apply(user) },
                password,
                oauth2Token
        )
    }

    @JsonIgnore
    override fun getCredentials(): Optional<UserPassword> {
        return when {
            !oauth2Token.isNullOrBlank() -> Optional.of(
                    UserPassword(
                            oauth2Token,
                            "x-oauth-basic"
                    )
            )
            !user.isNullOrBlank() && !password.isNullOrBlank() -> Optional.of(
                    UserPassword(
                            user,
                            password
                    )
            )
            else -> Optional.empty()
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GitHubEngineConfiguration

        if (name != other.name) return false
        if (user != other.user) return false
        if (password != other.password) return false
        if (oauth2Token != other.oauth2Token) return false
        if (url != other.url) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + (user?.hashCode() ?: 0)
        result = 31 * result + (password?.hashCode() ?: 0)
        result = 31 * result + (oauth2Token?.hashCode() ?: 0)
        result = 31 * result + url.hashCode()
        return result
    }

    companion object {

        /**
         * github.com end point.
         */
        const val GITHUB_COM = "https://github.com"

        @JvmStatic
        fun form(): Form {
            return Form.create()
                    .with(defaultNameField())
                    .with(
                            Text.of("url")
                                    .label("URL")
                                    .length(250)
                                    .optional()
                                    .help(format("URL of the GitHub engine. Defaults to %s if not defined.", GITHUB_COM))
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
                            Text.of("oauth2Token")
                                    .label("OAuth2 token")
                                    .length(50)
                                    .optional()
                    )
        }
    }

}