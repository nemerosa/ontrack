package net.nemerosa.ontrack.extension.gitlab.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.Form.Companion.defaultNameField
import net.nemerosa.ontrack.model.form.Password
import net.nemerosa.ontrack.model.form.Text
import net.nemerosa.ontrack.model.form.YesNo
import net.nemerosa.ontrack.model.support.ConfigurationDescriptor
import net.nemerosa.ontrack.model.support.UserPassword
import net.nemerosa.ontrack.model.support.UserPasswordConfiguration
import java.beans.ConstructorProperties
import java.lang.String.format
import java.util.*
import java.util.function.Function

/**
 * Configuration for accessing a GitLab application.
 */
open class GitLabConfiguration
@ConstructorProperties("name", "url", "user", "password", "ignoreSslCertificate")
constructor(
        private val name: String,
        val url: String,
        private val user: String?,
        private val password: String?,
        @get:JsonProperty("ignoreSslCertificate")
        val isIgnoreSslCertificate: Boolean
) : UserPasswordConfiguration<GitLabConfiguration> {

    override fun getName(): String = name

    override fun getUser(): String? = user

    override fun getPassword(): String? = password

    @JsonIgnore
    override fun getDescriptor(): ConfigurationDescriptor {
        return ConfigurationDescriptor(
                name,
                format("%s (%s)", name, url)
        )
    }

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

    override fun clone(targetConfigurationName: String, replacementFunction: Function<String, String>): GitLabConfiguration {
        return GitLabConfiguration(
                targetConfigurationName,
                replacementFunction.apply(url),
                user?.let { replacementFunction.apply(user) },
                password,
                isIgnoreSslCertificate
        )
    }

    override fun getCredentials(): Optional<UserPassword> =
            if (user.isNullOrBlank() || password.isNullOrBlank()) {
                Optional.empty()
            } else {
                Optional.of(
                        UserPassword(
                                user,
                                password
                        )
                )
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