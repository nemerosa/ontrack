package net.nemerosa.ontrack.kdsl.acceptance.tests.workflows

import net.nemerosa.ontrack.kdsl.acceptance.tests.support.uid
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.waitUntil
import net.nemerosa.ontrack.kdsl.spec.extension.notifications.notifications
import org.junit.jupiter.api.Test
import kotlin.test.fail

class ACCDSLWorkflowNotificationChannel : AbstractACCDSLWorkflowsTestSupport() {

    @Test
    fun `Notifications reusing their parent output`() {
        // Defining a workflow
        val prefixGroupName = uid("w-")
        val yaml = """
            name: $prefixGroupName
            nodes:
                - id: start
                  executorId: notification
                  data:
                    channel: in-memory
                    channelConfig:
                        group: "start-$prefixGroupName"
                        data: PRJ-123
                    template: |
                        Message for project ${'$'}{project}
                - id: end
                  executorId: notification
                  data:
                    channel: in-memory
                    channelConfig:
                        group: "end-$prefixGroupName"
                    template: |
                        Message for ${'$'}{workflow.start?path=data} in project ${'$'}{project}
                  parents:
                    - id: start
        """.trimIndent()

        project {
            // Subscribe to new branches
            subscribe(
                channel = "workflow",
                channelConfig = mapOf(
                    "workflow" to WorkflowTestSupport.yamlWorkflowToJson(yaml)
                ),
                keywords = null,
                events = listOf(
                    "new_branch",
                ),
            )
            // Creating a branch to trigger the workflow
            branch {}

            // Gets the workflow instance ID from the output of the notification

            fun getWorkflowInstanceId() = ontrack.notifications.notificationRecordsOutputs("workflow")
                .firstOrNull()
                ?.output
                ?.path("workflowInstanceId")
                ?.asText()

            waitUntil(
                timeout = 30_000,
                interval = 500L,
            ) {
                val instanceId = getWorkflowInstanceId()
                instanceId.isNullOrBlank().not()
            }

            // Getting the instance ID
            val instanceId = getWorkflowInstanceId() ?: fail("Cannot get the workflow instance ID")

            // Waits until the workflow is finished
            waitUntilWorkflowFinished(instanceId = instanceId)

            // We expect the messages

            waitUntil(
                timeout = 30_000,
                interval = 500L,
            ) {
                val message = ontrack.notifications.inMemory.group("start-$prefixGroupName").firstOrNull()
                message?.trim() == "Message for project $name"
            }

            waitUntil(
                timeout = 30_000,
                interval = 500L,
            ) {
                val message = ontrack.notifications.inMemory.group("end-$prefixGroupName").firstOrNull()
                message?.trim() == "Message for PRJ-123 in project $name"
            }
        }
    }

}