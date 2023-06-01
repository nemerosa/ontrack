package net.nemerosa.ontrack.graphql.schema

import graphql.Scalars
import graphql.schema.*
import net.nemerosa.ontrack.graphql.support.listType
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.PromotionRun
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Component

@Component
class GQLRootQueryPromotionRuns(
        private val structureService: StructureService,
        private val promotionRun: GQLTypePromotionRun,
) : GQLRootQuery {

    override fun getFieldDefinition(): GraphQLFieldDefinition {
        return GraphQLFieldDefinition.newFieldDefinition()
                .name("promotionRuns")
                .type(listType(promotionRun.typeRef))
                .argument(
                        GraphQLArgument.newArgument()
                                .name("id")
                                .description("ID of the promotion run to look for")
                                .type(GraphQLNonNull(Scalars.GraphQLInt))
                                .build()
                )
                .dataFetcher(promotionRunFetcher())
                .build()
    }

    private fun promotionRunFetcher() = DataFetcher { environment: DataFetchingEnvironment ->
        val id: Int? = environment.getArgument("id")
        if (id != null) {
            // Fetch by ID
            listOf<PromotionRun>(
                    structureService.getPromotionRun(ID.of(id))
            )
        } else {
            emptyList<Any>()
        }
    }

}
