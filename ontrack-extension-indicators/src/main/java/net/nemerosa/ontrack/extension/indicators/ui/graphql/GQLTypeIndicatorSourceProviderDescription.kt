package net.nemerosa.ontrack.extension.indicators.ui.graphql

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.indicators.model.IndicatorSource
import net.nemerosa.ontrack.extension.indicators.model.IndicatorSourceProviderDescription
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.stringField
import org.springframework.stereotype.Component

@Component
class GQLTypeIndicatorSourceProviderDescription : GQLType {

    override fun createType(cache: GQLTypeCache): GraphQLObjectType = GraphQLObjectType.newObject()
            .name(typeName)
            .description("Indicator source provider description")
            .stringField(IndicatorSourceProviderDescription::id.name, "Indicator source provider ID")
            .stringField(IndicatorSourceProviderDescription::name.name, "Indicator source provider name")
            .build()

    override fun getTypeName(): String = IndicatorSourceProviderDescription::class.java.simpleName

}