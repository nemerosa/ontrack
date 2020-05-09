package net.nemerosa.ontrack.extension.indicators.ui.graphql

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.indicators.model.IndicatorTypeService
import net.nemerosa.ontrack.extension.indicators.portfolio.IndicatorPortfolio
import net.nemerosa.ontrack.extension.indicators.portfolio.IndicatorPortfolioService
import net.nemerosa.ontrack.extension.indicators.ui.ProjectIndicatorType
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.schema.GQLTypeLabel
import net.nemerosa.ontrack.graphql.support.GraphqlUtils.stdList
import net.nemerosa.ontrack.graphql.support.stringField
import org.springframework.stereotype.Component

@Component
class GQLTypeIndicatorPortfolio(
        private val label: GQLTypeLabel,
        private val projectIndicatorType: GQLTypeProjectIndicatorType,
        private val indicatorPortfolioService: IndicatorPortfolioService,
        private val indicatorTypeService: IndicatorTypeService
) : GQLType {

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
            GraphQLObjectType.newObject()
                    .name(typeName)
                    .description("Grouping indicators for a group of projects identified by labels.")
                    .stringField("id", "ID of the portfolio")
                    .stringField("name", "Name of the portfolio")
                    // Labels for this protfolio
                    .field {
                        it.name(IndicatorPortfolio::labels.name)
                                .description("Labels for this portfolio")
                                .type(stdList(label.typeRef))
                                .dataFetcher { env ->
                                    val portfolio: IndicatorPortfolio = env.getSource()
                                    indicatorPortfolioService.getPortfolioLabels(portfolio)
                                }
                    }
                    // Types
                    .field {
                        it.name(IndicatorPortfolio::types.name)
                                .description("Indicators being shown for this portfolio")
                                .type(stdList(projectIndicatorType.typeRef))
                                .dataFetcher { env ->
                                    val portfolio: IndicatorPortfolio = env.getSource()
                                    portfolio.types.mapNotNull { id ->
                                        indicatorTypeService.findTypeById(id)
                                    }.map { type ->
                                        ProjectIndicatorType(type)
                                    }
                                }
                    }
                    //OK
                    .build()

    override fun getTypeName(): String = IndicatorPortfolio::class.java.simpleName
}