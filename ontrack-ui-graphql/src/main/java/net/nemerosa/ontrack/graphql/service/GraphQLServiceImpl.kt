package net.nemerosa.ontrack.graphql.service

import graphql.ExecutionInput
import graphql.ExecutionResult
import graphql.GraphQL
import graphql.execution.ExecutionStrategy
import graphql.schema.GraphQLSchema
import net.nemerosa.ontrack.graphql.schema.GraphqlSchemaService
import net.nemerosa.ontrack.tx.TransactionService
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class GraphQLServiceImpl(
        private val graphqlSchemaService: GraphqlSchemaService,
        private val graphQLExceptionHandlers: List<GraphQLExceptionHandler>,
        @Qualifier("queryExecutionStrategy")
        private val queryExecutionStrategy: ExecutionStrategy,
        @Qualifier("queryExecutionStrategy")
        private val mutationExecutionStrategy: ExecutionStrategy,
        private val transactionService: TransactionService
) : GraphQLService {

    private val graphQL: GraphQL by lazy {
        GraphQL.newGraphQL(graphqlSchemaService.schema)
                .queryExecutionStrategy(queryExecutionStrategy)
                .mutationExecutionStrategy(mutationExecutionStrategy)
                .build()
    }

    override fun execute(
            query: String,
            variables: Map<String, Any>,
            operationName: String?,
            reportErrors: Boolean
    ): ExecutionResult {
        val result: ExecutionResult = transactionService.doInTransaction {
            graphQL.execute(
                    ExecutionInput.newExecutionInput()
                            .query(query)
                            .operationName(operationName)
                            .variables(variables)
                            .build()
            )
        }
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
    }
}