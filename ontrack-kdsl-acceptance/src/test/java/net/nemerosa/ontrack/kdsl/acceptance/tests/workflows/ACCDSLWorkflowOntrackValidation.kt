package net.nemerosa.ontrack.kdsl.acceptance.tests.workflows

import net.nemerosa.ontrack.kdsl.acceptance.tests.support.uid
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.waitUntil
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ACCDSLWorkflowOntrackValidation : AbstractACCDSLWorkflowsTestSupport() {

    @Test
    fun `Setting a runtime for an Ontrack validation notification based on the workflow duration`() {
        project {
            branch {
                val vs = validationStamp()
                val workflowName = uid("w-")
                val workflow = """
                    name: $workflowName
                    nodes:
                        - id: start
                          executorId: mock
                          data:
                            text: Start
                            waitMs: 1000
                        - id: validation
                          parents:
                            - id: start
                          executorId: notification
                          data:
                             channel: ontrack-validation
                             channelConfig:
                               validation: ${vs.name}
                               runTime: '${'$'}{#.since?from=workflowInfo.start}'
                """.trimIndent()

                val pl = promotion()
                pl.subscribe(
                    channel = "workflow",
                    channelConfig = mapOf(
                        "workflow" to WorkflowTestSupport.yamlWorkflowToJson(workflow)
                    ),
                    keywords = null,
                    events = listOf(
                        "new_promotion_run",
                    ),
                )

                build {
                    // Triggering the workflow through the promotion
                    promote(pl.name)

                    // Waiting for the build to be validated
                    waitUntil(interval = 1_000L) {
                        getValidationRuns(validationStamp = vs.name).isNotEmpty()
                    }

                    // Checking the validation
                    val run = getValidationRuns(validationStamp = vs.name).firstOrNull()
                    assertNotNull(run, "Build has been validated") {
                        assertEquals("PASSED", it.lastStatus.id, "Passed run")
                        assertNotNull(it.runInfo?.runTime, "Run time has been set") {
                            assertTrue(it > 0, "Run time is set to > 0")
                        }
                    }
                }
            }
        }
    }

}