package net.nemerosa.ontrack.graphql.schema

import graphql.Scalars.GraphQLInt
import graphql.schema.GraphQLArgument.newArgument
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLFieldDefinition.newFieldDefinition
import graphql.schema.GraphQLNonNull
import net.nemerosa.ontrack.model.exceptions.PromotionLevelNotFoundException
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Component

/**
 * Root query to get a Project using its ID.
 */
@Component
class GQLRootQueryProject(
    private val structureService: StructureService,
    private val gqlTypeProject: GQLTypeProject,
) : GQLRootQuery {

    override fun getFieldDefinition(): GraphQLFieldDefinition {
        return newFieldDefinition()
            .name("project")
            .type(gqlTypeProject.typeRef)
            .argument(
                newArgument()
                    .name("id")
                    .description("ID of the project to look for (required)")
                    .type(GraphQLNonNull(GraphQLInt))
                    .build()
            )
            .dataFetcher { env ->
                // Gets the ID
                val id = env.getArgument<Int>("id") ?: throw IllegalStateException("`id` argument is required")
                // Gets the promotion level
                try {
                    structureService.getProject(ID.of(id))
                } catch (ignored: PromotionLevelNotFoundException) {
                    null
                }
            }
            .build()
    }

}
