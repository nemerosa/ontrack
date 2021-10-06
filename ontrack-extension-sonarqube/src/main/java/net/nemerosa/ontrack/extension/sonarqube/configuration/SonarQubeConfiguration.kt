package net.nemerosa.ontrack.extension.sonarqube.configuration

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
}