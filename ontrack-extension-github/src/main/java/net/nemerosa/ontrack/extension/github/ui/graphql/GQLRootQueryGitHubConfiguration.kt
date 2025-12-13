package net.nemerosa.ontrack.extension.github.ui.graphql

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.extension.github.service.GitHubConfigurationService
import net.nemerosa.ontrack.graphql.schema.GQLRootQuery
import net.nemerosa.ontrack.graphql.support.stringArgument
import org.springframework.stereotype.Component

@Component
class GQLRootQueryGitHubConfiguration(
    private val gqlTypeGitHubEngineConfiguration: GQLTypeGitHubEngineConfiguration,
    private val gitHubConfigurationService: GitHubConfigurationService,
) : GQLRootQuery {

    override fun getFieldDefinition(): GraphQLFieldDefinition = GraphQLFieldDefinition.newFieldDefinition()
        .name("gitHubConfiguration")
        .description("Getting a GitHub configuration by name")
        .type(gqlTypeGitHubEngineConfiguration.typeRef)
        .argument(stringArgument("name", "Name of the configuration to get", nullable = false))
        .dataFetcher { env ->
            val name: String = env.getArgument("name")!!
            gitHubConfigurationService.findConfiguration(name)
        }
        .build()
}