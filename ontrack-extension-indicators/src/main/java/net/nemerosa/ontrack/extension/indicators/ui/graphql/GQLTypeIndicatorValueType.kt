package net.nemerosa.ontrack.extension.indicators.ui.graphql

import graphql.Scalars.GraphQLString
import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.indicators.model.IndicatorValueType
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.schema.GQLTypeExtensionFeatureDescription
import org.springframework.stereotype.Component

@Component
class GQLTypeIndicatorValueType(
        private val extensionFeatureDescription: GQLTypeExtensionFeatureDescription
) : GQLType {

    override fun createType(cache: GQLTypeCache): GraphQLObjectType = GraphQLObjectType.newObject()
            .name(typeName)
            .description("Indicator value type")
            .field {
                it.name("name")
                        .description("FQCN of the value type")
                        .type(GraphQLString)
                        .dataFetcher { env ->
                            env.getSource<IndicatorValueType<*, *>>()::class.java.name
                        }
            }
            .field {
                it.name("feature")
                        .description("Extension feature")
                        .type(extensionFeatureDescription.getTypeRef())
            }
            .build()

    override fun getTypeName(): String = IndicatorValueType::class.java.simpleName
}
