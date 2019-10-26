package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLSchema

/**
 * Gets the GraphQL schema to serve.
 */
interface GraphqlSchemaService {


    /**
     * Gets the GraphQL schema to serve.
     * The result should be cached.
     */
    val schema: GraphQLSchema

}
