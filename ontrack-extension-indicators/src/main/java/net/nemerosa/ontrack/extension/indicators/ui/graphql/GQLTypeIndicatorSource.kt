package net.nemerosa.ontrack.extension.indicators.ui.graphql

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.indicators.model.IndicatorSource
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.stringField
import org.springframework.stereotype.Component

@Component
class GQLTypeIndicatorSource : GQLType {

    override fun createType(cache: GQLTypeCache): GraphQLObjectType = GraphQLObjectType.newObject()
            .name(typeName)
            .description("Indicator source")
            .stringField("id", "Indicator source ID")
            .stringField("name", "Indicator source name")
            .build()

    override fun getTypeName(): String = INDICATOR_SOURCE

    companion object {
        val INDICATOR_SOURCE = IndicatorSource::class.java.simpleName
    }
}