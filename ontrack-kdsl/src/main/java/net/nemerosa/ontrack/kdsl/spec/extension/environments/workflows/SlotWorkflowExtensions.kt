package net.nemerosa.ontrack.kdsl.spec.extension.environments.workflows

import net.nemerosa.ontrack.kdsl.connector.graphql.convert
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.environments.CreateSlotWorkflowMutation
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.type.SlotWorkflowTrigger
import net.nemerosa.ontrack.kdsl.connector.graphqlConnector
import net.nemerosa.ontrack.kdsl.spec.extension.environments.Slot

fun Slot.addWorkflow(
    trigger: SlotWorkflowTrigger,
    workflowYaml: String,
) {
    graphqlConnector.mutate(
        CreateSlotWorkflowMutation(
            id,
            trigger,
            workflowYaml
        )
    ) { it?.addSlotWorkflow()?.fragments()?.payloadUserErrors()?.convert() }
}