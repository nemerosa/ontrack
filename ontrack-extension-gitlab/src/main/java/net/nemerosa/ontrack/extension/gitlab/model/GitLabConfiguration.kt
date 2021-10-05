package net.nemerosa.ontrack.extension.gitlab.model

import com.fasterxml.jackson.annotation.JsonProperty
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.Form.Companion.defaultNameField
import net.nemerosa.ontrack.model.form.Password
import net.nemerosa.ontrack.model.form.Text
import net.nemerosa.ontrack.model.form.YesNo
import net.nemerosa.ontrack.model.support.ConfigurationDescriptor
import net.nemerosa.ontrack.model.support.UserPasswordConfiguration

/**
 * Configuration for accessing a GitLab application.
 */
open class GitLabConfiguration(
    override val name: String,
    val url: String,
    override val user: String?,
    override val password: String?,
    @get:JsonProperty("ignoreSslCertificate")
    val isIgnoreSslCertificate: Boolean
) : UserPasswordConfiguration<GitLabConfiguration> {

    override val descriptor = ConfigurationDescriptor(
        name,
        "$name ($url)"
    )

    override fun obfuscate(): GitLabConfiguration {
        return this.withPassword("")
    }

    override fun withPassword(password: String?): GitLabConfiguration {
        return GitLabConfiguration(
            name,
            url,
            user,
            password,
            isIgnoreSslCertificate
        )
    }


    fun asForm(): Form {
        return form()
            .with(defaultNameField().readOnly().value(name))
            .fill("url", url)
            .fill("user", user)
            .fill("ignoreSslCertificate", isIgnoreSslCertificate)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GitLabConfiguration

        if (name != other.name) return false
        if (url != other.url) return false
        if (user != other.user) return false
        if (password != other.password) return false
        if (isIgnoreSslCertificate != other.isIgnoreSslCertificate) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + url.hashCode()
        result = 31 * result + (user?.hashCode() ?: 0)
        result = 31 * result + (password?.hashCode() ?: 0)
        result = 31 * result + isIgnoreSslCertificate.hashCode()
        return result
    }

    companion object {

        @JvmStatic
        fun form(): Form {
            return Form.create()
                .with(defaultNameField())
                .with(
                    Text.of("url")
                        .label("URL")
                        .length(250)
                        .help("URL of the GitLab engine.")
                )
                .with(
                    Text.of("user")
                        .label("User")
                        .length(16)
                )
                .with(
                    Password.of("password")
                        .label("Personal Access Token")
                        .length(50)
                )
                .with(
                    YesNo.of("ignoreSslCertificate")
                        .label("Ignore SSL certificate")
                )
        }
    }
}