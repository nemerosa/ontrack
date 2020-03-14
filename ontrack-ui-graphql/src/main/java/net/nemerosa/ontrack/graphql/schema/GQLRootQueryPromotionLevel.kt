package net.nemerosa.ontrack.graphql.schema

import graphql.Scalars.GraphQLInt
import graphql.schema.GraphQLArgument.newArgument
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLFieldDefinition.newFieldDefinition
import graphql.schema.GraphQLNonNull
import net.nemerosa.ontrack.model.exceptions.PromotionLevelNotFoundException
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.PromotionLevel
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Component

/**
 * Root query to get a [PromotionLevel] using its ID.
 */
@Component
class GQLRootQueryPromotionLevel(
        private val structureService: StructureService,
        private val promotionLevel: GQLTypePromotionLevel
) : GQLRootQuery {

    override fun getFieldDefinition(): GraphQLFieldDefinition {
        return newFieldDefinition()
                .name("promotionLevel")
                .type(promotionLevel.typeRef)
                .argument(
                        newArgument()
                                .name("id")
                                .description("ID of the promotion level to look for (required)")
                                .type(GraphQLNonNull(GraphQLInt))
                                .build()
                )
                .dataFetcher { env ->
                    // Gets the ID
                    val id = env.getArgument<Int>("id") ?: throw  IllegalStateException("`id` argument is required")
                    // Gets the promotion level
                    try {
                        structureService.getPromotionLevel(ID.of(id))
                    } catch (ignored: PromotionLevelNotFoundException) {
                        null
                    }
                }
                .build()
    }

}
