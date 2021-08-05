package net.nemerosa.ontrack.extension.indicators.ui.graphql

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.indicators.computing.ConfigurableIndicatorAttribute
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.booleanField
import net.nemerosa.ontrack.graphql.support.getDescription
import net.nemerosa.ontrack.graphql.support.stringField
import net.nemerosa.ontrack.graphql.support.toNotNull
import org.springframework.stereotype.Component

@Component
class GQLTypeConfigurableIndicatorAttribute(
    private val gqlConfigurableIndicatorAttributeType: GQLEnumConfigurableIndicatorAttributeType
) : GQLType {

    override fun getTypeName(): String = ConfigurableIndicatorAttribute::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType = GraphQLObjectType.newObject()
        .name(typeName)
        .description("Definition of an attribute for a configurable indicator")
        // Key
        .stringField(ConfigurableIndicatorAttribute::key)
        // Name
        .stringField(ConfigurableIndicatorAttribute::name)
        // Type
        .field {
            it.name(ConfigurableIndicatorAttribute::type.name)
                .description(getDescription(ConfigurableIndicatorAttribute::type))
                .type(gqlConfigurableIndicatorAttributeType.getTypeRef().toNotNull())
        }
        // Required flag
        .booleanField(ConfigurableIndicatorAttribute::required)
        // OK
        .build()
}