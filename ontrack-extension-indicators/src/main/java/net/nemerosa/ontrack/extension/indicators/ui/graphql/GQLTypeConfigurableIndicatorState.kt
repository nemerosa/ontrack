package net.nemerosa.ontrack.extension.indicators.ui.graphql

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.indicators.computing.ConfigurableIndicatorState
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.booleanField
import net.nemerosa.ontrack.graphql.support.getDescription
import net.nemerosa.ontrack.graphql.support.listType
import net.nemerosa.ontrack.graphql.support.stringField
import org.springframework.stereotype.Component

@Component
class GQLTypeConfigurableIndicatorState(
    private val gqlConfigurableIndicatorAttributeValue: GQLTypeConfigurableIndicatorAttributeValue
) : GQLType {

    override fun getTypeName(): String = ConfigurableIndicatorState::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType = GraphQLObjectType.newObject()
        .name(typeName)
        .description("Stored state for a configurable indicator")
        // Enabled flag
        .booleanField(ConfigurableIndicatorState::enabled)
        // Link
        .stringField(ConfigurableIndicatorState::link)
        // Attributes values
        .field {
            it.name(ConfigurableIndicatorState::values.name)
                .description(getDescription(ConfigurableIndicatorState::values))
                .type(listType(gqlConfigurableIndicatorAttributeValue.typeRef))
        }
        // OK
        .build()
}