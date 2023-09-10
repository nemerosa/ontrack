package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLTypeReference
import net.nemerosa.ontrack.graphql.support.listType
import net.nemerosa.ontrack.graphql.support.stringListArgument
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import net.nemerosa.ontrack.model.structure.PromotionRunService
import org.springframework.stereotype.Component

@Component
class GQLProjectLastBuildsWithPromotionsFieldContributor(
    private val promotionRunService: PromotionRunService,
) : GQLProjectEntityFieldContributor {

    override fun getFields(
        projectEntityClass: Class<out ProjectEntity>,
        projectEntityType: ProjectEntityType,
    ): List<GraphQLFieldDefinition>? {
        return if (projectEntityType == ProjectEntityType.PROJECT) {
            listOf<GraphQLFieldDefinition>(
                GraphQLFieldDefinition.newFieldDefinition()
                    .name("lastBuildsWithPromotions")
                    .description("For each promotion, returns the last promotion run within the project")
                    .type(listType(GraphQLTypeReference(GQLTypePromotionRun.PROMOTION_RUN)))
                    .argument(stringListArgument("promotions", "List of promotion names"))
                    .dataFetcher { env ->
                        val project: Project = env.getSource()
                        val promotionNames: List<String> = env.getArgument("promotions")
                        promotionNames.mapNotNull { promotionName ->
                            promotionRunService.getLastPromotionRunForProject(project, promotionName)
                        }
                    }
                    .build()
            )
        } else {
            emptyList()
        }
    }
}
