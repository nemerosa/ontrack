package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.support.GraphQLBeanConverter
import net.nemerosa.ontrack.model.support.NameValue
import org.springframework.stereotype.Component

@Component
class GQLTypeNameValue : GQLType {
    override fun getTypeName(): String = NameValue::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
            GraphQLBeanConverter.asObjectType(NameValue::class, cache)
}