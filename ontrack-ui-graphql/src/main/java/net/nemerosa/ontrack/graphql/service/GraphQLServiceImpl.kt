package net.nemerosa.ontrack.graphql.service

import graphql.ExecutionResult
import graphql.GraphQL
import graphql.schema.GraphQLSchema
import org.springframework.stereotype.Service

@Service
class GraphQLServiceImpl(
        private val graphQLExceptionHandlers: List<GraphQLExceptionHandler>
) : GraphQLService {
    override fun execute(
            schema: GraphQLSchema,
            query: String,
            variables: Map<String, Any>,
            operationName: String?,
            reportErrors: Boolean
    ): ExecutionResult {
        val result = GraphQL(schema).execute(
                query,
                operationName,
                null,
                variables
        )
        if (result != null) {
            if (result.errors != null && !result.errors.isEmpty() && reportErrors) {
                result.errors.forEach { error ->
                    graphQLExceptionHandlers.forEach {
                        it.handle(error)
                    }
                }
                throw GraphQLServiceException(result.errors)
            } else {
                return result
            }
        } else {
            throw NullPointerException("No execution result returned by the GraphQL query.")
        }
    }
}