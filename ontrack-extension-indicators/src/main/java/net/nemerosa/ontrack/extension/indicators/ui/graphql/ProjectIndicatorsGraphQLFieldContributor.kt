package net.nemerosa.ontrack.extension.indicators.ui.graphql

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.extension.indicators.ui.ProjectIndicatorService
import net.nemerosa.ontrack.graphql.schema.GQLProjectEntityFieldContributor
import net.nemerosa.ontrack.graphql.support.GraphqlUtils.stdList
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import org.springframework.stereotype.Component

@Component
class ProjectIndicatorsGraphQLFieldContributor(
        private val projectIndicators: GQLTypeProjectIndicators,
        private val projectIndicatorService: ProjectIndicatorService
) : GQLProjectEntityFieldContributor {

    override fun getFields(projectEntityClass: Class<out ProjectEntity>, projectEntityType: ProjectEntityType): List<GraphQLFieldDefinition>? {
        return if (projectEntityType == ProjectEntityType.PROJECT) {
            listOf(
                    GraphQLFieldDefinition.newFieldDefinition()
                            .name("projectIndicators")
                            .description("List of project indicators")
                            .type(stdList(projectIndicators.typeRef))
                            .dataFetcher { env ->
                                val project: Project = env.getSource()
                                projectIndicatorService.getProjectIndicators(project.id, true)
                            }
                            .build()
            )
        } else {
            null
        }
    }

}