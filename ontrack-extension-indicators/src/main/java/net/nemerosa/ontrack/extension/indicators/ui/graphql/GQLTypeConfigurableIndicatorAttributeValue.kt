package net.nemerosa.ontrack.extension.indicators.ui.graphql

import graphql.Scalars.GraphQLString
import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.indicators.computing.ConfigurableIndicatorAttributeValue
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.toNotNull
import org.springframework.stereotype.Component

@Component
class GQLTypeConfigurableIndicatorAttributeValue(
    private val gqlConfigurableIndicatorAttribute: GQLTypeConfigurableIndicatorAttribute
) : GQLType {

    override fun getTypeName(): String = ConfigurableIndicatorAttributeValue::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType = GraphQLObjectType.newObject()
        .name(typeName)
        .description("Value for a state")
        // Attribute definition
        .field {
            it.name("definition")
                .description("Attribute definition")
                .type(gqlConfigurableIndicatorAttribute.typeRef.toNotNull())
                .dataFetcher { env ->
                    val value: ConfigurableIndicatorAttributeValue = env.getSource()!!
                    value.attribute
                }
        }
        // Attribute key
        .field {
            it.name("key")
                .description("Attribute key")
                .type(GraphQLString.toNotNull())
                .dataFetcher { env ->
                    val value: ConfigurableIndicatorAttributeValue = env.getSource()!!
                    value.attribute.key
                }
        }
        // Attribute value
        .field {
            it.name("value")
                .description("Attribute value")
                .type(GraphQLString)
        }
        // Value
        .build()
}