package net.nemerosa.ontrack.extension.github.model

import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.Memo
import net.nemerosa.ontrack.model.form.Password
import net.nemerosa.ontrack.model.form.Text
import net.nemerosa.ontrack.model.support.ConfigurationDescriptor
import net.nemerosa.ontrack.model.support.CredentialsConfiguration
import net.nemerosa.ontrack.model.support.UserPasswordConfiguration
import java.lang.String.format

/**
 * Configuration for accessing a GitHub engine, github.com or GitHub enterprise.
 *
 * @property name Name of this configuration
 * @property url End point
 * @property user User name
 * @property password User password
 * @property oauth2Token OAuth2 token
 */
open class GitHubEngineConfiguration(
    override val name: String,
    url: String?,
    override val user: String? = null,
    override val password: String? = null,
    val oauth2Token: String? = null,
    val appId: String? = null,
    val appPrivateKey: String? = null,
    val appInstallationAccountName: String? = null
) : UserPasswordConfiguration<GitHubEngineConfiguration>, CredentialsConfiguration<GitHubEngineConfiguration> {

    /**
     * Authentication type
     */
    fun authenticationType(): GitHubAuthenticationType =
        when {
            !appId.isNullOrBlank() -> GitHubAuthenticationType.APP
            !oauth2Token.isNullOrBlank() -> when {
                user.isNullOrBlank() -> GitHubAuthenticationType.TOKEN
                else -> GitHubAuthenticationType.USER_TOKEN
            }
            !password.isNullOrBlank() -> GitHubAuthenticationType.PASSWORD
            else -> GitHubAuthenticationType.ANONYMOUS
        }

    /**
     * End point
     */
    val url: String

    init {
        this.url = if (url.isNullOrBlank()) GITHUB_COM else url
    }

    override val descriptor: ConfigurationDescriptor
        get() = ConfigurationDescriptor(
            name,
            "$name ($url)"
        )

    override fun obfuscate(): GitHubEngineConfiguration =
        GitHubEngineConfiguration(
            name = name,
            url = url,
            user = user,
            password = null,
            oauth2Token = null,
            appId = appId,
            appPrivateKey = null,
            appInstallationAccountName = appInstallationAccountName,
        )

    override fun withPassword(password: String?): GitHubEngineConfiguration =
        GitHubEngineConfiguration(
            name = name,
            url = url,
            user = user,
            password = password,
            oauth2Token = oauth2Token,
            appId = appId,
            appPrivateKey = appPrivateKey,
            appInstallationAccountName = appInstallationAccountName,
        )

    fun asForm(): Form = form(this)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GitHubEngineConfiguration

        if (name != other.name) return false
        if (user != other.user) return false
        if (password != other.password) return false
        if (oauth2Token != other.oauth2Token) return false
        if (appId != other.appId) return false
        if (appPrivateKey != other.appPrivateKey) return false
        if (appInstallationAccountName != other.appInstallationAccountName) return false
        if (url != other.url) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + (user?.hashCode() ?: 0)
        result = 31 * result + (password?.hashCode() ?: 0)
        result = 31 * result + (oauth2Token?.hashCode() ?: 0)
        result = 31 * result + (appId?.hashCode() ?: 0)
        result = 31 * result + (appPrivateKey?.hashCode() ?: 0)
        result = 31 * result + (appInstallationAccountName?.hashCode() ?: 0)
        result = 31 * result + url.hashCode()
        return result
    }

    override fun toString(): String {
        return "GitHubEngineConfiguration(name='$name', user=$user, password=$password, oauth2Token=$oauth2Token, appId=$appId, appPrivateKey=$appPrivateKey, appInstallationAccountName=$appInstallationAccountName, url='$url')"
    }

    companion object {

        /**
         * github.com end point.
         */
        const val GITHUB_COM = "https://github.com"

        @JvmStatic
        fun form(configuration: GitHubEngineConfiguration?): Form {
            return Form.create()
                .with(
                    Text.of(GitHubEngineConfiguration::name.name)
                        .label("Name")
                        .length(40)
                        .readOnly(configuration != null)
                        .regex("[A-Za-z0-9_\\.\\-]+")
                        .validation("Name is required and must contain only alpha-numeric characters, underscores, points or dashes.")
                        .value(configuration?.name)
                )
                .with(
                    Text.of(GitHubEngineConfiguration::url.name)
                        .label("URL")
                        .length(250)
                        .optional()
                        .help(format("URL of the GitHub engine. Defaults to %s if not defined.", GITHUB_COM))
                        .value(configuration?.url)
                )
                .with(
                    Text.of(GitHubEngineConfiguration::user.name)
                        .label("User")
                        .length(16)
                        .optional()
                        .value(configuration?.user)
                )
                .with(
                    Password.of(GitHubEngineConfiguration::password.name)
                        .label("Password")
                        .help("Deprecated. Prefer using tokens or GitHub Apps")
                        .length(40)
                        .optional()
                )
                .with(
                    Password.of(GitHubEngineConfiguration::oauth2Token.name)
                        .label("OAuth2 token")
                        .length(50)
                        .optional()
                )
                .with(
                    Text.of(GitHubEngineConfiguration::appId.name)
                        .label("GitHub App ID")
                        .help("ID of the GitHub App to use")
                        .optional()
                        .value(configuration?.appId)
                )
                .with(
                    Memo.of(GitHubEngineConfiguration::appPrivateKey.name)
                        .label("GitHub App PKey")
                        .help("Private key for the GitHub App")
                        .optional()
                )
                .with(
                    Text.of(GitHubEngineConfiguration::appInstallationAccountName.name)
                        .label("GitHub App Installation Account")
                        .help("Optional. In case of several installations for this app, select the account where this app has been installed.")
                        .optional()
                        .value(configuration?.appInstallationAccountName)
                )
        }
    }

}