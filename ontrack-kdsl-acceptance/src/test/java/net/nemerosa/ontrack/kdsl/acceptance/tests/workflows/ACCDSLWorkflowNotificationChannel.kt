package net.nemerosa.ontrack.kdsl.acceptance.tests.workflows

import net.nemerosa.ontrack.json.getRequiredTextField
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.uid
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.waitUntil
import net.nemerosa.ontrack.kdsl.spec.configurations.configurations
import net.nemerosa.ontrack.kdsl.spec.extension.jenkins.JenkinsConfiguration
import net.nemerosa.ontrack.kdsl.spec.extension.jenkins.jenkins
import net.nemerosa.ontrack.kdsl.spec.extension.notifications.notifications
import net.nemerosa.ontrack.kdsl.spec.extension.workflows.WorkflowInstanceNodeStatus
import net.nemerosa.ontrack.kdsl.spec.extension.workflows.WorkflowInstanceStatus
import net.nemerosa.ontrack.kdsl.spec.extension.workflows.workflows
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
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
            val subName = uid("sub-")
            subscribe(
                name = subName,
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

            waitUntil(
                timeout = 30_000,
                interval = 500L,
            ) {
                val instanceId = getWorkflowInstanceId(subName)
                instanceId.isNullOrBlank().not()
            }

            // Getting the instance ID
            val instanceId = getWorkflowInstanceId(subName) ?: fail("Cannot get the workflow instance ID")

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

    @Test
    fun `Workflow node must still contain the build URL in its output when the remote job fails`() {
        // Job to call
        val job = uid("job_")
        // Jenkins configuration
        val jenkinsConfName = uid("j_")
        ontrack.configurations.jenkins.create(
            JenkinsConfiguration(
                name = jenkinsConfName,
                url = "any",
                user = "any",
                password = "any",
            )
        )
        // Defining a workflow
        val workflowName = uid("w-")
        val yaml = """
            name: $workflowName
            nodes:
                - id: start
                  executorId: notification
                  data:
                    channel: mock-jenkins
                    channelConfig:
                        config: $jenkinsConfName
                        job: /mock/$job
                        callMode: SYNC
                        parameters:
                            - name: result
                              value: FAILURE
                            - name: waiting
                              value: 1000
        """.trimIndent()

        project {
            // Subscribe to new branches
            val subName = uid("sub-")
            subscribe(
                name = subName,
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
            waitUntil(
                task = "Getting the workflow instance id",
                timeout = 30_000L,
                interval = 500L,
            ) {
                val instanceId = getWorkflowInstanceId(subName)
                instanceId.isNullOrBlank().not()
            }

            // Getting the instance ID
            val instanceId = getWorkflowInstanceId(subName) ?: fail("Cannot get the workflow instance ID")

            // Waits until the workflow is finished
            val instance = waitUntilWorkflowFinished(
                instanceId = instanceId,
                returnInstanceOnError = true,
            )

            // Checks that the node is marked as success & contains the build URL
            assertNotNull(instance.getWorkflowInstanceNode("start"), "Node accessed") { node ->
                assertTrue(!node.error.isNullOrBlank(), "There is an error")
                assertNotNull(node.output) { output ->
                    val buildUrl = output.path("buildUrl").asText()
                    assertTrue(buildUrl.isNotBlank(), "Build URL must not be blank")
                }
            }
        }
    }

    @Test
    fun `Workflow node contains the build URL while the node is running`() {
        // Job to call
        val job = uid("job_")
        // Jenkins configuration
        val jenkinsConfName = uid("j_")
        ontrack.configurations.jenkins.create(
            JenkinsConfiguration(
                name = jenkinsConfName,
                url = "any",
                user = "any",
                password = "any",
            )
        )
        // Defining a workflow
        val workflowName = uid("w-")
        val yaml = """
            name: $workflowName
            nodes:
                - id: start
                  executorId: notification
                  data:
                    channel: mock-jenkins
                    channelConfig:
                        config: $jenkinsConfName
                        job: /mock/$job
                        callMode: SYNC
                        parameters:
                            - name: waiting
                              value: 5000
        """.trimIndent()



        project {
            // Subscribe to new branches
            val subName = uid("sub-")
            subscribe(
                name = subName,
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
            waitUntil(
                task = "Getting the workflow instance id",
                timeout = 30_000L,
                interval = 500L,
            ) {
                val instanceId = getWorkflowInstanceId(subName)
                instanceId.isNullOrBlank().not()
            }

            // Getting the instance ID
            val instanceId = getWorkflowInstanceId(subName) ?: fail("Cannot get the workflow instance ID")

            // Checks that the node is marked as ongoing & contains the build URL
            waitUntil(interval = 500L, timeout = 2_000L, task = "Waiting for the build URL") {
                val instance = ontrack.workflows.workflowInstance(instanceId) ?: fail("Instance not found")
                val output = instance.getExecutionOutput("start") ?: return@waitUntil false
                val buildUrl = output.path("buildUrl").asText()
                !buildUrl.isNullOrBlank()
            }
            // Waiting for the workflow result
            val instance = waitUntilWorkflowFinished(instanceId)
            // Checks that the node is marked as success & contains the build URL
            assertNotNull(instance.getExecutionOutput("start"), "Output filled in") { output ->
                val buildUrl = output.path("buildUrl").asText()
                assertTrue(buildUrl.isNotBlank(), "Build URL must not be blank")
            }
        }

    }

    @Test
    fun `If a Jenkins sync job fails in a workflow node, the workflow must fail`() {
        // Job to call
        val job = uid("job_")
        // Jenkins configuration
        val jenkinsConfName = uid("j_")
        ontrack.configurations.jenkins.create(
            JenkinsConfiguration(
                name = jenkinsConfName,
                url = "any",
                user = "any",
                password = "any",
            )
        )
        // Defining a workflow
        val workflowName = uid("w-")
        val yaml = """
            name: $workflowName
            nodes:
                - id: start
                  executorId: notification
                  data:
                    channel: mock-jenkins
                    channelConfig:
                        config: $jenkinsConfName
                        job: /mock/$job
                        callMode: SYNC
                        parameters:
                            - name: result
                              value: FAILURE
                - id: end
                  executorId: mock
                  data:
                    text: Should not be called
                  parents:
                    - id: start
        """.trimIndent()

        project {
            branch {
                val subName = uid("sub-")
                val pl = promotion().apply {
                    subscribe(
                        name = subName,
                        channel = "workflow",
                        channelConfig = mapOf(
                            "workflow" to WorkflowTestSupport.yamlWorkflowToJson(yaml)
                        ),
                        keywords = null,
                        events = listOf(
                            "new_promotion_run",
                        ),
                    )
                }
                build {
                    // Promoting to trigger the worflow
                    promote(pl.name)

                    waitUntil(
                        timeout = 30_000,
                        interval = 500L,
                    ) {
                        val instanceId = getWorkflowInstanceId(subName)
                        instanceId.isNullOrBlank().not()
                    }

                    // Getting the instance ID
                    val instanceId = getWorkflowInstanceId(subName) ?: fail("Cannot get the workflow instance ID")

                    // Waits until the workflow is finished
                    val instance = waitUntilWorkflowFinished(instanceId = instanceId, returnInstanceOnError = true)

                    // We expect the workflow to have failed
                    assertEquals(WorkflowInstanceStatus.ERROR, instance.status)

                    // Checking the node states
                    val nodeStatuses = instance.nodesExecutions.associate { it.id to it.status }
                    assertEquals(WorkflowInstanceNodeStatus.ERROR, nodeStatuses["start"])
                    assertEquals(WorkflowInstanceNodeStatus.STOPPED, nodeStatuses["end"])
                }
            }
        }
    }

    /**
     * Gets the workflow instance ID from the output of the notification
     */
    private fun getWorkflowInstanceId(subscriptionName: String) = ontrack.notifications.notificationRecords("workflow")
        .filter { record ->
            record.source?.id == "entity-subscription" &&
                    record.source?.data?.getRequiredTextField("subscriptionName") == subscriptionName
        }
        .firstOrNull()
        ?.result?.output
        ?.path("workflowInstanceId")
        ?.asText()

}