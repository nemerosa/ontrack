package net.nemerosa.ontrack.boot.graphql.schema;

import graphql.schema.GraphQLFieldDefinition;

/**
 * Provides a root query
 */
public interface GQLRootQuery {

    /**
     * Field definition to use as a field in the root query.
     */
    GraphQLFieldDefinition getFieldDefinition();

}
