package net.nemerosa.ontrack.extension.indicators.ui.graphql

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.extension.indicators.portfolio.IndicatorPortfolioService
import net.nemerosa.ontrack.graphql.schema.GQLRootQuery
import org.springframework.stereotype.Component

@Component
@Deprecated("Use indicator views instead")
class GQLRootQueryIndicatorPortfolioOfPortfolios(
        private val indicatorPortfolioOfPortfolios: GQLTypeIndicatorPortfolioOfPortfolios,
        private val indicatorPortfolioService: IndicatorPortfolioService
) : GQLRootQuery {
    override fun getFieldDefinition(): GraphQLFieldDefinition =
            GraphQLFieldDefinition.newFieldDefinition()
                    .name("indicatorPortfolioOfPortfolios")
                    .deprecate("Use indicator views. Will be removed in Ontrack V4.")
                    .description("List of all portfolios")
                    .type(indicatorPortfolioOfPortfolios.typeRef)
                    .dataFetcher {
                        indicatorPortfolioService.getPortfolioOfPortfolios()
                    }
                    .build()

}