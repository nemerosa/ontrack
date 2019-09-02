package net.nemerosa.ontrack.extension.sonarqube.configuration

import net.nemerosa.ontrack.model.support.ConfigurationDescriptor
import net.nemerosa.ontrack.model.support.UserPasswordConfiguration
import java.util.function.Function

class SonarQubeConfiguration(
        private val name: String,
        val url: String,
        val token: String
) : UserPasswordConfiguration<SonarQubeConfiguration> {

    override fun getName(): String = name

    /**
     * Not used
     */
    override fun getUser(): String? = null

    /**
     * The token
     */
    override fun getPassword(): String? = token

    override fun withPassword(password: String?) = SonarQubeConfiguration(
            name,
            url,
            password ?: ""
    )

    override fun clone(targetConfigurationName: String, replacementFunction: Function<String, String>) = SonarQubeConfiguration(
            targetConfigurationName,
            url,
            token
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