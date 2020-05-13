package net.nemerosa.ontrack.extension.indicators.ui.graphql

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.indicators.model.IndicatorCategoryService
import net.nemerosa.ontrack.extension.indicators.portfolio.IndicatorPortfolioOfPortfolios
import net.nemerosa.ontrack.extension.indicators.portfolio.IndicatorPortfolioService
import net.nemerosa.ontrack.graphql.schema.GQLFieldContributor
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.schema.graphQLFieldContributions
import net.nemerosa.ontrack.graphql.support.GraphqlUtils.stdList
import org.springframework.stereotype.Component

@Component
class GQLTypeIndicatorPortfolioOfPortfolios(
        private val indicatorPortfolioService: IndicatorPortfolioService,
        private val indicatorPortfolio: GQLTypeIndicatorPortfolio,
        private val indicatorCategory: GQLTypeIndicatorCategory,
        private val indicatorCategoryService: IndicatorCategoryService,
        private val fieldContributors: List<GQLFieldContributor>
) : GQLType {

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
            GraphQLObjectType.newObject()
                    .name(typeName)
                    .description("List of portfolios")
                    // Categories
                    .field {
                        it.name(IndicatorPortfolioOfPortfolios::categories.name)
                                .description("Global indicator categories")
                                .type(stdList(indicatorCategory.typeRef))
                                .dataFetcher { env ->
                                    val pp = env.getSource<IndicatorPortfolioOfPortfolios>()
                                    pp.categories.mapNotNull { id ->
                                        indicatorCategoryService.findCategoryById(id)
                                    }
                                }
                    }
                    // List of portfolios
                    .field {
                        it.name("portfolios")
                                .description("List of portfolios")
                                .type(stdList(indicatorPortfolio.typeRef))
                                .dataFetcher {
                                    indicatorPortfolioService.findAll()
                                }
                    }
                    // Links
                    .fields(IndicatorPortfolioOfPortfolios::class.java.graphQLFieldContributions(fieldContributors))
                    //OK
                    .build()

    override fun getTypeName(): String = IndicatorPortfolioOfPortfolios::class.java.simpleName
}