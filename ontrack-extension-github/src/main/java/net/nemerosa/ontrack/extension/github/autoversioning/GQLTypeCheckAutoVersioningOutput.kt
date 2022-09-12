package net.nemerosa.ontrack.extension.github.autoversioning

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.GraphQLBeanConverter
import org.springframework.stereotype.Component

@Component
class GQLTypeCheckAutoVersioningOutput : GQLType {
    override fun getTypeName(): String = CheckAutoVersioningOutput::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLBeanConverter.asObjectType(CheckAutoVersioningOutput::class, cache)

}
