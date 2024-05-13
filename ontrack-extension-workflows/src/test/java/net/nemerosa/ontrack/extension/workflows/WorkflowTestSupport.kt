package net.nemerosa.ontrack.extension.workflows

import com.fasterxml.jackson.databind.node.TextNode
import net.nemerosa.ontrack.extension.workflows.engine.WorkflowContext
import net.nemerosa.ontrack.extension.workflows.engine.WorkflowContextData
import net.nemerosa.ontrack.extension.workflows.engine.WorkflowEngine
import net.nemerosa.ontrack.extension.workflows.engine.getWorkflowInstance
import net.nemerosa.ontrack.extension.workflows.notifications.WorkflowNotificationChannelNodeExecutor
import net.nemerosa.ontrack.extension.workflows.notifications.WorkflowNotificationItemConverter
import net.nemerosa.ontrack.extension.workflows.registry.WorkflowRegistry
import net.nemerosa.ontrack.it.waitUntil
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.security.SecurityService
import org.springframework.stereotype.Component
import java.util.concurrent.TimeoutException
import kotlin.test.fail
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

@Component
class WorkflowTestSupport(
    private val workflowEngine: WorkflowEngine,
    private val workflowRegistry: WorkflowRegistry,
    private val workflowNotificationItemConverter: WorkflowNotificationItemConverter,
    private val securityService: SecurityService,
) {

    private fun displayInstance(instanceId: String) {
        val workflowInstance = workflowEngine.getWorkflowInstance(instanceId)
        workflowInstance.nodesExecutions.forEach { node ->
            println("node = ${node.id}")
            println("   * status = ${node.status}")
            println("   * start  = ${node.startTime}")
            println("   * end    = ${node.endTime}")
            println("   * output = ${node.output}")
            println("   * error  = ${node.error}")
        }
    }

    @OptIn(ExperimentalTime::class)
    fun registerLaunchAndWaitForWorkflow(
        yaml: String,
        workflowContextName: String? = null,
        event: Event? = null,
        display: Boolean = false,
    ): String {
        return securityService.asAdmin {
            val workflowId = workflowRegistry.saveYamlWorkflow(yaml)
            // Getting the workflow
            val record = workflowRegistry.findWorkflow(workflowId) ?: fail("No workflow found for $workflowId")

            // Context
            val contextData = mutableListOf<WorkflowContextData>()
            if (!workflowContextName.isNullOrBlank()) {
                contextData += WorkflowContextData("mock", TextNode(workflowContextName))
            }
            if (event != null) {
                val item = workflowNotificationItemConverter.convertForQueue(event)
                contextData += WorkflowContextData(WorkflowNotificationChannelNodeExecutor.CONTEXT_EVENT, item.asJson())
            }
            val context = WorkflowContext(contextData)

            // Launching the workflow
            val instance = workflowEngine.startWorkflow(record.workflow, context)
            // Waiting until the workflow is completed (error or success)
            try {
                waitUntil("Waiting until workflow is complete", timeout = 10.seconds) {
                    val workflowInstance = workflowEngine.getWorkflowInstance(instance.id)
                    println("workflowInstance = $workflowInstance")
                    workflowInstance.status.finished
                }
            } catch (any: TimeoutException) {
                // Displaying the state of the instance
                displayInstance(instance.id)
                // Going on with the error
                throw any
            }
            // Displaying
            if (display) {
                displayInstance(instance.id)
            }
            // OK
            instance.id
        }
    }
}