package net.nemerosa.ontrack.extension.indicators.ui.graphql

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.indicators.portfolio.IndicatorStatsService
import net.nemerosa.ontrack.extension.indicators.ui.ProjectCategoryIndicators
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.GraphqlUtils.stdList
import org.springframework.stereotype.Component

@Component
class GQLTypeProjectCategoryIndicators(
        private val indicatorCategory: GQLTypeIndicatorCategory,
        private val indicatorCategoryStats: GQLTypeIndicatorCategoryStats,
        private val projectIndicator: GQLTypeProjectIndicator,
        private val indicatorStatsService: IndicatorStatsService
) : GQLType {

    override fun createType(cache: GQLTypeCache): GraphQLObjectType = GraphQLObjectType.newObject()
            .name(typeName)
            .description("Association of an indicator category and a list of associated indicators.")
            .field {
                it.name(ProjectCategoryIndicators::category.name)
                        .description("Indicator category")
                        .type(indicatorCategory.typeRef)
            }
            .field {
                it.name("categoryStats")
                        .description("Indicator stats for this project and category")
                        .type(indicatorCategoryStats.typeRef)
                        .dataFetcher { env ->
                            val projectCategoryIndicators: ProjectCategoryIndicators = env.getSource()
                            indicatorStatsService.getStatsForCategoryAndProject(projectCategoryIndicators.category, projectCategoryIndicators.project)
                        }
            }
            .field {
                it.name(ProjectCategoryIndicators::indicators.name)
                        .description("List of indicators")
                        .type(stdList(projectIndicator.typeRef))
            }
            .build()

    override fun getTypeName(): String = ProjectCategoryIndicators::class.java.simpleName
}