package net.nemerosa.ontrack.graphql.service

import graphql.ExecutionInput
import graphql.ExecutionResult
import graphql.GraphQL
import graphql.execution.ExecutionStrategy
import graphql.execution.instrumentation.dataloader.DataLoaderDispatcherInstrumentation
import graphql.execution.instrumentation.dataloader.DataLoaderDispatcherInstrumentationOptions
import net.nemerosa.ontrack.graphql.schema.GQLDataLoader
import net.nemerosa.ontrack.graphql.schema.GraphqlSchemaService
import net.nemerosa.ontrack.tx.TransactionService
import org.dataloader.DataLoader
import org.dataloader.DataLoaderRegistry
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
        private val gqlDataLoaders: List<GQLDataLoader<*, *>>,
        private val transactionService: TransactionService
) : GraphQLService {

    private val dispatcherInstrumentation: DataLoaderDispatcherInstrumentation by lazy {
        DataLoaderDispatcherInstrumentation(
                DataLoaderDispatcherInstrumentationOptions
                        .newOptions()
                        .includeStatistics(true)
        )
    }

    private val graphQL: GraphQL by lazy {
        GraphQL.newGraphQL(graphqlSchemaService.schema)
                .queryExecutionStrategy(queryExecutionStrategy)
                .mutationExecutionStrategy(mutationExecutionStrategy)
                .instrumentation(dispatcherInstrumentation)
                .build()
    }

    private val dataLoaderRegistry: DataLoaderRegistry by lazy {
        DataLoaderRegistry().apply {
            gqlDataLoaders.forEach { gqlDataLoader ->
                val dataLoader = DataLoader(gqlDataLoader.batchLoader)
                register(gqlDataLoader.key, dataLoader)
            }
        }
    }

    override fun execute(
            query: String,
            variables: Map<String, Any>,
            operationName: String?,
    ): ExecutionResult {
        val result: ExecutionResult = transactionService.doInTransaction {
            graphQL.execute(
                    ExecutionInput.newExecutionInput()
                            .query(query)
                            .dataLoaderRegistry(dataLoaderRegistry)
                            .operationName(operationName)
                            .variables(variables)
                            .build()
            )
        }
        if (result.errors != null && result.errors.isNotEmpty()) {
            result.errors.forEach { error ->
                graphQLExceptionHandlers.forEach {
                    it.handle(error)
                }
            }
        }
        return result
    }
}