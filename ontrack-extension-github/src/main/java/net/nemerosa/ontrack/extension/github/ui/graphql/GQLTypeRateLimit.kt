package net.nemerosa.ontrack.extension.github.ui.graphql

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.github.client.RateLimit
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.intField
import org.springframework.stereotype.Component

@Component
class GQLTypeRateLimit : GQLType {

    override fun getTypeName(): String = RateLimit::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType = GraphQLObjectType.newObject()
        .name(typeName)
        .description("Rate limit representation")
        .intField(RateLimit::limit, "Absolute limit")
        .intField(RateLimit::remaining, "Remaining calls")
        .intField(RateLimit::used, "Used calls")
        .build()
}