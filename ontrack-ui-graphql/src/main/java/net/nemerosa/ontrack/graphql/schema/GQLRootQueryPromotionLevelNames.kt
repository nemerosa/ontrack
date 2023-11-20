package net.nemerosa.ontrack.graphql.schema

import graphql.Scalars.GraphQLString
import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.graphql.support.listType
import net.nemerosa.ontrack.graphql.support.stringArgument
import net.nemerosa.ontrack.model.structure.PromotionLevelService
import org.springframework.stereotype.Component

@Component
class GQLRootQueryPromotionLevelNames(
    private val promotionLevelService: PromotionLevelService,
) : GQLRootQuery {

    override fun getFieldDefinition(): GraphQLFieldDefinition = GraphQLFieldDefinition.newFieldDefinition()
        .name("promotionLevelNames")
        .description("Gets a list of existing available promotion level names")
        .argument(stringArgument(ARG_TOKEN, "Part of the name to look for"))
        .type(listType(GraphQLString))
        .dataFetcher { env ->
            val token: String? = env.getArgument(ARG_TOKEN)
            promotionLevelService.findPromotionLevelNames(token)
        }
        .build()

    companion object {
        const val ARG_TOKEN = "token"
    }
}