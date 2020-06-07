package net.nemerosa.ontrack.extension.indicators.ui.graphql

import graphql.Scalars.GraphQLInt
import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.indicators.stats.IndicatorPreviousStats
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.enumAsStringField
import net.nemerosa.ontrack.graphql.support.field
import org.springframework.stereotype.Component

@Component
class GQLTypeIndicatorPreviousStats(
        private val indicatorStats: GQLTypeIndicatorStats
) : GQLType {

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
            GraphQLObjectType.newObject()
                    .name(typeName)
                    .description("Indicator stats from the past")
                    .field(IndicatorPreviousStats::stats, indicatorStats)
                    .field {
                        it.name("durationSeconds")
                                .description("Time (in seconds) since the indicator values were computed or entered.")
                                .type(GraphQLInt)
                                .dataFetcher { env ->
                                    val source = env.getSource<IndicatorPreviousStats>()
                                    (source.period.toMillis() / 1000).toInt()
                                }
                    }
                    .enumAsStringField(IndicatorPreviousStats::minTrend)
                    .enumAsStringField(IndicatorPreviousStats::avgTrend)
                    .enumAsStringField(IndicatorPreviousStats::maxTrend)
                    // OK
                    .build()

    override fun getTypeName(): String = IndicatorPreviousStats::class.java.simpleName
}