package net.nemerosa.ontrack.extension.workflows.graphql

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.extension.workflows.engine.WorkflowInstance
import net.nemerosa.ontrack.extension.workflows.engine.WorkflowInstanceFilter
import net.nemerosa.ontrack.extension.workflows.engine.WorkflowInstanceStore
import net.nemerosa.ontrack.extension.workflows.execution.WorkflowNodeExecutorService
import net.nemerosa.ontrack.graphql.schema.GQLRootQuery
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.listType
import net.nemerosa.ontrack.graphql.support.pagination.GQLPaginatedListFactory
import org.springframework.stereotype.Component

@Component
class GQLRootQueryWorkflowNodeExecutors(
    private val gqlTypeWorkflowNodeExecutor: GQLTypeWorkflowNodeExecutor,
    private val workflowNodeExecutorService: WorkflowNodeExecutorService,
) : GQLRootQuery {
    override fun getFieldDefinition(): GraphQLFieldDefinition =
        GraphQLFieldDefinition.newFieldDefinition()
            .name("workflowNodeExecutors")
            .description("List of all workflow node executors")
            .type(listType(gqlTypeWorkflowNodeExecutor.typeRef))
            .dataFetcher {
                workflowNodeExecutorService.executors
            }
            .build()
}