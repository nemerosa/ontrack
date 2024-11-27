package net.nemerosa.ontrack.extension.environments.workflows

import net.nemerosa.ontrack.extension.environments.SlotTestSupport
import net.nemerosa.ontrack.extension.workflows.definition.Workflow
import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.TestPropertySource
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

@TestPropertySource(
    properties = [
        "ontrack.extension.queue.general.async=false",
    ]
)
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
                            trigger: DEPLOYING,
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
                        trigger = SlotWorkflowTrigger.DEPLOYING,
                    )
                    assertEquals(1, workflows.size)
                    val slotWorkflow = workflows.first()
                    assertEquals(slot, slotWorkflow.slot)
                    assertEquals(SlotWorkflowTrigger.DEPLOYING, slotWorkflow.trigger)
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
                trigger = SlotWorkflowTrigger.DEPLOYING,
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
                            trigger: DEPLOYING,
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
                        trigger = SlotWorkflowTrigger.DEPLOYING,
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
            trigger = SlotWorkflowTrigger.CREATION,
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

}