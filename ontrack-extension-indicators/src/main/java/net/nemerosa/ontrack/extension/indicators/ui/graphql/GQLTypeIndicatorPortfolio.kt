package net.nemerosa.ontrack.extension.indicators.ui.graphql

import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLTypeReference
import net.nemerosa.ontrack.extension.indicators.model.IndicatorCategoryService
import net.nemerosa.ontrack.extension.indicators.portfolio.IndicatorPortfolio
import net.nemerosa.ontrack.extension.indicators.portfolio.IndicatorPortfolioService
import net.nemerosa.ontrack.extension.indicators.stats.IndicatorStatsService
import net.nemerosa.ontrack.graphql.schema.*
import net.nemerosa.ontrack.graphql.support.GraphqlUtils.stdList
import net.nemerosa.ontrack.graphql.support.stringField
import org.springframework.stereotype.Component

@Component
class GQLTypeIndicatorPortfolio(
        private val indicatorPortfolioService: IndicatorPortfolioService,
        private val label: GQLTypeLabel,
        private val indicatorCategory: GQLTypeIndicatorCategory,
        private val indicatorCategoryService: IndicatorCategoryService,
        private val indicatorCategoryStats: GQLTypeIndicatorCategoryStats,
        private val indicatorStatsService: IndicatorStatsService,
        private val fieldContributors: List<GQLFieldContributor>
) : GQLType {

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
            GraphQLObjectType.newObject()
                    .name(typeName)
                    .description("Grouping indicators for a group of projects identified by labels.")
                    .stringField("id", "ID of the portfolio")
                    .stringField("name", "Name of the portfolio")
                    // Label for this portfolio
                    .field {
                        it.name(IndicatorPortfolio::label.name)
                                .description("Label for this portfolio")
                                .type(label.typeRef)
                                .dataFetcher { env ->
                                    val portfolio: IndicatorPortfolio = env.getSource()
                                    indicatorPortfolioService.getPortfolioLabel(portfolio)
                                }
                    }
                    // Categories
                    .field {
                        it.name(IndicatorPortfolio::categories.name)
                                .description("Indicator categories being shown for this portfolio")
                                .type(stdList(indicatorCategory.typeRef))
                                .dataFetcher { env ->
                                    val portfolio: IndicatorPortfolio = env.getSource()
                                    portfolio.categories.mapNotNull { id ->
                                        indicatorCategoryService.findCategoryById(id)
                                    }
                                }
                    }
                    // Associated projects
                    .field {
                        it.name("projects")
                                .description("List of projects associated with this portfolio")
                                .type(stdList(GraphQLTypeReference(GQLTypeProject.PROJECT)))
                                .dataFetcher { env ->
                                    val portfolio: IndicatorPortfolio = env.getSource()
                                    indicatorPortfolioService.getPortfolioProjects(portfolio)
                                }
                    }
                    // Categories and stats
                    .field {
                        it.name("categoryStats")
                                .description("Stats per category")
                                .type(stdList(indicatorCategoryStats.typeRef))
                                .durationArgument()
                                .dataFetcher { env ->
                                    val duration = env.getDurationArgument()
                                    val portfolio: IndicatorPortfolio = env.getSource()
                                    indicatorStatsService.getStatsPortfolio(portfolio, duration)
                                }
                    }
                    // Stats
                    .field {
                        it.name("globalStats")
                                .description("Global indicator stats")
                                .type(stdList(indicatorCategoryStats.typeRef))
                                .durationArgument()
                                .dataFetcher { env ->
                                    val duration = env.getDurationArgument()
                                    val portfolio: IndicatorPortfolio = env.getSource()
                                    indicatorStatsService.getGlobalStats(portfolio, duration)
                                }
                    }
                    // Links
                    .fields(IndicatorPortfolio::class.java.graphQLFieldContributions(fieldContributors))
                    //OK
                    .build()

    override fun getTypeName(): String = IndicatorPortfolio::class.java.simpleName
}