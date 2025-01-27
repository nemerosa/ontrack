package net.nemerosa.ontrack.graphql.schema

import graphql.Scalars.GraphQLString
import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.graphql.support.listType
import net.nemerosa.ontrack.graphql.support.stringArgument
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import net.nemerosa.ontrack.model.structure.PromotionLevelService
import org.springframework.stereotype.Component

/**
 * Looks for promotion levels in a project
 */
@Component
class GQLProjectPromotionLevelNamesFieldContributor(
    private val promotionLevelService: PromotionLevelService,
) : GQLProjectEntityFieldContributor {

    override fun getFields(
        projectEntityClass: Class<out ProjectEntity>,
        projectEntityType: ProjectEntityType,
    ): List<GraphQLFieldDefinition>? {
        return if (projectEntityType == ProjectEntityType.PROJECT) {
            listOf<GraphQLFieldDefinition>(
                GraphQLFieldDefinition.newFieldDefinition()
                    .name("promotionLevelNames")
                    .description("Looking for promotion level names in this project")
                    .type(listType(GraphQLString))
                    .argument(stringArgument("token", "Filter by promotion level name"))
                    .dataFetcher { env ->
                        val project: Project = env.getSource()
                        val token: String? = env.getArgument<String>("token")
                        promotionLevelService.findPromotionLevelNamesByProject(project, token)
                    }
                    .build()
            )
        } else {
            emptyList()
        }
    }
}
