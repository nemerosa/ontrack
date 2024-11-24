package net.nemerosa.ontrack.extension.environments.workflows

import net.nemerosa.ontrack.extension.environments.SlotPipelineStatus
import net.nemerosa.ontrack.extension.environments.SlotTestSupport
import net.nemerosa.ontrack.extension.environments.events.EnvironmentsEventsFactory
import net.nemerosa.ontrack.extension.environments.service.SlotService
import net.nemerosa.ontrack.extension.environments.service.getPipelineById
import net.nemerosa.ontrack.extension.workflows.WorkflowTestSupport
import net.nemerosa.ontrack.extension.workflows.engine.WorkflowInstanceStatus
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.json.asJson
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.TestPropertySource
import kotlin.test.*

@TestPropertySource(
    properties = [
        "net.nemerosa.ontrack.extension.workflows.store=memory",
        "ontrack.extension.queue.general.async=false",
    ]
)
class SlotWorkflowServiceIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var slotTestSupport: SlotTestSupport

    @Autowired
    private lateinit var slotWorkflowService: SlotWorkflowService

    @Autowired
    private lateinit var workflowTestSupport: WorkflowTestSupport

    @Autowired
    private lateinit var slotService: SlotService

    @Autowired
    private lateinit var slotWorkflowTestSupport: SlotWorkflowTestSupport

    @Autowired
    private lateinit var environmentsEventsFactory: EnvironmentsEventsFactory

    @Test
    fun `Registering a workflow on a slot`() {
        slotTestSupport.withSlot { slot ->
            val testWorkflow = SlotWorkflowTestFixtures.testWorkflow()
            slotWorkflowService.addSlotWorkflow(
                SlotWorkflow(
                    slot = slot,
                    trigger = SlotWorkflowTrigger.DEPLOYING,
                    workflow = testWorkflow,
                )
            )
            val workflows = slotWorkflowService.getSlotWorkflowsBySlotAndTrigger(
                slot = slot,
                trigger = SlotWorkflowTrigger.DEPLOYING,
            )
            assertEquals(1, workflows.size)
            val slotWorkflow = workflows.first()
            assertEquals(slot, slotWorkflow.slot)
            assertEquals(SlotWorkflowTrigger.DEPLOYING, slotWorkflow.trigger)
            assertEquals(testWorkflow.name, slotWorkflow.workflow.name)
        }
    }

    @Test
    fun `Running a workflow for a pipeline`() {
        slotTestSupport.withSlotPipeline { pipeline ->
            val testWorkflow = SlotWorkflowTestFixtures.testWorkflow()
            val slotWorkflow = SlotWorkflow(
                slot = pipeline.slot,
                trigger = SlotWorkflowTrigger.DEPLOYING,
                workflow = testWorkflow,
            )
            slotWorkflowService.addSlotWorkflow(
                slotWorkflow
            )

            val event = environmentsEventsFactory.pipelineCreation(pipeline)
            val startedSlotWorkflowInstance = slotWorkflowService.startWorkflow(pipeline, slotWorkflow, event)

            workflowTestSupport.waitForWorkflowInstance(startedSlotWorkflowInstance.workflowInstance.id)

            val slotWorkflowInstances = slotWorkflowService.getSlotWorkflowInstancesByPipeline(pipeline)
            assertEquals(1, slotWorkflowInstances.size)
            val slotWorkflowInstance = slotWorkflowInstances.first()
            assertEquals(startedSlotWorkflowInstance.id, slotWorkflowInstance.id)
            assertEquals(startedSlotWorkflowInstance.pipeline.id, slotWorkflowInstance.pipeline.id)
            assertEquals(startedSlotWorkflowInstance.slotWorkflow.id, slotWorkflowInstance.slotWorkflow.id)
            assertEquals(startedSlotWorkflowInstance.workflowInstance.id, slotWorkflowInstance.workflowInstance.id)
            assertEquals(WorkflowInstanceStatus.SUCCESS, slotWorkflowInstance.workflowInstance.status)
        }
    }

    @Test
    fun `Running a workflow on pipeline creation`() {
        slotWorkflowTestSupport.withSlotWorkflow(trigger = SlotWorkflowTrigger.CREATION) { slot, slotWorkflow ->
            val pipeline = slotTestSupport.createPipeline(slot = slot)

            val slotWorkflowInstance = slotWorkflowService.getSlotWorkflowInstancesByPipeline(pipeline).firstOrNull()
                ?: fail("Expecting a slot workflow instance for the pipeline")

            workflowTestSupport.waitForWorkflowInstance(slotWorkflowInstance.workflowInstance.id)

            val finishedSlotWorkflowInstance = slotWorkflowService.getSlotWorkflowInstanceById(slotWorkflowInstance.id)
            assertEquals(WorkflowInstanceStatus.SUCCESS, finishedSlotWorkflowInstance.workflowInstance.status)
        }
    }

    @Test
    fun `Running a workflow on pipeline deploying`() {
        slotWorkflowTestSupport.withSlotWorkflow(trigger = SlotWorkflowTrigger.DEPLOYING) { slot, slotWorkflow ->
            val pipeline = slotTestSupport.createPipeline(slot = slot)
            slotService.startDeployment(pipeline, dryRun = false)

            val slotWorkflowInstance = slotWorkflowService.getSlotWorkflowInstancesByPipeline(pipeline).firstOrNull()
                ?: fail("Expecting a slot workflow instance for the pipeline")

            workflowTestSupport.waitForWorkflowInstance(slotWorkflowInstance.workflowInstance.id)

            val finishedSlotWorkflowInstance = slotWorkflowService.getSlotWorkflowInstanceById(slotWorkflowInstance.id)
            assertEquals(WorkflowInstanceStatus.SUCCESS, finishedSlotWorkflowInstance.workflowInstance.status)
        }
    }

    @Test
    fun `Running a workflow on pipeline deployed`() {
        slotWorkflowTestSupport.withSlotWorkflow(trigger = SlotWorkflowTrigger.DEPLOYED) { slot, _ ->

            val pipeline = slotTestSupport.createStartAndDeployPipeline(slot = slot)

            val slotWorkflowInstance = slotWorkflowService.getSlotWorkflowInstancesByPipeline(pipeline).firstOrNull()
                ?: fail("Expecting a slot workflow instance for the pipeline")

            workflowTestSupport.waitForWorkflowInstance(slotWorkflowInstance.workflowInstance.id)

            val finishedSlotWorkflowInstance = slotWorkflowService.getSlotWorkflowInstanceById(slotWorkflowInstance.id)
            assertEquals(WorkflowInstanceStatus.SUCCESS, finishedSlotWorkflowInstance.workflowInstance.status)
        }
    }

    @Test
    fun `Workflows on creation participate into the pipeline check list`() {
        slotWorkflowTestSupport.withSlotWorkflow(
            trigger = SlotWorkflowTrigger.CREATION,
            waitMs = 2_000,
        ) { slot, slotWorkflow ->

            // Creating a pipeline (this triggers the workflow)
            val pipeline = slotTestSupport.createPipeline(slot = slot)

            // Pipeline workflow is finished when we reach this code
            // because workflows run synchronously in unit test mode
            val status = slotService.startDeployment(pipeline, dryRun = true)
            assertTrue(status.status, "Pipeline can start deployment")

            // Getting the details of the check
            assertNotNull(status.checks.firstOrNull(), "There is a check for the workflow") { check ->
                assertTrue(check.check.status, "Workflow finished")
                assertEquals("Workflow successful", check.check.reason)
                assertEquals("workflow", check.config.ruleId)
                assertEquals(
                    mapOf(
                        "slotWorkflowId" to slotWorkflow.id
                    ).asJson(),
                    check.config.ruleConfig
                )
                assertEquals(
                    slotWorkflow.workflow.name,
                    check.config.name,
                )
                assertNotNull(
                    check.ruleData?.path("slotWorkflowInstanceId")?.asText(),
                    "slotWorkflowInstanceId is present in the check data"
                ) { slotWorkflowInstanceId ->
                    assertTrue(
                        slotWorkflowInstanceId.isNotBlank(),
                        "slotWorkflowInstanceId is present in the check data"
                    )
                }
            }
        }
    }

    @Test
    fun `Cannot start a deployment if not all workflows are successful`() {
        slotWorkflowTestSupport.withSlotWorkflow(
            trigger = SlotWorkflowTrigger.CREATION,
            error = true,
        ) { slot, _ ->

            // Creating a pipeline (this triggers the workflow)
            val pipeline = slotTestSupport.createPipeline(slot = slot)

            // Trying to start the deployment
            val status = slotService.startDeployment(pipeline, dryRun = false)
            assertFalse(status.status, "Pipeline can not start its deployment")

        }
    }

    @Test
    fun `Cannot finish a deployment if not all workflows are successful`() {
        slotWorkflowTestSupport.withSlotWorkflow(
            trigger = SlotWorkflowTrigger.DEPLOYING,
            error = true,
        ) { slot, _ ->

            // Creating a pipeline
            val pipeline = slotTestSupport.createPipeline(slot = slot)

            // Starting the deployment
            val status = slotService.startDeployment(pipeline, dryRun = false)
            assertTrue(status.status, "Pipeline has started its deployment")

            // Reloading the pipeline's status
            val deployingPipeline = slotService.getPipelineById(pipeline.id)
            assertEquals(SlotPipelineStatus.DEPLOYING, deployingPipeline.status)

            // Finish the deployment
            val end = slotService.finishDeployment(deployingPipeline)
            assertFalse(end.deployed, "Pipeline could not be deployed")

        }
    }

}