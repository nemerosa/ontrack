package net.nemerosa.ontrack.extension.sonarqube.configuration

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import net.nemerosa.ontrack.model.support.ConfigurationDescriptor
import net.nemerosa.ontrack.model.support.CredentialsConfiguration

/**
 * Configuration to connect to a SonarQube server.
 *
 * @property name Unique name for this configuration
 * @property url URL of the SonarQube server
 * @property password Connection token (called `password` for legacy reasons)
 */
// TODO #532 Using `open` as a workaround
@JsonIgnoreProperties(ignoreUnknown = true)
open class SonarQubeConfiguration(
    override val name: String,
    val url: String,
    val password: String?
) : CredentialsConfiguration<SonarQubeConfiguration> {

    override val descriptor: ConfigurationDescriptor
        get() = ConfigurationDescriptor(name, "$name ($url)")

    override fun obfuscate() = SonarQubeConfiguration(
        name,
        url,
        ""
    )

    override fun injectCredentials(oldConfig: SonarQubeConfiguration) = SonarQubeConfiguration(
        name = name,
        url = url,
        password = if (password.isNullOrBlank()) {
            oldConfig.password
        } else {
            password
        },
    )

    override fun encrypt(crypting: (plain: String?) -> String?) = SonarQubeConfiguration(
        name = name,
        url = url,
        password = crypting(password),
    )

    override fun decrypt(decrypting: (encrypted: String?) -> String?) = SonarQubeConfiguration(
        name = name,
        url = url,
        password = decrypting(password),
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SonarQubeConfiguration) return false

        if (name != other.name) return false
        if (url != other.url) return false
        if (password != other.password) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + url.hashCode()
        result = 31 * result + (password?.hashCode() ?: 0)
        return result
    }


}