package net.nemerosa.ontrack.extension.workflows.graphql

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.extension.workflows.engine.WorkflowInstanceStatus
import net.nemerosa.ontrack.graphql.schema.GQLRootQuery
import net.nemerosa.ontrack.graphql.support.listType
import net.nemerosa.ontrack.graphql.support.toTypeRef
import org.springframework.stereotype.Component

@Component
class GQLRootQueryWorkflowInstanceStatusList : GQLRootQuery {

    override fun getFieldDefinition(): GraphQLFieldDefinition =
        GraphQLFieldDefinition.newFieldDefinition()
            .name("workflowInstanceStatusList")
            .description("List of workflow instance statuses")
            .type(listType(WorkflowInstanceStatus::class.toTypeRef()))
            .dataFetcher {
                WorkflowInstanceStatus.values()
            }
            .build()
}
