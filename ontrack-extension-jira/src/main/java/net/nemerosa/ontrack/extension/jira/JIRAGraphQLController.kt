package net.nemerosa.ontrack.extension.jira

import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

@Controller
class JIRAGraphQLController(
    private val jiraConfigurationService: JIRAConfigurationService,
) {

    @QueryMapping
    fun jiraConfigurations(): List<JIRAConfiguration> =
        jiraConfigurationService.configurations.map { it.obfuscate() }

    @QueryMapping
    fun jiraConfiguration(@Argument name: String): JIRAConfiguration? =
        jiraConfigurationService.findConfiguration(name)?.obfuscate()

}