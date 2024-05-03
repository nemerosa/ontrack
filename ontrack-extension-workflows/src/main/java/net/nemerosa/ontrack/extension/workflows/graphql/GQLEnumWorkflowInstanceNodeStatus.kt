package net.nemerosa.ontrack.extension.workflows.graphql

import net.nemerosa.ontrack.extension.workflows.engine.WorkflowInstanceNodeStatus
import net.nemerosa.ontrack.graphql.schema.AbstractGQLEnum
import org.springframework.stereotype.Component

@Component
class GQLEnumWorkflowInstanceNodeStatus : AbstractGQLEnum<WorkflowInstanceNodeStatus>(
    type = WorkflowInstanceNodeStatus::class,
    values = WorkflowInstanceNodeStatus.values(),
    description = "Status of a node in a workflow",
)