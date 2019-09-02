package net.nemerosa.ontrack.extension.sonarqube.client

import net.nemerosa.ontrack.extension.sonarqube.configuration.SonarQubeConfiguration
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.web.client.RestTemplate

class SonarQubeClientImpl(
        configuration: SonarQubeConfiguration
) : SonarQubeClient {

    override val serverVersion: String
        get() = restTemplate.getForObject("/api/server/version", String::class.java)

    override val systemHealth: String
        get() = restTemplate.getForObject("/api/system/health", SystemHealth::class.java).health

    private val restTemplate: RestTemplate = RestTemplateBuilder()
            .rootUri(configuration.url)
            .basicAuthorization(configuration.password, "") // See https://docs.sonarqube.org/latest/extend/web-api/
            .build()

    private class SystemHealth(
            val health: String
    )

}