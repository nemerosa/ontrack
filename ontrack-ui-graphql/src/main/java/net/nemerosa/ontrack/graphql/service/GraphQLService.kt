package net.nemerosa.ontrack.graphql.service

import graphql.ExecutionResult

/**
 * Execution of GraphQL queries.
 */
interface GraphQLService {
    /**
     * Executes a GraphQL query
     *
     * @param query GraphQL query
     * @param variables List of variables to associate with the query
     * @param operationName Optional name of the operation
     * @return Execution result
     */
    @Throws(GraphQLServiceException::class)
    fun execute(
        query: String,
        variables: Map<String, Any> = mapOf(),
        operationName: String? = null,
    ): ExecutionResult
}