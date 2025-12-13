package net.nemerosa.ontrack.extension.indicators.ui.graphql

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.indicators.model.IndicatorCategoryService
import net.nemerosa.ontrack.extension.indicators.model.IndicatorTypeService
import net.nemerosa.ontrack.extension.indicators.model.Rating
import net.nemerosa.ontrack.extension.indicators.portfolio.IndicatorView
import net.nemerosa.ontrack.extension.indicators.stats.IndicatorStatsService
import net.nemerosa.ontrack.graphql.schema.GQLFieldContributor
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.schema.graphQLFieldContributions
import net.nemerosa.ontrack.graphql.support.listType
import net.nemerosa.ontrack.graphql.support.stringField
import org.springframework.stereotype.Component

@Component
class GQLTypeIndicatorView(
    private val indicatorCategory: GQLTypeIndicatorCategory,
    private val indicatorCategoryService: IndicatorCategoryService,
    private val indicatorTypeService: IndicatorTypeService,
    private val indicatorViewProjectReport: GQLTypeIndicatorViewProjectReport,
    private val indicatorReportingService: GQLIndicatorReportingService,
    private val indicatorStatsService: IndicatorStatsService,
    private val fieldContributors: List<GQLFieldContributor>
) : GQLType {

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("List of categories to display for a portfolio or a list of portfolios.")
            // ID
            .stringField(IndicatorView::id, "Unique ID for this view")
            // Name
            .stringField(IndicatorView::name, "Name of the view")
            // Categories
            .field {
                it.name(IndicatorView::categories.name)
                    .description("Selected categories for this view")
                    .type(listType(indicatorCategory.typeRef))
                    .dataFetcher { env ->
                        val view: IndicatorView = env.getSource()!!
                        view.categories.mapNotNull { id ->
                            indicatorCategoryService.findCategoryById(id)
                        }
                    }
            }
            // Project reports - List<IndicatorViewProjectReport>
            .field {
                it.name("reports")
                    .description("List of indicator stats per project for all categories in this view")
                    .type(listType(indicatorViewProjectReport.typeRef))
                    .arguments(indicatorReportingService.arguments)
                    .durationArgument()
                    .rateArgument()
                    .dataFetcher { env ->
                        val view: IndicatorView = env.getSource()!!
                        // Gets the trending duration
                        val duration = env.getDurationArgument()
                        // Gets the rate condition
                        val rate = env.getRateArgument()
                        // Gets the list of all categories
                        val categories = view.categories.mapNotNull(indicatorCategoryService::findCategoryById)
                        // Gets the list of all the types
                        val types = categories.flatMap(indicatorTypeService::findByCategory)
                        // Gets the list of projects for this report, based on field arguments
                        val projects = indicatorReportingService.findProjects(env, types)
                        // Getting the stats for each project
                        projects.map { project ->
                            IndicatorViewProjectReport(
                                project = project,
                                viewStats = categories.map { category ->
                                    indicatorStatsService.getStatsForCategoryAndProject(category, project, duration)
                                }
                            )
                        }.filter { projectReport ->
                            if (rate != null) {
                                // At least one avg rate in one category must be worse or equal to this rate
                                projectReport.viewStats.any { stats ->
                                    stats.stats.avg?.let { candidate ->
                                        Rating.asRating(candidate.value) <= rate
                                    } ?: false
                                }
                            } else {
                                true
                            }
                        }
                    }
            }
            // Links
            .fields(IndicatorView::class.java.graphQLFieldContributions(fieldContributors))
            //OK
            .build()

    override fun getTypeName(): String = IndicatorView::class.java.simpleName
}