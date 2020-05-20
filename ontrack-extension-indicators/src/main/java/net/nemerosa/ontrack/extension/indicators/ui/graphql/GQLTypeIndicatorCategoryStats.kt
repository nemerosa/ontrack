package net.nemerosa.ontrack.extension.indicators.ui.graphql

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.indicators.portfolio.IndicatorCategoryStats
import net.nemerosa.ontrack.extension.indicators.portfolio.IndicatorTypeStats
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.field
import org.springframework.stereotype.Component

@Component
class GQLTypeIndicatorCategoryStats(
        private val indicatorStats: GQLTypeIndicatorStats,
        private val indicatorPreviousStats: GQLTypeIndicatorPreviousStats,
        private val indicatorCategory: GQLTypeIndicatorCategory
) : GQLType {

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
            GraphQLObjectType.newObject()
                    .name(typeName)
                    .description("Association of a category and statistics over several items")
                    .field {
                        it.name(IndicatorCategoryStats::category.name)
                                .description("Associated indicator category")
                                .type(indicatorCategory.typeRef)
                    }
                    .field {
                        it.name(IndicatorTypeStats::stats.name)
                                .description("Statistics")
                                .type(indicatorStats.typeRef)
                    }
                    // Previous stats
                    .field(IndicatorCategoryStats::previousStats, indicatorPreviousStats)
                    // OK
                    .build()

    override fun getTypeName(): String = IndicatorCategoryStats::class.java.simpleName
}