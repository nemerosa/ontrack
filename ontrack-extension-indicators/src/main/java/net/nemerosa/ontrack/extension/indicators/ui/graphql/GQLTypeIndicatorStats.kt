package net.nemerosa.ontrack.extension.indicators.ui.graphql

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.indicators.portfolio.IndicatorStats
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.intField
import net.nemerosa.ontrack.graphql.support.stringField
import org.springframework.stereotype.Component

@Component
class GQLTypeIndicatorStats : GQLType {

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
            GraphQLObjectType.newObject()
                    .name(typeName)
                    .description("Aggregation of statuses over several items.")
                    .intField(IndicatorStats::total.name, "Total number of items used for this stat")
                    .intField(IndicatorStats::count.name, "Number of items having an actual usable value for stat computation")
                    .stringField(IndicatorStats::min.name, "Minimal value (undefined if no stat available)")
                    .stringField(IndicatorStats::avg.name, "Average value (undefined if no stat available)")
                    .stringField(IndicatorStats::max.name, "Maximal value (undefined if no stat available)")
                    .build()

    override fun getTypeName(): String = IndicatorStats::class.java.simpleName
}