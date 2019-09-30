package net.nemerosa.ontrack.extension.sonarqube.client

import net.nemerosa.ontrack.extension.sonarqube.configuration.SonarQubeConfiguration
import org.springframework.stereotype.Component

@Component
class SonarQubeClientFactoryImpl : SonarQubeClientFactory {
    override fun getClient(configuration: SonarQubeConfiguration) = SonarQubeClientImpl(configuration)
}