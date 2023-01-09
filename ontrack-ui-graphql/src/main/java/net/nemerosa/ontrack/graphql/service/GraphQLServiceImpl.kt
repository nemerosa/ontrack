package net.nemerosa.ontrack.graphql.service

import graphql.ExecutionInput
import graphql.ExecutionResult
import graphql.GraphQL
import graphql.execution.ExecutionStrategy
import graphql.execution.instrumentation.ChainedInstrumentation
import graphql.execution.instrumentation.Instrumentation
import graphql.execution.instrumentation.dataloader.DataLoaderDispatcherInstrumentation
import graphql.execution.instrumentation.dataloader.DataLoaderDispatcherInstrumentationOptions
import graphql.execution.instrumentation.tracing.TracingInstrumentation
import net.nemerosa.ontrack.graphql.OntrackGraphQLConfigProperties
import net.nemerosa.ontrack.graphql.schema.GraphqlSchemaService
import net.nemerosa.ontrack.tx.TransactionService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
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
        private val transactionService: TransactionService,
        private val ontrackGraphQLConfigProperties: OntrackGraphQLConfigProperties,
) : GraphQLService {

    private val logger: Logger = LoggerFactory.getLogger(GraphQLServiceImpl::class.java)

    private val instrumentation: Instrumentation by lazy {
        val instrumentations = mutableListOf<Instrumentation>()
        if (ontrackGraphQLConfigProperties.instrumentation.dataloader) {
            logger.warn("GraphQL data loader instrumentation is enabled.")
            instrumentations += DataLoaderDispatcherInstrumentation(
                    DataLoaderDispatcherInstrumentationOptions
                            .newOptions()
                            .includeStatistics(true)
            )
        }
        if (ontrackGraphQLConfigProperties.instrumentation.tracing) {
            logger.warn("GraphQL tracing instrumentation is enabled.")
            instrumentations += TracingInstrumentation(
                    TracingInstrumentation.Options.newOptions()
                            .includeTrivialDataFetchers(false)
            )
        }
        ChainedInstrumentation(instrumentations)
    }

    private val graphQL: GraphQL by lazy {
        GraphQL.newGraphQL(graphqlSchemaService.schema)
                .queryExecutionStrategy(queryExecutionStrategy)
                .mutationExecutionStrategy(mutationExecutionStrategy)
                .instrumentation(instrumentation)
                .build()
    }

    override fun execute(
            query: String,
            variables: Map<String, Any?>,
            operationName: String?,
    ): ExecutionResult {
        val result: ExecutionResult = transactionService.doInTransaction {
            graphQL.execute(
                    ExecutionInput.newExecutionInput()
                            .query(query)
                            // .dataLoaderRegistry(dataLoaderRegistry)
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