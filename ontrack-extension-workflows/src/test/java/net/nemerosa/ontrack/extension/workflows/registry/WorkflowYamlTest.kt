package net.nemerosa.ontrack.extension.workflows.registry

import net.nemerosa.ontrack.extension.workflows.definition.Workflow
import net.nemerosa.ontrack.extension.workflows.definition.WorkflowNode
import net.nemerosa.ontrack.extension.workflows.definition.WorkflowParentNode
import net.nemerosa.ontrack.json.asJson
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class WorkflowYamlTest {

    @Test
    fun `Parsing of YAML`() {
        val yaml = """
            name: Notifications in a workflow can reuse the output of the parent nodes in their templates
            nodes:
                - id: ticket-creation
                  executorId: notification
                  data:
                    channel: mock
                    channelConfig:
                        target: PPP
                        data: XXX
                    template: Creating the ticket
                - id: mail
                  executorId: notification
                  parents:
                    - id: ticket-creation
                  data:
                    channel: mock
                    channelConfig:
                        target: QQQ
                    template: |
                        Ticket created: ${'$'}{workflow.ticket-creation?path=data}
        """.trimIndent()
        val workflow = WorkflowYaml.parseYamlWorkflow(yaml)
        assertEquals(
            Workflow(
                name = "Notifications in a workflow can reuse the output of the parent nodes in their templates",
                nodes = listOf(
                    WorkflowNode(
                        id = "ticket-creation",
                        executorId = "notification",
                        data = mapOf(
                            "channel" to "mock",
                            "channelConfig" to mapOf(
                                "target" to "PPP",
                                "data" to "XXX",
                            ),
                            "template" to "Creating the ticket"
                        ).asJson(),
                    ),
                    WorkflowNode(
                        id = "mail",
                        parents = listOf(
                            WorkflowParentNode(id = "ticket-creation"),
                        ),
                        executorId = "notification",
                        data = mapOf(
                            "channel" to "mock",
                            "channelConfig" to mapOf(
                                "target" to "QQQ",
                            ),
                            "template" to "Ticket created: \${workflow.ticket-creation?path=data}"
                        ).asJson(),
                    ),
                )
            ),
            workflow
        )
    }

}