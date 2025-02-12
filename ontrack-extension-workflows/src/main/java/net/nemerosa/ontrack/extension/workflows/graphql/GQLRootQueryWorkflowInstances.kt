package net.nemerosa.ontrack.extension.workflows.graphql

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.extension.workflows.engine.WorkflowInstance
import net.nemerosa.ontrack.extension.workflows.engine.WorkflowInstanceFilter
import net.nemerosa.ontrack.extension.workflows.repository.WorkflowInstanceRepository
import net.nemerosa.ontrack.graphql.schema.GQLRootQuery
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.pagination.GQLPaginatedListFactory
import net.nemerosa.ontrack.graphql.support.stringArgument
import org.springframework.stereotype.Component

@Component
class GQLRootQueryWorkflowInstances(
    private val gqlPaginatedListFactory: GQLPaginatedListFactory,
    private val gqlTypeWorkflowInstance: GQLTypeWorkflowInstance,
    private val workflowInstanceRepository: WorkflowInstanceRepository,
) : GQLRootQuery {
    override fun getFieldDefinition(): GraphQLFieldDefinition =
        gqlPaginatedListFactory.createPaginatedField<Any?, WorkflowInstance>(
            cache = GQLTypeCache(),
            fieldName = "workflowInstances",
            fieldDescription = "List of workflow instances",
            itemType = gqlTypeWorkflowInstance.typeName,
            arguments = listOf(
                stringArgument(ARG_ID, "ID of a workflow"),
                stringArgument(ARG_NAME, "Name of a workflow"),
            ),
            itemPaginatedListProvider = { env, _, offset, size ->
                val id: String? = env.getArgument(ARG_ID)
                val name: String? = env.getArgument(ARG_NAME)
                workflowInstanceRepository.findInstances(
                    WorkflowInstanceFilter(
                        offset = offset,
                        size = size,
                        id = id,
                        name = name,
                    )
                )
            }
        )

    companion object {
        private const val ARG_NAME = "name"
        private const val ARG_ID = "id"
    }
}