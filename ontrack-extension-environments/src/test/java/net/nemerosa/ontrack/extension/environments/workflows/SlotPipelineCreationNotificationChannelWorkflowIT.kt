package net.nemerosa.ontrack.extension.environments.workflows

import net.nemerosa.ontrack.extension.environments.EnvironmentTestSupport
import net.nemerosa.ontrack.extension.environments.SlotPipelineStatus
import net.nemerosa.ontrack.extension.environments.SlotTestSupport
import net.nemerosa.ontrack.extension.environments.service.SlotService
import net.nemerosa.ontrack.extension.workflows.registry.WorkflowParser
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.it.waitUntil
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.TestPropertySource
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

@TestPropertySource(
    properties = [
        "net.nemerosa.ontrack.extension.workflows.store=memory",
        "ontrack.extension.queue.general.async=false",
    ]
)
class SlotPipelineCreationNotificationChannelWorkflowIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var environmentTestSupport: EnvironmentTestSupport

    @Autowired
    private lateinit var slotTestSupport: SlotTestSupport

    @Autowired
    private lateinit var slotService: SlotService

    @Autowired
    private lateinit var slotWorkflowService: SlotWorkflowService

    @OptIn(ExperimentalTime::class)
    @Test
    fun `Creating a pipeline into another slot for the same build using a workflow on deployed`() {
        slotTestSupport.withSlot { staging ->
            environmentTestSupport.withEnvironment { productionEnv ->
                slotTestSupport.withSlot(
                    environment = productionEnv,
                    project = staging.project,
                ) { production ->

                    slotWorkflowService.addSlotWorkflow(
                        SlotWorkflow(
                            slot = staging,
                            trigger = SlotWorkflowTrigger.DEPLOYED,
                            workflow = WorkflowParser.parseYamlWorkflow(
                                """
                                    name: Deployment to production
                                    nodes:
                                        - id: deploy
                                          executorId: notification
                                          data:
                                            channel: slot-pipeline-creation
                                            channelConfig:
                                                environment: ${productionEnv.name}
                                """.trimIndent()
                            )
                        )
                    )

                    // Creating, starting & finishing a pipeline
                    // This will trigger the workflow
                    /* val stagingPipeline = */ slotTestSupport.createStartAndDeployPipeline(slot = staging)

                    // We wait until the new pipeline is registered
                    waitUntil(
                        message = "New pipeline registered in production",
                        timeout = 10.seconds,
                        interval = 1.seconds,
                    ) {
                        slotService.findPipelines(production).pageItems.isNotEmpty()
                    }

                    // Getting the new pipeline
                    val productionPipeline = slotService.findPipelines(production).pageItems.first()
                    assertEquals(SlotPipelineStatus.ONGOING, productionPipeline.status)
                }
            }
        }
    }
}