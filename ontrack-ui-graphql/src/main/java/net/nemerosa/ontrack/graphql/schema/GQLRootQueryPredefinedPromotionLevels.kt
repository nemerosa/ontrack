package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.graphql.support.listType
import net.nemerosa.ontrack.graphql.support.stringArgument
import net.nemerosa.ontrack.graphql.support.toTypeRef
import net.nemerosa.ontrack.model.settings.PredefinedPromotionLevelService
import net.nemerosa.ontrack.model.structure.PredefinedPromotionLevel
import org.springframework.stereotype.Component

@Component
class GQLRootQueryPredefinedPromotionLevels(
    private val predefinedPromotionLevelService: PredefinedPromotionLevelService,
) : GQLRootQuery {

    override fun getFieldDefinition(): GraphQLFieldDefinition =
        GraphQLFieldDefinition.newFieldDefinition()
            .name("predefinedPromotionLevels")
            .description("Returns the list of all predefined promotion levels")
            .argument(stringArgument("name", "Filtering on the predefined promotion level name"))
            .type(listType(PredefinedPromotionLevel::class.toTypeRef()))
            .dataFetcher { env ->
                val name: String? = env.getArgument("name")
                if (name.isNullOrBlank()) {
                    predefinedPromotionLevelService.predefinedPromotionLevels
                } else {
                    predefinedPromotionLevelService.findPredefinedPromotionLevels(name)
                }
            }
            .build()

}