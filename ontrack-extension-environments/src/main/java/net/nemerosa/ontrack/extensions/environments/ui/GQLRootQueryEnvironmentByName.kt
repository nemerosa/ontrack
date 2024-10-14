package net.nemerosa.ontrack.extensions.environments.ui

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.extensions.environments.service.EnvironmentService
import net.nemerosa.ontrack.graphql.schema.GQLRootQuery
import net.nemerosa.ontrack.graphql.support.stringArgument
import org.springframework.stereotype.Component

@Component
class GQLRootQueryEnvironmentByName(
    private val gqlTypeEnvironment: GQLTypeEnvironment,
    private val environmentService: EnvironmentService,
) : GQLRootQuery {

    override fun getFieldDefinition(): GraphQLFieldDefinition =
        GraphQLFieldDefinition.newFieldDefinition()
            .name("environmentByName")
            .description("Getting an environment using its name")
            .argument(
                stringArgument(ARG_NAME, "Name of the environment", nullable = false)
            )
            .type(gqlTypeEnvironment.typeRef)
            .dataFetcher { env ->
                val name: String = env.getArgument(ARG_NAME)
                environmentService.findByName(name)
            }
            .build()

    companion object {
        private const val ARG_NAME = "name"
    }

}