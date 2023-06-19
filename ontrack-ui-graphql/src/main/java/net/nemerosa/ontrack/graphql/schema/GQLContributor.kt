package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLType

/**
 * Contributes objects, types, etc. to a dictionary
 */
interface GQLContributor {

    fun contribute(cache: GQLTypeCache, dictionary: MutableSet<GraphQLType>): Set<GraphQLType>

}