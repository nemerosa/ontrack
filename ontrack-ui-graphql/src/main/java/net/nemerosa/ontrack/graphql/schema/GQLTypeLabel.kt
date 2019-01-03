package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.support.GraphQLBeanConverter
import net.nemerosa.ontrack.model.labels.Label
import org.springframework.stereotype.Component

@Component
class GQLTypeLabel : GQLType {
    override fun getTypeName(): String = Label::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
            GraphQLBeanConverter.asObjectType(Label::class.java, cache)
}