package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.graphql.support.intArgument
import net.nemerosa.ontrack.model.settings.PredefinedPromotionLevelService
import net.nemerosa.ontrack.model.structure.ID
import org.springframework.stereotype.Component

@Component
class GQLRootQueryPredefinedPromotionLevelById(
    private val gqlTypePredefinedPromotionLevel: GQLTypePredefinedPromotionLevel,
    private val predefinedPromotionLevelService: PredefinedPromotionLevelService,
) : GQLRootQuery {

    override fun getFieldDefinition(): GraphQLFieldDefinition =
        GraphQLFieldDefinition.newFieldDefinition()
            .name("predefinedPromotionLevelById")
            .description("Predefined promotion level by ID")
            .type(gqlTypePredefinedPromotionLevel.typeRef)
            .argument(intArgument("id", "ID", nullable = false))
            .dataFetcher { env ->
                val id: Int = env.getArgument("id")!!
                predefinedPromotionLevelService.getPredefinedPromotionLevel(ID.of(id))
            }
            .build()
}