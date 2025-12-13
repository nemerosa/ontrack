package net.nemerosa.ontrack.kdsl.spec.extension.environments.workflows

import net.nemerosa.ontrack.kdsl.connector.graphql.convert
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.CreateSlotWorkflowMutation
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.type.SlotPipelineStatus
import net.nemerosa.ontrack.kdsl.connector.graphqlConnector
import net.nemerosa.ontrack.kdsl.spec.extension.environments.Slot

fun Slot.addWorkflow(
    trigger: SlotPipelineStatus,
    workflowYaml: String,
) {
    graphqlConnector.mutate(
        CreateSlotWorkflowMutation(
            id,
            trigger,
            workflowYaml
        )
    ) { it?.addSlotWorkflow?.payloadUserErrors?.convert() }
}