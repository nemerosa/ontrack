package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLSchema
import graphql.schema.idl.RuntimeWiring
import graphql.schema.idl.TypeDefinitionRegistry

/**
 * Gets the GraphQL schema to serve.
 */
interface GraphqlSchemaService {


    /**
     * Gets the GraphQL schema to serve.
     */
    fun createSchema(typeDefinitionRegistry: TypeDefinitionRegistry, runtimeWiring: RuntimeWiring): GraphQLSchema

}
