package net.nemerosa.ontrack.kdsl.acceptance.tests.workflows

import net.nemerosa.ontrack.kdsl.acceptance.tests.support.uid
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.waitUntil
import net.nemerosa.ontrack.kdsl.spec.extension.workflows.WorkflowInstanceStatus
import net.nemerosa.ontrack.kdsl.spec.extension.workflows.mock.mock
import net.nemerosa.ontrack.kdsl.spec.extension.workflows.workflows
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class ACCDSLWorkflowParallel : AbstractACCDSLWorkflowsTestSupport() {

    @Test
    fun `Running a parallel workflow in parallel`() {
        project {
            branch {
                val subName = uid("pl-")
                val pl = promotion().apply {
                    subscribe(
                        name = subName,
                        channel = "workflow",
                        channelConfig = mapOf(
                            "workflow" to WorkflowTestSupport.yamlWorkflowToJson(
                                """
                                    name: Parallel workflow
                                    nodes:
                                      - id: start
                                        executorId: mock
                                        data:
                                            text: Start
                                            waitMs: 500
                                      - id: parallel-1
                                        parents:
                                           - id: start
                                        executorId: mock
                                        data:
                                            text: Parallel 1
                                            waitMs: 2000
                                      - id: parallel-2
                                        parents:
                                           - id: start
                                        executorId: mock
                                        data:
                                            text: Parallel 2
                                            waitMs: 1000
                                      - id: end
                                        parents:
                                           - id: parallel-1
                                           - id: parallel-2
                                        executorId: mock
                                        data:
                                            text: End
                                            waitMs: 500
                                """.trimIndent()
                            )
                        ),
                        keywords = null,
                        contentTemplate = null,
                        events = listOf("new_promotion_run"),
                    )
                }

                build {
                    // This launches the workflow
                    promote(pl.name)

                    // Gets the workflow instance ID from the output of the notification
                    waitUntil(
                        task = "Getting the workflow instance id",
                        timeout = 10_000L,
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
                        timeout = 10_000L,
                    )

                    // Checks the instance is marked as success
                    assertEquals(WorkflowInstanceStatus.SUCCESS, instance.status)

                    // Checks the outcome of the workflow run
                    val texts = ontrack.workflows.mock.getTexts(instanceId)
                    assertEquals(
                        listOf(
                            "Processed: Start for null",
                            "Processed: Parallel 2 for null",
                            "Processed: Parallel 1 for null",
                            "Processed: End for null",
                        ),
                        texts
                    )
                }
            }
        }

    }

}