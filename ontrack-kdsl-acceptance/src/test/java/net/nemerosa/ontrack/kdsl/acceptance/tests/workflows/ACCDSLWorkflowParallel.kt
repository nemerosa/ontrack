package net.nemerosa.ontrack.kdsl.acceptance.tests.workflows

import net.nemerosa.ontrack.kdsl.acceptance.tests.support.uid
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.waitUntil
import net.nemerosa.ontrack.kdsl.spec.extension.workflows.WorkflowInstanceNodeStatus
import net.nemerosa.ontrack.kdsl.spec.extension.workflows.WorkflowInstanceStatus
import net.nemerosa.ontrack.kdsl.spec.extension.workflows.mock.mock
import net.nemerosa.ontrack.kdsl.spec.extension.workflows.workflows
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.fail

class ACCDSLWorkflowParallel : AbstractACCDSLWorkflowsTestSupport() {


    /**
     * Regression test for
     *
     * #1422 Postgres deadlock when dealing with parallel nodes failing at the same time
     */
    @Test
    fun `Parallel nodes running immediately in parallel and one failing`() {
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
                                      - id: csv
                                        executorId: mock
                                        data:
                                            text: CSV
                                      - id: promotion
                                        executorId: mock
                                        data:
                                            text: Promotion
                                      - id: diff
                                        executorId: mock
                                        data:
                                            text: Diff
                                            error: true
                                      - id: test1
                                        executorId: mock
                                        data:
                                            text: Test1
                                      - id: test2
                                        executorId: mock
                                        data:
                                            text: Test2
                                      - id: test3
                                        executorId: mock
                                        data:
                                            text: Test3
                                      - id: test4
                                        executorId: mock
                                        data:
                                            text: Test4
                                      - id: test5
                                        executorId: mock
                                        data:
                                            text: Test5
                                      - id: audit
                                        parents:
                                          - id: promotion
                                        executorId: mock
                                        data:
                                            text: Audit
                                      - id: validation
                                        parents:
                                          - id: csv
                                          - id: diff
                                          - id: test1
                                          - id: test2
                                          - id: test3
                                          - id: test4
                                          - id: test5
                                          - id: audit
                                        executorId: mock
                                        data:
                                            text: Validation
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

                    // Checks the instance is marked as error because of one node
                    assertEquals(WorkflowInstanceStatus.ERROR, instance.status)

                    // Getting the nodes in error
                    val errors = instance.nodesExecutions.filter { it.status == WorkflowInstanceNodeStatus.ERROR }
                    assertEquals(1, errors.size, "Only one node to be failed")
                    val error = errors.first()
                    val message = error.error
                    assertNotNull(message, "Message set") {
                        assertFalse(
                            it.contains("deadlock detected", ignoreCase = true),
                            "No deadlock detected"
                        )
                    }

                }
            }
        }
    }

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