package net.nemerosa.ontrack.extension.indicators.ui.graphql

import graphql.Scalars.GraphQLString
import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.indicators.model.IndicatorValueType
import net.nemerosa.ontrack.extension.indicators.model.id
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.schema.GQLTypeExtensionFeatureDescription
import net.nemerosa.ontrack.graphql.support.stringField
import org.springframework.stereotype.Component

@Component
class GQLTypeIndicatorValueType(
        private val extensionFeatureDescription: GQLTypeExtensionFeatureDescription
) : GQLType {

    override fun createType(cache: GQLTypeCache): GraphQLObjectType = GraphQLObjectType.newObject()
            .name(typeName)
            .description("Indicator value type")
            .field {
                it.name(IndicatorValueType<*, *>::id.name)
                        .description("FQCN of the value type")
                        .type(GraphQLString)
                        .dataFetcher { env ->
                            env.getSource<IndicatorValueType<*, *>>()!!.id
                        }
            }
            .stringField(IndicatorValueType<*, *>::name, "Display name of the value type")
            .field {
                it.name("feature")
                        .description("Extension feature")
                        .type(extensionFeatureDescription.typeRef)
            }
            .build()

    override fun getTypeName(): String = IndicatorValueType::class.java.simpleName
}
