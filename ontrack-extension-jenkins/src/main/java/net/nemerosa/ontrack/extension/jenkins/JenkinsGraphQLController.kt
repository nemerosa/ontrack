package net.nemerosa.ontrack.extension.jenkins

import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

@Controller
class JenkinsGraphQLController(
    private val jenkinsConfigurationService: JenkinsConfigurationService,
) {

    @QueryMapping
    fun jenkinsConfigurations(): List<JenkinsConfiguration> =
        jenkinsConfigurationService.configurations.map { it.obfuscate() }

    @QueryMapping
    fun jenkinsConfiguration(@Argument name: String): JenkinsConfiguration? =
        jenkinsConfigurationService.findConfiguration(name)?.obfuscate()

}
