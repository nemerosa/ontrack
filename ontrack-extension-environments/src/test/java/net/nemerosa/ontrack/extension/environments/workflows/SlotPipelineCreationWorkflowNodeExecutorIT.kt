package net.nemerosa.ontrack.extension.environments.workflows

import net.nemerosa.ontrack.extension.environments.EnvironmentTestSupport
import net.nemerosa.ontrack.extension.environments.SlotPipelineStatus
import net.nemerosa.ontrack.extension.environments.SlotTestSupport
import net.nemerosa.ontrack.extension.environments.service.SlotService
import net.nemerosa.ontrack.extension.environments.workflows.executors.SlotPipelineCreationWorkflowNodeExecutorData
import net.nemerosa.ontrack.extension.notifications.AbstractNotificationTestSupport
import net.nemerosa.ontrack.extension.notifications.subscriptions.subscribe
import net.nemerosa.ontrack.extension.workflows.definition.Workflow
import net.nemerosa.ontrack.extension.workflows.definition.WorkflowNode
import net.nemerosa.ontrack.extension.workflows.notifications.WorkflowNotificationChannel
import net.nemerosa.ontrack.extension.workflows.notifications.WorkflowNotificationChannelConfig
import net.nemerosa.ontrack.extension.workflows.registry.WorkflowParser
import net.nemerosa.ontrack.it.waitUntil
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.test.TestUtils.uid
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
class SlotPipelineCreationWorkflowNodeExecutorIT : AbstractNotificationTestSupport() {

    @Autowired
    private lateinit var environmentTestSupport: EnvironmentTestSupport

    @Autowired
    private lateinit var slotTestSupport: SlotTestSupport

    @Autowired
    private lateinit var slotService: SlotService

    @Autowired
    private lateinit var slotWorkflowService: SlotWorkflowService

    @Autowired
    private lateinit var workflowNotificationChannel: WorkflowNotificationChannel

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
                                          executorId: slot-pipeline-creation
                                          data:
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

    @OptIn(ExperimentalTime::class)
    @Test
    fun `On a promotion, running a workflow to put a pipeline into a slot`() {
        slotTestSupport.withSlot { slot ->
            slot.project.branch {
                val pl = promotionLevel()

                // Promotion subscription running a workflow which pushes the build into a slot
                eventSubscriptionService.subscribe(
                    name = "Start pipeline on promotion",
                    channel = workflowNotificationChannel,
                    channelConfig = WorkflowNotificationChannelConfig(
                        workflow = Workflow(
                            name = "Start pipeline",
                            nodes = listOf(
                                WorkflowNode(
                                    id = "pipeline",
                                    description = null,
                                    executorId = "slot-pipeline-creation",
                                    data = SlotPipelineCreationWorkflowNodeExecutorData(
                                        environment = slot.environment.name,
                                        qualifier = null
                                    ).asJson(),
                                )
                            )
                        )
                    ),
                    projectEntity = pl,
                    keywords = null,
                    origin = "Test",
                    contentTemplate = null,
                    EventFactory.NEW_PROMOTION_RUN,
                )

                build {
                    // Promoting & launching the workflow
                    promote(pl)

                    // Waiting for this build to be into a pipeline
                    waitUntil(
                        message = "Build into pipeline",
                        timeout = 10.seconds,
                        interval = 1.seconds,
                    ) {
                        slotService.getCurrentPipeline(slot)?.build?.id == id
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun `On a promotion, running a workflow to put a pipeline into a slot and send a link to this pipeline`() {
        slotTestSupport.withSlot { slot ->
            slot.project.branch {
                val pl = promotionLevel()

                // Promotion subscription running a workflow which pushes the build into a slot
                val mockChannelTarget = uid("mock-")
                eventSubscriptionService.subscribe(
                    name = "Start pipeline on promotion",
                    channel = workflowNotificationChannel,
                    channelConfig = WorkflowNotificationChannelConfig(
                        workflow = WorkflowParser.parseYamlWorkflow(
                            """
                                name: Start pipeline
                                nodes:
                                  - id: pipeline
                                    executorId: slot-pipeline-creation
                                    data:
                                        environment: ${slot.environment.name}
                                  - id: notification
                                    parents:
                                      - id: pipeline
                                    executorId: notification
                                    data:
                                        channel: mock
                                        channelConfig:
                                            target: $mockChannelTarget
                                            rendererType: html
                                        template: |
                                            Build ${'$'}{build} has started its deployment at ${'$'}{#.pipeline?id=workflow.pipeline.targetPipelineId}
                            """
                        ),
                    ),
                    projectEntity = pl,
                    keywords = null,
                    origin = "Test",
                    contentTemplate = null,
                    EventFactory.NEW_PROMOTION_RUN,
                )

                build {
                    // Promoting & launching the workflow
                    promote(pl)

                    // Waiting for this build to be into a pipeline
                    waitUntil(
                        message = "Build into pipeline",
                        timeout = 10.seconds,
                        interval = 1.seconds,
                    ) {
                        slotService.getCurrentPipeline(slot)?.build?.id == id
                    }

                    // Pipeline
                    val pipeline = slotService.getCurrentPipeline(slot)
                        ?: error("No current pipeline found")

                    // Checking the message
                    val message = mockNotificationChannel.targetMessages(mockChannelTarget).firstOrNull()
                    assertEquals(
                        """
                            Build <a href="http://localhost:8080/#/build/$id">$name</a> has started its deployment at <a href="http://localhost:3000/ui/extension/environments/pipeline/${pipeline.id}">${pipeline.fullName()}</a>
                        """.trimIndent().trim(),
                        message?.trim()
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun `On a promotion, running a workflow to put a pipeline into a slot, deploying it and finishing it`() {
        slotTestSupport.withSlot { slot ->
            slot.project.branch {
                val pl = promotionLevel()

                // Promotion subscription running a workflow which pushes the build into a slot
                val mockChannelTarget = uid("mock-")
                eventSubscriptionService.subscribe(
                    name = "Start pipeline on promotion",
                    channel = workflowNotificationChannel,
                    channelConfig = WorkflowNotificationChannelConfig(
                        workflow = WorkflowParser.parseYamlWorkflow(
                            """
                                name: Start pipeline
                                nodes:
                                  - id: start
                                    executorId: slot-pipeline-creation
                                    data:
                                        environment: ${slot.environment.name}
                                  - id: deploying
                                    parents:
                                      - id: start
                                    executorId: slot-pipeline-deploying
                                    data: {}
                                  - id: deployment
                                    parents:
                                      - id: deploying
                                    executorId: notification
                                    data:
                                        channel: mock
                                        channelConfig:
                                            target: $mockChannelTarget
                                            rendererType: html
                                        template: |
                                            Build ${'$'}{build} has starting its deployment at ${'$'}{#.pipeline?id=workflow.pipeline.targetPipelineId}
                                  - id: deployed
                                    parents:
                                      - id: deployment
                                    executorId: slot-pipeline-deployed
                                    data: {}
                                  - id: deployed-message
                                    parents:
                                      - id: deployed
                                    executorId: notification
                                    data:
                                        channel: mock
                                        channelConfig:
                                            target: $mockChannelTarget
                                            rendererType: html
                                        template: |
                                            Build ${'$'}{build} has been deployed at ${'$'}{#.pipeline?id=workflow.pipeline.targetPipelineId}
                            """
                        ),
                    ),
                    projectEntity = pl,
                    keywords = null,
                    origin = "Test",
                    contentTemplate = null,
                    EventFactory.NEW_PROMOTION_RUN,
                )

                build {
                    // Promoting & launching the workflow
                    promote(pl)

                    // Waiting for this build to be into a pipeline
                    // ... and this pipeline must be deployed
                    waitUntil(
                        message = "Pipeline deployed",
                        timeout = 10.seconds,
                        interval = 1.seconds,
                    ) {
                        val pipeline = slotService.getCurrentPipeline(slot)
                        pipeline?.build?.id == id && pipeline.status == SlotPipelineStatus.DEPLOYED
                    }

                    // Pipeline
                    val pipeline = slotService.getCurrentPipeline(slot)
                        ?: error("No current pipeline found")

                    // Checking the messages
                    val messages = mockNotificationChannel.targetMessages(mockChannelTarget)
                    assertEquals(
                        listOf(
                            """
                                Build <a href="http://localhost:8080/#/build/$id">$name</a> has started its deployment at <a href="http://localhost:3000/ui/extension/environments/pipeline/${pipeline.id}">${pipeline.fullName()}</a>
                            """.trimIndent().trim(),
                            """
                                Build <a href="http://localhost:8080/#/build/$id">$name</a> has been deployed at <a href="http://localhost:3000/ui/extension/environments/pipeline/${pipeline.id}">${pipeline.fullName()}</a>
                            """.trimIndent().trim(),
                        ),
                        messages.map { it.trim() }
                    )
                }
            }
        }
    }
}