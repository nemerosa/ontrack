package net.nemerosa.ontrack.extension.sonarqube.configuration

import com.fasterxml.jackson.annotation.JsonProperty
import net.nemerosa.ontrack.model.support.ConfigurationDescriptor
import net.nemerosa.ontrack.model.support.UserPasswordConfiguration

// TODO #532 Using `open` as a workaround
open class SonarQubeConfiguration(
    final override val name: String,
    val url: String,
    override val password: String?
) : UserPasswordConfiguration<SonarQubeConfiguration> {

    /**
     * Not used
     */

    @get:JsonProperty(access = JsonProperty.Access.READ_ONLY)
    override val user: String? = null

    override fun withPassword(password: String?) = SonarQubeConfiguration(
        name,
        url,
        password ?: ""
    )

    override val descriptor: ConfigurationDescriptor = ConfigurationDescriptor(
        name,
        "$name ($url)"
    )

    override fun obfuscate() = SonarQubeConfiguration(
        name,
        url,
        ""
    )
}