package net.nemerosa.ontrack.extension.environments.workflows

import net.nemerosa.ontrack.extension.workflows.definition.Workflow
import net.nemerosa.ontrack.extension.workflows.definition.WorkflowNode
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.test.TestUtils.uid

object SlotWorkflowTestFixtures {
    fun testWorkflow(
        name: String = uid("w-"),
        waitMs: Int = 0,
    ) = Workflow(
        name = name,
        nodes = listOf(
            WorkflowNode(
                id = "test",
                executorId = "mock",
                description = "Mock node",
                data = mapOf(
                    "text" to "Test",
                    "waitMs" to waitMs,
                ).asJson(),
            )
        )
    )
}