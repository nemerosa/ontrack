package net.nemerosa.ontrack.extension.workflows.graphql

import net.nemerosa.ontrack.extension.queue.QueueNoAsync
import net.nemerosa.ontrack.extension.workflows.WorkflowTestSupport
import net.nemerosa.ontrack.extension.workflows.engine.WorkflowEngine
import net.nemerosa.ontrack.extension.workflows.registry.WorkflowParser
import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.model.events.dehydrate
import net.nemerosa.ontrack.model.templating.TestTemplatingContextData
import net.nemerosa.ontrack.model.templating.TestTemplatingContextHandler
import net.nemerosa.ontrack.model.templating.createTemplatingContextData
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

@QueueNoAsync
class GQLTypeWorkflowInstanceIT : AbstractQLKTITSupport() {

    @Autowired
    private lateinit var workflowTestSupport: WorkflowTestSupport

    @Autowired
    private lateinit var workflowEngine: WorkflowEngine

    @Autowired
    private lateinit var eventFactory: EventFactory

    @Autowired
    private lateinit var testTemplatingContextHandler: TestTemplatingContextHandler

    @Test
    fun `Getting the event for a workflow instance`() {
        project {
            branch {
                val event = eventFactory.newBranch(this).dehydrate()
                val instance = workflowEngine.startWorkflow(
                    workflow = WorkflowParser.parseYamlWorkflow("""
                        name: Test
                        nodes:
                          - id: start
                            executorId: mock
                            data:
                              text: This is a test
                    """.trimIndent()),
                    event = event,
                    triggerData = workflowTestSupport.testTriggerData(),
                )
                run("""{
                    workflowInstance(id: "${instance.id}") {
                      event {
                        eventType
                        entities {
                          type
                          id
                        }
                        values {
                          name
                          value
                        }
                      }
                    }
                }""".trimIndent()) { data ->

                }
            }
        }
    }

    @Test
    fun `Getting the context data for a workflow instance`() {
        project {
            branch {
                val event = eventFactory.newBranch(this).dehydrate()
                val instance = workflowEngine.startWorkflow(
                    workflow = WorkflowParser.parseYamlWorkflow("""
                        name: Test
                        nodes:
                          - id: start
                            executorId: mock
                            data:
                              text: This is a test
                    """.trimIndent()),
                    event = event,
                    triggerData = workflowTestSupport.testTriggerData(),
                    contexts = mapOf(
                        "deployment" to testTemplatingContextHandler.createTemplatingContextData(
                            TestTemplatingContextData("some-id")
                        )
                    ),
                )
                run("""{
                    workflowInstance(id: "${instance.id}") {
                        contexts {
                            name
                            contextData {
                                id
                                data
                            }
                        }
                    }
                }""".trimIndent()) { data ->
                    assertEquals(
                        mapOf(
                            "workflowInstance" to mapOf(
                                "contexts" to listOf(
                                    mapOf(
                                        "name" to "deployment",
                                        "contextData" to mapOf(
                                            "id" to "test",
                                            "data" to mapOf(
                                                "id" to "some-id"
                                            )
                                        )
                                    )
                                )
                            )
                        ).asJson(),
                        data
                    )
                }
            }
        }
    }

}