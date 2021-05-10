package net.nemerosa.ontrack.extension.indicators.ui.graphql

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.extension.indicators.portfolio.IndicatorPortfolioService
import net.nemerosa.ontrack.graphql.schema.GQLRootQuery
import org.springframework.stereotype.Component

@Component
class GQLRootQueryIndicatorPortfolioOfPortfolios(
        private val indicatorPortfolioOfPortfolios: GQLTypeIndicatorPortfolioOfPortfolios,
        private val indicatorPortfolioService: IndicatorPortfolioService
) : GQLRootQuery {
    override fun getFieldDefinition(): GraphQLFieldDefinition =
            GraphQLFieldDefinition.newFieldDefinition()
                    .name("indicatorPortfolioOfPortfolios")
                    .description("List of all portfolios")
                    .type(indicatorPortfolioOfPortfolios.typeRef)
                    .dataFetcher {
                        indicatorPortfolioService.getPortfolioOfPortfolios()
                    }
                    .build()

}