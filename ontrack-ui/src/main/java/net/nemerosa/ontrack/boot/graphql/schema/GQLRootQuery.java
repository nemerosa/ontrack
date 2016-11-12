package net.nemerosa.ontrack.boot.graphql.schema;

import graphql.schema.GraphQLFieldDefinition;

// TODO Builds
// TODO Promotion levels
// TODO Promotion runs
// TODO Validation stamps

/**
 * Provides a root query
 */
public interface GQLRootQuery {

    /**
     * Field definition to use as a field in the root query.
     */
    GraphQLFieldDefinition getFieldDefinition();

}
