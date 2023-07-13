package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.graphql.support.stringArgument
import net.nemerosa.ontrack.model.settings.PredefinedPromotionLevelService
import org.springframework.stereotype.Component
import kotlin.jvm.optionals.getOrNull

@Component
class GQLRootQueryPredefinedPromotionLevelByName(
    private val gqlTypePredefinedPromotionLevel: GQLTypePredefinedPromotionLevel,
    private val predefinedPromotionLevelService: PredefinedPromotionLevelService,
) : GQLRootQuery {

    override fun getFieldDefinition(): GraphQLFieldDefinition =
        GraphQLFieldDefinition.newFieldDefinition()
            .name("predefinedPromotionLevelByName")
            .description("Predefined promotion level by name")
            .type(gqlTypePredefinedPromotionLevel.typeRef)
            .argument(stringArgument("name", "Name", nullable = false))
            .dataFetcher { env ->
                val name: String = env.getArgument("name")
                predefinedPromotionLevelService.findPredefinedPromotionLevelByName(name)
                    .getOrNull()
            }
            .build()
}