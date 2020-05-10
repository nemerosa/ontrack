package net.nemerosa.ontrack.extension.indicators.ui.graphql

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.indicators.model.IndicatorTypeService
import net.nemerosa.ontrack.extension.indicators.portfolio.IndicatorPortfolioOfPortfolios
import net.nemerosa.ontrack.extension.indicators.portfolio.IndicatorPortfolioService
import net.nemerosa.ontrack.extension.indicators.ui.ProjectIndicatorType
import net.nemerosa.ontrack.graphql.schema.GQLFieldContributor
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.schema.graphQLFieldContributions
import net.nemerosa.ontrack.graphql.support.GraphqlUtils.stdList
import org.springframework.stereotype.Component

@Component
class GQLTypeIndicatorPortfolioOfPortfolios(
        private val indicatorPortfolio: GQLTypeIndicatorPortfolio,
        private val indicatorType: GQLTypeProjectIndicatorType,
        private val fieldContributors: List<GQLFieldContributor>,
        private val indicatorPortfolioService: IndicatorPortfolioService,
        private val indicatorTypeService: IndicatorTypeService
) : GQLType {

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
            GraphQLObjectType.newObject()
                    .name(typeName)
                    .description("List of portfolios")
                    // Types
                    .field {
                        it.name(IndicatorPortfolioOfPortfolios::types.name)
                                .description("Global indicator types")
                                .type(stdList(indicatorType.typeRef))
                                .dataFetcher { env ->
                                    val pp = env.getSource<IndicatorPortfolioOfPortfolios>()
                                    pp.types.mapNotNull { typeId ->
                                        indicatorTypeService.findTypeById(typeId)
                                    }.map { type ->
                                        ProjectIndicatorType(type)
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