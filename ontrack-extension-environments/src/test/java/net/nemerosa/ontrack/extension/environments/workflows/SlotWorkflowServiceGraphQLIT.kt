package net.nemerosa.ontrack.extension.environments.workflows

import net.nemerosa.ontrack.extension.environments.Slot
import net.nemerosa.ontrack.extension.environments.SlotPipelineStatus
import net.nemerosa.ontrack.extension.environments.SlotTestSupport
import net.nemerosa.ontrack.extension.queue.QueueNoAsync
import net.nemerosa.ontrack.extension.workflows.definition.Workflow
import net.nemerosa.ontrack.extension.workflows.engine.WorkflowInstanceStatus
import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.test.TestUtils.uid
import net.nemerosa.ontrack.test.assertJsonNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

@QueueNoAsync
class SlotWorkflowServiceGraphQLIT : AbstractQLKTITSupport() {

    @Autowired
    private lateinit var slotTestSupport: SlotTestSupport

    @Autowired
    private lateinit var slotWorkflowTestSupport: SlotWorkflowTestSupport

    @Autowired
    private lateinit var slotWorkflowService: SlotWorkflowService

    @Test
    fun `Registering a workflow on a slot`() {
        slotTestSupport.withSlot { slot ->
            val workflowYaml = """
                name: Test
                nodes:
                  - id: start
                    executorId: mock
                    data:
                        text: Start
                  - id: end
                    parents:
                      - id: start
                    executorId: mock
                    data:
                        text: End
            """.trimIndent()
            run(
                """
                    mutation AddSlotWorkflow(
                        ${'$'}workflowYaml: String!,
                    ) {
                        addSlotWorkflow(input: {
                            slotId: "${slot.id}",
                            trigger: RUNNING,
                            workflowYaml: ${'$'}workflowYaml,
                        }) {
                            slotWorkflow {
                                id
                            }
                            errors {
                                message
                            }
                        }
                    }
                """,
                mapOf(
                    "workflowYaml" to workflowYaml,
                )
            ) { data ->
                checkGraphQLUserErrors(data, "addSlotWorkflow") { node ->
                    val workflows = slotWorkflowService.getSlotWorkflowsBySlotAndTrigger(
                        slot = slot,
                        trigger = SlotPipelineStatus.RUNNING,
                    )
                    assertEquals(1, workflows.size)
                    val slotWorkflow = workflows.first()
                    assertEquals(slot, slotWorkflow.slot)
                    assertEquals(SlotPipelineStatus.RUNNING, slotWorkflow.trigger)
                    assertEquals("Test", slotWorkflow.workflow.name)
                }
            }
        }
    }

    @Test
    fun `Saving a workflow on a slot`() {
        slotTestSupport.withSlot { slot ->
            // Adding a workflow
            val workflow = SlotWorkflowTestFixtures.testWorkflow()
            val slotWorkflow = SlotWorkflow(
                slot = slot,
                trigger = SlotPipelineStatus.RUNNING,
                workflow = workflow,
            )
            slotWorkflowService.addSlotWorkflow(slotWorkflow)
            // Changing the workflow using GraphQL
            val newName = uid("w-")
            run(
                """
                    mutation SaveSlotWorkflow(
                        ${'$'}workflow: JSON!,
                    ) {
                        saveSlotWorkflow(input: {
                            id: "${slotWorkflow.id}",
                            slotId: "${slot.id}",
                            trigger: RUNNING,
                            workflow: ${'$'}workflow,
                        }) {
                            slotWorkflow {
                                id
                            }
                            errors {
                                message
                            }
                        }
                    }
                """,
                mapOf(
                    "workflow" to Workflow(
                        name = newName,
                        nodes = workflow.nodes,
                    ).asJson(),
                )
            ) { data ->
                checkGraphQLUserErrors(data, "saveSlotWorkflow") { node ->
                    val savedSlotWorkflow = slotWorkflowService.getSlotWorkflowsBySlotAndTrigger(
                        slot = slot,
                        trigger = SlotPipelineStatus.RUNNING,
                    ).first()
                    assertEquals(slotWorkflow.id, savedSlotWorkflow.id)
                    assertEquals(newName, savedSlotWorkflow.workflow.name)
                    assertEquals(slotWorkflow.workflow.nodes, savedSlotWorkflow.workflow.nodes)
                }
            }
        }
    }

    @Test
    fun `Getting a slot workflow instance by id`() {

        slotWorkflowTestSupport.withSlotWorkflow(
            trigger = SlotPipelineStatus.CANDIDATE,
            waitMs = 2_000,
        ) { slot, slotWorkflow ->
            // Creating a pipeline (this triggers the workflow)
            val pipeline = slotTestSupport.createPipeline(slot = slot)
            // Getting the slot workflow instances for this pipeline (assuming only one)
            val slotWorkflowInstance = slotWorkflowService.getSlotWorkflowInstancesByPipeline(pipeline).firstOrNull()
                ?: fail("Cannot find slot workflow instance")
            // Getting it using GraphQL
            run(
                """
                    query SlotWorkflowInstanceById(${'$'}id: String!) {
                        slotWorkflowInstanceById(id: ${'$'}id) {
                            id
                            pipeline {
                                id
                            }
                            slotWorkflow {
                                id
                            }
                            workflowInstance {
                                id
                                status
                                workflow {
                                    name
                                }
                            }
                        }
                    }
                """, mapOf("id" to slotWorkflowInstance.id)
            ) { data ->
                val node = data.path("slotWorkflowInstanceById")
                assertEquals(slotWorkflowInstance.id, node.path("id").asText())
                assertEquals(slotWorkflowInstance.pipeline.id, node.path("pipeline").path("id").asText())
                assertEquals(slotWorkflowInstance.slotWorkflow.id, node.path("slotWorkflow").path("id").asText())
                val workflowInstanceNode = node.path("workflowInstance")
                assertTrue(workflowInstanceNode.path("id").asText().isNotBlank())
                assertEquals("RUNNING", workflowInstanceNode.path("status").asText())
                assertEquals(slotWorkflow.workflow.name, workflowInstanceNode.path("workflow").path("name").asText())
            }
        }
    }

    @Test
    fun `Getting the workflow instance for a pipeline and specific trigger`() {
        slotWorkflowTestSupport.withSlotWorkflow(
            trigger = SlotPipelineStatus.CANDIDATE,
        ) { slot: Slot, candidateWorkflow: SlotWorkflow ->
            // Adding a 2nd workflow
            val runningWorkflow = SlotWorkflow(
                slot = slot,
                trigger = SlotPipelineStatus.RUNNING,
                workflow = SlotWorkflowTestFixtures.testWorkflow(),
            )
            slotWorkflowService.addSlotWorkflow(runningWorkflow)

            // Creating a pipeline
            val pipeline = slotTestSupport.createPipeline(slot = slot)

            // Waiting for the candidate workflow to be finished
            slotWorkflowTestSupport.waitForSlotWorkflowsToFinish(pipeline, SlotPipelineStatus.CANDIDATE)

            run(
                """
                    {
                        slotById(id: "${slot.id}") {
                            workflows(trigger: CANDIDATE) {
                                slotWorkflowInstanceForPipeline(pipelineId: "${pipeline.id}") {
                                    workflowInstance {
                                        status
                                    }
                                }
                            }
                        }
                    }
                """.trimIndent()
            ) { data ->
                val workflows = data.path("slotById").path("workflows")
                assertEquals(1, workflows.size())
                val workflow = workflows.first()
                assertJsonNotNull(workflow.path("slotWorkflowInstanceForPipeline")) {
                    assertEquals(
                        WorkflowInstanceStatus.SUCCESS.name,
                        path("workflowInstance").path("status").asText()
                    )
                }
            }
        }
    }

}