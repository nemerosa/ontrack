package net.nemerosa.ontrack.extension.indicators.ui.graphql

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.indicators.model.IndicatorSource
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.stringField
import org.springframework.stereotype.Component

@Component
class GQLTypeIndicatorSource(
        private val indicatorSourceProviderDescription: GQLTypeIndicatorSourceProviderDescription
) : GQLType {

    override fun createType(cache: GQLTypeCache): GraphQLObjectType = GraphQLObjectType.newObject()
            .name(typeName)
            .description("Indicator source")
            .stringField(IndicatorSource::name, "Indicator source name")
            .field {
                it.name(IndicatorSource::provider.name)
                        .description("Indicator source provider")
                        .type(indicatorSourceProviderDescription.typeRef)
            }
            .build()

    override fun getTypeName(): String = INDICATOR_SOURCE

    companion object {
        val INDICATOR_SOURCE: String = IndicatorSource::class.java.simpleName
    }
}