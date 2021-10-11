package net.nemerosa.ontrack.extension.github.ui.graphql

import graphql.Scalars.GraphQLString
import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.extension.github.service.GitHubConfigurationService
import net.nemerosa.ontrack.graphql.schema.GQLRootQuery
import net.nemerosa.ontrack.graphql.support.listType
import org.springframework.stereotype.Component

@Component
class GQLRootQueryGitHubConfigurations(
    private val gqlTypeGitHubEngineConfiguration: GQLTypeGitHubEngineConfiguration,
    private val gitHubConfigurationService: GitHubConfigurationService,
) : GQLRootQuery {

    override fun getFieldDefinition(): GraphQLFieldDefinition = GraphQLFieldDefinition.newFieldDefinition()
        .name("gitHubConfigurations")
        .description("List of GitHub configurations")
        .type(listType(gqlTypeGitHubEngineConfiguration.typeRef))
        .argument {
            it.name("name")
                .description("Name of the configuration to get")
                .type(GraphQLString)
        }
        .dataFetcher { env ->
            val name: String? = env.getArgument("name")
            if (name.isNullOrBlank()) {
                gitHubConfigurationService.configurations.map {
                    it.obfuscate()
                }
            } else {
                listOfNotNull(
                    gitHubConfigurationService.getOptionalConfiguration(name).getOrNull()
                )
            }
        }
        .build()
}