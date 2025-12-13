package net.nemerosa.ontrack.extension.artifactory.client

import net.nemerosa.ontrack.extension.artifactory.configuration.ArtifactoryConfiguration
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.stereotype.Component

@Component
class ArtifactoryClientFactoryImpl() :
    ArtifactoryClientFactory {

    override fun getClient(configuration: ArtifactoryConfiguration): ArtifactoryClient {
        val restTemplate = RestTemplateBuilder()
            .rootUri(configuration.url)
            .basicAuthentication(configuration.user, configuration.password)
            .build()
        return ArtifactoryClientImpl(restTemplate)
    }
}
