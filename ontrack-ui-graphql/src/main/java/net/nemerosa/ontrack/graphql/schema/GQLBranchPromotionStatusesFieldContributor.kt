package net.nemerosa.ontrack.graphql.schema

import graphql.Scalars.GraphQLString
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLList
import graphql.schema.GraphQLNonNull
import graphql.schema.GraphQLTypeReference
import net.nemerosa.ontrack.graphql.support.listType
import net.nemerosa.ontrack.model.structure.*
import org.springframework.stereotype.Component
import kotlin.jvm.optionals.getOrNull

@Component
class GQLBranchPromotionStatusesFieldContributor(
    private val structureService: StructureService,
) : GQLProjectEntityFieldContributor {

    override fun getFields(
        projectEntityClass: Class<out ProjectEntity>,
        projectEntityType: ProjectEntityType
    ): List<GraphQLFieldDefinition>? =
        if (projectEntityType == ProjectEntityType.BRANCH) {
            listOf(
                GraphQLFieldDefinition.newFieldDefinition()
                    .name("promotionStatuses")
                    .description("Given a list of promotion names, returns for each one the last promotion run or null if the promotion does not exist.")
                    .argument {
                        it.name("names")
                            .type(GraphQLNonNull(GraphQLList(GraphQLNonNull(GraphQLString))))
                    }
                    .type(listType(GraphQLTypeReference(GQLTypePromotionRun.PROMOTION_RUN)))
                    .dataFetcher { env ->
                        val branch: Branch = env.getSource()!!
                        val names: List<String> = env.getArgument("names") ?: emptyList()
                        val runs = mutableListOf<PromotionRun>()
                        names.forEach { name ->
                            val pl = structureService.findPromotionLevelByName(branch.project.name, branch.name, name)
                                .getOrNull()
                            if (pl != null) {
                                val run = structureService.getLastPromotionRunForPromotionLevel(pl)
                                if (run != null) {
                                    runs += run
                                }
                            }
                        }
                        // OK
                        runs
                    }
                    .build()
            )
        } else {
            null
        }
}