package net.nemerosa.ontrack.extension.chart

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.GraphQLBeanConverter
import org.springframework.stereotype.Component

/**
 * GraphQL type for [ChartDefinition].
 */
@Component
class GQLTypeChartDefinition : GQLType {

    override fun getTypeName(): String = ChartDefinition::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLBeanConverter.asObjectType(ChartDefinition::class, cache)
}