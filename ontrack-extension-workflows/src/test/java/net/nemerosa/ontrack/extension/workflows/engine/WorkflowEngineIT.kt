package net.nemerosa.ontrack.extension.workflows.engine

import net.nemerosa.ontrack.extension.workflows.definition.WorkflowFixtures
import net.nemerosa.ontrack.extension.workflows.mock.MockWorkflowNodeExecutor
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.it.waitUntil
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.TestPropertySource
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@TestPropertySource(
    properties = [
        "net.nemerosa.ontrack.extension.workflows.store=memory",
        "ontrack.extension.queue.general.async=false",
    ]
)
class WorkflowEngineIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var workflowEngine: WorkflowEngine

    @Autowired
    private lateinit var mockWorkflowNodeExecutor: MockWorkflowNodeExecutor

    @Test
    fun `Simple linear workflow`() {
        // Defining a workflow
        val workflow = WorkflowFixtures.simpleLinearWorkflow()
        // Running the workflow
        val instance = workflowEngine.startWorkflow(workflow, mockWorkflowNodeExecutor)
        // Waiting until the workflow is completed (error or success)
        waitUntil("Waiting until workflow is complete", timeout = 10.seconds) {
            val workflowInstance = workflowEngine.getWorkflowInstance(instance.id)
            println("workflowInstance = $workflowInstance")
            workflowInstance.status.finished
        }
    }

    @Test
    fun `Parallel with join`() {
        // Defining a workflow
        val workflow = WorkflowFixtures.twoParallelAndJoin()
        // Running the workflow
        val instance = workflowEngine.startWorkflow(workflow, mockWorkflowNodeExecutor)
        // Waiting until the workflow is completed (error or success)
        waitUntil("Waiting until workflow is complete", timeout = 10.seconds) {
            val workflowInstance = workflowEngine.getWorkflowInstance(instance.id)
            println("workflowInstance = $workflowInstance")
            workflowInstance.status.finished
        }
    }

}