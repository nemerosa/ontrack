package net.nemerosa.ontrack.extension.workflows.graphql

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.extension.workflows.engine.WorkflowInstance
import net.nemerosa.ontrack.extension.workflows.engine.WorkflowInstanceFilter
import net.nemerosa.ontrack.extension.workflows.engine.WorkflowInstanceStore
import net.nemerosa.ontrack.graphql.schema.GQLRootQuery
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.pagination.GQLPaginatedListFactory
import org.springframework.stereotype.Component

@Component
class GQLRootQueryWorkflowInstances(
    private val gqlPaginatedListFactory: GQLPaginatedListFactory,
    private val gqlTypeWorkflowInstance: GQLTypeWorkflowInstance,
    private val workflowInstanceStore: WorkflowInstanceStore,
) : GQLRootQuery {
    override fun getFieldDefinition(): GraphQLFieldDefinition =
        gqlPaginatedListFactory.createPaginatedField<Any?, WorkflowInstance>(
            cache = GQLTypeCache(),
            fieldName = "workflowInstances",
            fieldDescription = "List of workflow instances",
            itemType = gqlTypeWorkflowInstance.typeName,
            itemPaginatedListProvider = { _, _, offset, size ->
                workflowInstanceStore.findByFilter(
                    WorkflowInstanceFilter(
                        offset = offset,
                        size = size,
                    )
                )
            }
        )
}