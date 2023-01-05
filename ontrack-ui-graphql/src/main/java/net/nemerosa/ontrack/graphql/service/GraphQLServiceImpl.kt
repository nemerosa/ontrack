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
import net.nemerosa.ontrack.graphql.schema.GQLDataLoader
import net.nemerosa.ontrack.graphql.schema.GraphqlSchemaService
import net.nemerosa.ontrack.model.support.StartupService
import net.nemerosa.ontrack.tx.TransactionService
import org.dataloader.BatchLoaderContextProvider
import org.dataloader.DataLoader
import org.dataloader.DataLoaderOptions
import org.dataloader.DataLoaderRegistry
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.security.core.context.SecurityContextHolder
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
        private val transactionService: TransactionService,
        private val ontrackGraphQLConfigProperties: OntrackGraphQLConfigProperties,
) : GraphQLService, StartupService {

    private val logger: Logger = LoggerFactory.getLogger(GraphQLServiceImpl::class.java)

    private val dispatcherInstrumentation: DataLoaderDispatcherInstrumentation by lazy {
        DataLoaderDispatcherInstrumentation(
                DataLoaderDispatcherInstrumentationOptions
                        .newOptions()
                        .includeStatistics(true)
        )
    }

    private val instrumentation: Instrumentation by lazy {
        val instrumentations = mutableListOf<Instrumentation>()
        instrumentations += dispatcherInstrumentation
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

    private val dataLoaderRegistry: DataLoaderRegistry by lazy {

        val securityContextProvider = BatchLoaderContextProvider {
            SecurityContextHolder.getContext()
        }

        val loaderOptions: DataLoaderOptions = DataLoaderOptions.newOptions()
                .setBatchLoaderContextProvider(securityContextProvider)

        DataLoaderRegistry().apply {
            gqlDataLoaders.forEach { gqlDataLoader ->
                val dataLoader = DataLoader.newDataLoader(gqlDataLoader.batchLoader, loaderOptions)
                register(gqlDataLoader.key, dataLoader)
            }
        }
    }

    override fun getName(): String = "GraphQL service"

    override fun startupOrder(): Int = StartupService.JOB_REGISTRATION

    override fun start() {
        // Forcing the creation of the lazy variables
        logger.info("GraphQL service initialized: $graphQL")
        logger.info("GraphQL data loader registry initialized: ${dataLoaderRegistry.dataLoaders.size} data loaders.")
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