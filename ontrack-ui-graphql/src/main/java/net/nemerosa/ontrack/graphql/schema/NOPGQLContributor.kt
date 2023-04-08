package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLType
import org.springframework.stereotype.Component

@Component
class NOPGQLContributor : GQLContributor {

    override fun contribute(cache: GQLTypeCache, dictionary: MutableSet<GraphQLType>): Set<GraphQLType> = emptySet()

}