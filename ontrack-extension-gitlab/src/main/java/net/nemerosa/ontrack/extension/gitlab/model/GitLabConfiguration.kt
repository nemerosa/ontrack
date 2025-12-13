package net.nemerosa.ontrack.extension.gitlab.model

import com.fasterxml.jackson.annotation.JsonProperty
import net.nemerosa.ontrack.model.support.UserPasswordConfiguration

/**
 * Configuration for accessing a GitLab application.
 */
open class GitLabConfiguration(
    name: String,
    val url: String,
    user: String?,
    password: String?,
    @get:JsonProperty("ignoreSslCertificate")
    val isIgnoreSslCertificate: Boolean
) : UserPasswordConfiguration<GitLabConfiguration>(name, user, password) {

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

}