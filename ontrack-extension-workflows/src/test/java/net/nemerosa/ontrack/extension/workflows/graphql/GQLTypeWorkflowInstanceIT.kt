package net.nemerosa.ontrack.extension.workflows.graphql

import net.nemerosa.ontrack.extension.workflows.WorkflowTestSupport
import net.nemerosa.ontrack.extension.workflows.engine.WorkflowEngine
import net.nemerosa.ontrack.extension.workflows.registry.WorkflowParser
import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.model.events.dehydrate
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class GQLTypeWorkflowInstanceIT : AbstractQLKTITSupport() {

    @Autowired
    private lateinit var workflowTestSupport: WorkflowTestSupport

    @Autowired
    private lateinit var workflowEngine: WorkflowEngine

    @Autowired
    private lateinit var eventFactory: EventFactory

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

}