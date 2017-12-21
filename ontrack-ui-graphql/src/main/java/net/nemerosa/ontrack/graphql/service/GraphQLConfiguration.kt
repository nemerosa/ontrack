package net.nemerosa.ontrack.graphql.service

import graphql.execution.AsyncExecutionStrategy
import graphql.execution.AsyncSerialExecutionStrategy
import graphql.execution.ExecutionStrategy
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GraphQLConfiguration {

    /**
     * Execution strategy to use for the query GraphQL executions.
     */
    @Bean
    @ConditionalOnMissingBean
    @Qualifier("queryExecutionStrategy")
    fun queryExecutionStrategy(): ExecutionStrategy = AsyncExecutionStrategy()

    /**
     * Execution strategy to use for the mutation GraphQL executions.
     */
    @Bean
    @ConditionalOnMissingBean
    @Qualifier("queryExecutionStrategy")
    fun mutationExecutionStrategy(): ExecutionStrategy = AsyncSerialExecutionStrategy()

}