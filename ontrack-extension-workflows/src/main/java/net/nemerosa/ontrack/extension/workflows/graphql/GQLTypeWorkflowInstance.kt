package net.nemerosa.ontrack.extension.workflows.graphql

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.workflows.engine.WorkflowInstance
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.booleanFieldFunction
import net.nemerosa.ontrack.graphql.support.enumField
import net.nemerosa.ontrack.graphql.support.listType
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
            // Status
            .enumField(WorkflowInstance::status)
            // Finished (status)
            .booleanFieldFunction<WorkflowInstance>(
                name = "finished",
                description = "Is the workflow finished?",
            ) {
                it.status.finished
            }
            // Node executions
            .field {
                it.name(WorkflowInstance::nodesExecutions.name)
                    .description("List of node statuses in the workflow")
                    .type(listType(gqlTypeWorkflowInstanceNode.typeRef))
            }
            // OK
            .build()
}