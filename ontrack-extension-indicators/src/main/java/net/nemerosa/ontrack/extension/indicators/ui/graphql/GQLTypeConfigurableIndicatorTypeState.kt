package net.nemerosa.ontrack.extension.indicators.ui.graphql

import graphql.Scalars.GraphQLString
import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.indicators.computing.ConfigurableIndicatorTypeState
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.listType
import net.nemerosa.ontrack.graphql.support.toNotNull
import org.springframework.stereotype.Component

/**
 * GraphQL type for [ConfigurableIndicatorTypeState].
 */
@Component
class GQLTypeConfigurableIndicatorTypeState(
    private val gqlIndicatorComputedCategory: GQLTypeIndicatorComputedCategory,
    private val gqlConfigurableIndicatorAttribute: GQLTypeConfigurableIndicatorAttribute,
    private val gqlConfigurableIndicatorState: GQLTypeConfigurableIndicatorState,
) : GQLType {

    override fun getTypeName(): String = ConfigurableIndicatorTypeState::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("Configurable indicator and its state")
            // Category
            .field {
                it.name("category")
                    .description("Associated category")
                    .type(gqlIndicatorComputedCategory.typeRef.toNotNull())
                    .dataFetcher { env ->
                        val typeState: ConfigurableIndicatorTypeState<*, *> = env.getSource()
                        typeState.type.category
                    }
            }
            // Indicator type ID
            .field {
                it.name("id")
                    .description("Indicator type ID")
                    .type(GraphQLString)
                    .dataFetcher { env ->
                        val typeState: ConfigurableIndicatorTypeState<*, *> = env.getSource()
                        typeState.type.id
                    }
            }
            // Indicator type name
            .field {
                it.name("name")
                    .description("Indicator type name")
                    .type(GraphQLString)
                    .dataFetcher { env ->
                        val typeState: ConfigurableIndicatorTypeState<*, *> = env.getSource()
                        typeState.type.name
                    }
            }
            // List of attributes
            .field {
                it.name("attributes")
                    .description("List of attributes for this type")
                    .type(listType(gqlConfigurableIndicatorAttribute.typeRef))
                    .dataFetcher { env ->
                        val typeState: ConfigurableIndicatorTypeState<*, *> = env.getSource()
                        typeState.type.attributes
                    }
            }
            // State
            .field {
                it.name("state")
                    .description("Saved stated for this configurable indicator")
                    .type(gqlConfigurableIndicatorState.typeRef)
            }
            // OK
            .build()
}