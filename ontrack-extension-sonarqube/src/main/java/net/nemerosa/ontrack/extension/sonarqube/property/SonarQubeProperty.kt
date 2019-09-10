package net.nemerosa.ontrack.extension.sonarqube.property

import net.nemerosa.ontrack.extension.sonarqube.configuration.SonarQubeConfiguration
import net.nemerosa.ontrack.model.support.ConfigurationProperty
import java.net.URLEncoder

class SonarQubeProperty(
        private val configuration: SonarQubeConfiguration,
        val key: String,
        val validationStamp: String
) : ConfigurationProperty<SonarQubeConfiguration> {
    override fun getConfiguration(): SonarQubeConfiguration = configuration

    val projectUrl: String
        get() = "${configuration.url}/dashboard?id=${URLEncoder.encode(key, "UTF-8")}"
}
