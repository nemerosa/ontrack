package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLFieldDefinition

/**
 * Provides a root query
 */
interface GQLRootQuery {
    /**
     * Field definition to use as a field in the root query.
     */
    fun getFieldDefinition(): GraphQLFieldDefinition
}
