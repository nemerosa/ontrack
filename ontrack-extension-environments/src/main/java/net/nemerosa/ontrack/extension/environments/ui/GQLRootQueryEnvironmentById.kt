package net.nemerosa.ontrack.extension.environments.ui

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.extension.environments.service.EnvironmentService
import net.nemerosa.ontrack.graphql.schema.GQLRootQuery
import net.nemerosa.ontrack.graphql.support.stringArgument
import org.springframework.stereotype.Component

@Component
class GQLRootQueryEnvironmentById(
    private val gqlTypeEnvironment: GQLTypeEnvironment,
    private val environmentService: EnvironmentService,
) : GQLRootQuery {

    override fun getFieldDefinition(): GraphQLFieldDefinition =
        GraphQLFieldDefinition.newFieldDefinition()
            .name("environmentById")
            .description("Getting an environment using its ID")
            .argument(
                stringArgument(ARG_ID, "ID of the environment", nullable = false)
            )
            .type(gqlTypeEnvironment.typeRef)
            .dataFetcher { env ->
                val id: String = env.getArgument(ARG_ID)
                environmentService.getById(id)
            }
            .build()

    companion object {
        private const val ARG_ID = "id"
    }

}