package net.nemerosa.ontrack.extension.indicators.ui.graphql

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.indicators.portfolio.IndicatorTypeStats
import net.nemerosa.ontrack.extension.indicators.ui.ProjectIndicatorType
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.GraphqlUtils.stdList
import org.springframework.stereotype.Component

@Component
class GQLTypeIndicatorTypeStats(
        private val indicatorStats: GQLTypeIndicatorStats,
        private val indicatorType: GQLTypeProjectIndicatorType
) : GQLType {

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
            GraphQLObjectType.newObject()
                    .name(typeName)
                    .description("Association of a type and statistics over several items")
                    .field {
                        it.name(IndicatorTypeStats::type.name)
                                .description("Associated indicator type")
                                .type(indicatorType.typeRef)
                                .dataFetcher { env ->
                                    ProjectIndicatorType(
                                            env.getSource<IndicatorTypeStats>().type
                                    )
                                }
                    }
                    .field {
                        it.name(IndicatorTypeStats::stats.name)
                                .description("Statistics")
                                .type(indicatorStats.typeRef)
                    }
                    .build()

    override fun getTypeName(): String = IndicatorTypeStats::class.java.simpleName
}