package net.nemerosa.ontrack.extension.github.ui.graphql

import graphql.schema.GraphQLFieldDefinition
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
        .dataFetcher {
            gitHubConfigurationService.configurations.map {
                it.obfuscate()
            }
        }
        .build()
}