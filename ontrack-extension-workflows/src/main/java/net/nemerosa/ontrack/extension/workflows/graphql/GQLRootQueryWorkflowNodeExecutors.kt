package net.nemerosa.ontrack.extension.workflows.graphql

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.extension.workflows.execution.WorkflowNodeExecutorService
import net.nemerosa.ontrack.graphql.schema.GQLRootQuery
import net.nemerosa.ontrack.graphql.support.booleanArgument
import net.nemerosa.ontrack.graphql.support.listType
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
            .argument(booleanArgument(ARG_ENABLED, "Gets only the matching executors"))
            .dataFetcher { env ->
                val enabled: Boolean? = env.getArgument(ARG_ENABLED)
                workflowNodeExecutorService.executors.filter {
                    if (enabled != null) {
                        it.enabled == enabled
                    } else {
                        true
                    }
                }
            }
            .build()

    companion object {
        const val ARG_ENABLED = "enabled"
    }
}