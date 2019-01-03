package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.support.GraphQLBeanConverter
import net.nemerosa.ontrack.model.labels.LabelProviderDescription
import org.springframework.stereotype.Component

@Component
class GQLTypeLabelProviderDescription : GQLType {
    override fun getTypeName(): String = LabelProviderDescription::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
            GraphQLBeanConverter.asObjectType(LabelProviderDescription::class.java, cache)
}