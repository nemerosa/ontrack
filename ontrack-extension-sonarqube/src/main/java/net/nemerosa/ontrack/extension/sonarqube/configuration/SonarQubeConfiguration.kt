package net.nemerosa.ontrack.extension.sonarqube.configuration

import com.fasterxml.jackson.annotation.JsonProperty
import net.nemerosa.ontrack.model.support.ConfigurationDescriptor
import net.nemerosa.ontrack.model.support.UserPasswordConfiguration
import java.util.function.Function

// TODO #532 Using `open` as a workaround
open class SonarQubeConfiguration(
        private val name: String,
        val url: String,
        private val password: String?
) : UserPasswordConfiguration<SonarQubeConfiguration> {

    override fun getName(): String = name

    /**
     * Not used
     */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    override fun getUser(): String? = null

    /**
     * The token
     */
    override fun getPassword(): String? = password

    override fun withPassword(password: String?) = SonarQubeConfiguration(
            name,
            url,
            password ?: ""
    )

    override fun clone(targetConfigurationName: String, replacementFunction: Function<String, String>) = SonarQubeConfiguration(
            targetConfigurationName,
            url,
            password
    )

    override fun getDescriptor() = ConfigurationDescriptor(
            name,
            "$name ($url)"
    )

    override fun obfuscate() = SonarQubeConfiguration(
            name,
            url,
            ""
    )
}