package net.nemerosa.ontrack.extension.workflows.graphql

import net.nemerosa.ontrack.extension.workflows.engine.WorkflowInstanceStatus
import net.nemerosa.ontrack.graphql.schema.AbstractGQLEnum
import org.springframework.stereotype.Component

@Component
class GQLEnumWorkflowInstanceStatus : AbstractGQLEnum<WorkflowInstanceStatus>(
    type = WorkflowInstanceStatus::class,
    values = WorkflowInstanceStatus.values(),
    description = "Status of a running workflow",
)