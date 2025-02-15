package net.nemerosa.ontrack.extension.workflows.graphql

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.workflows.engine.WorkflowInstance
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.*
import org.springframework.stereotype.Component

@Component
class GQLTypeWorkflowInstance(
    private val gqlTypeWorkflowInstanceNode: GQLTypeWorkflowInstanceNode,
) : GQLType {

    override fun getTypeName(): String = WorkflowInstance::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("Running workflow instance")
            // ID
            .stringField(WorkflowInstance::id)
            // Timestamp
            .localDateTimeField(WorkflowInstance::timestamp)
            // Status
            .enumField(WorkflowInstance::status)
            // Finished (status)
            .booleanFieldFunction<WorkflowInstance>(
                name = "finished",
                description = "Is the workflow finished?",
            ) {
                it.status.finished
            }
            // Event
            .field(WorkflowInstance::event)
            // Trigger
            .field(WorkflowInstance::triggerData)
            // Node executions
            .field {
                it.name(WorkflowInstance::nodesExecutions.name)
                    .description("List of node statuses in the workflow")
                    .type(listType(gqlTypeWorkflowInstanceNode.typeRef))
            }
            // Workflow
            .field(WorkflowInstance::workflow)
            // Timing
            .localDateTimeField(WorkflowInstance::startTime)
            .localDateTimeField(WorkflowInstance::endTime)
            .longField(WorkflowInstance::durationMs)
            // OK
            .build()
}