package net.nemerosa.ontrack.extension.workflows

import net.nemerosa.ontrack.extension.workflows.engine.WorkflowEngine
import net.nemerosa.ontrack.extension.workflows.engine.getWorkflowInstance
import net.nemerosa.ontrack.extension.workflows.registry.WorkflowRegistry
import net.nemerosa.ontrack.it.waitUntil
import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.events.MockEventType
import net.nemerosa.ontrack.model.events.SerializableEventService
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.templating.TemplatingContextData
import net.nemerosa.ontrack.model.trigger.TestTrigger
import net.nemerosa.ontrack.model.trigger.TestTriggerData
import net.nemerosa.ontrack.model.trigger.createTriggerData
import org.springframework.stereotype.Component
import java.util.concurrent.TimeoutException
import kotlin.test.fail
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

@Component
class WorkflowTestSupport(
    private val workflowEngine: WorkflowEngine,
    private val workflowRegistry: WorkflowRegistry,
    private val securityService: SecurityService,
    private val serializableEventService: SerializableEventService,
    private val testTrigger: TestTrigger,
) {

    fun testTriggerData(message: String? = null) =
        testTrigger.createTriggerData(
            TestTriggerData(message = message)
        )

    private fun displayInstance(instanceId: String) {
        val workflowInstance = workflowEngine.getWorkflowInstance(instanceId)
        println("status = ${workflowInstance.status}")
        println("durations (ms) = ${workflowInstance.durationMs}")
        workflowInstance.nodesExecutions.forEach { node ->
            println("node = ${node.id}")
            println("   * status = ${node.status}")
            println("   * start  = ${node.startTime}")
            println("   * end    = ${node.endTime}")
            println("   * output = ${node.output}")
            println("   * error  = ${node.error}")
        }
    }

    fun registerLaunchAndWaitForWorkflow(
        yaml: String,
        workflowContextName: String? = null,
        event: Event? = null,
        contexts: Map<String, TemplatingContextData> = emptyMap(),
        display: Boolean = false,
        wait: Boolean = true,
    ): String {
        return securityService.asAdmin {
            val workflowId = workflowRegistry.saveYamlWorkflow(yaml)

            // Getting the workflow
            val record = workflowRegistry.findWorkflow(workflowId) ?: fail("No workflow found for $workflowId")

            // Actual event
            val actualEvent = event ?: MockEventType.mockEvent(
                workflowContextName
                    ?.takeIf { it.isNotBlank() }
                    ?: "No name"
            )

            // Workflow event
            val serializableEvent = serializableEventService.dehydrate(actualEvent)

            // Launching the workflow
            val instance =
                workflowEngine.startWorkflow(
                    workflow = record.workflow,
                    event = serializableEvent,
                    contexts = contexts,
                    triggerData = testTrigger.createTriggerData(TestTriggerData()),
                )
            // Waiting until the workflow is completed (error or success)
            if (wait) {
                waitForWorkflowInstance(instance.id)
            }
            // Displaying
            if (display) {
                displayInstance(instance.id)
            }
            // OK
            instance.id
        }
    }

    @OptIn(ExperimentalTime::class)
    fun waitForWorkflowInstance(instanceId: String, timeout: Duration = 10.seconds) {
        try {
            waitUntil("Waiting until workflow is complete", timeout = timeout, interval = 1.seconds) {
                val workflowInstance = workflowEngine.getWorkflowInstance(instanceId)
                println("workflowInstance = $workflowInstance")
                workflowInstance.status.finished
            }
        } catch (any: TimeoutException) {
            // Displaying the state of the instance
            displayInstance(instanceId)
            // Going on with the error
            throw any
        }
    }
}