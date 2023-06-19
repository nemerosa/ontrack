package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLFieldDefinition

/**
 * Provides a list of root queries
 */
interface GQLRootQueries {
    /**
     * Field definition to use as a field in the root query.
     */
    val fieldDefinitions: List<GraphQLFieldDefinition>
}
