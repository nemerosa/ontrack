package net.nemerosa.ontrack.extension.indicators.ui.graphql

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.extension.indicators.portfolio.IndicatorPortfolioService
import net.nemerosa.ontrack.graphql.schema.GQLRootQuery
import net.nemerosa.ontrack.graphql.support.GraphqlUtils.stdList
import org.springframework.stereotype.Component

@Component
class GQLRootQueryIndicatorPortfolios(
        private val indicatorPortfolio: GQLTypeIndicatorPortfolio,
        private val indicatorPortfolioService: IndicatorPortfolioService
): GQLRootQuery {
    override fun getFieldDefinition(): GraphQLFieldDefinition =
            GraphQLFieldDefinition.newFieldDefinition()
                    .name("indicatorPortfolios")
                    .description("List of indicator portfolios")
                    .type(stdList(indicatorPortfolio.typeRef))
                    .dataFetcher {
                        indicatorPortfolioService.findAll()
                    }
                    .build()
}