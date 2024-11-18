package net.nemerosa.ontrack.extension.workflows.engine

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.workflows.definition.Workflow
import net.nemerosa.ontrack.model.events.SerializableEvent
import java.time.Duration
import java.time.LocalDateTime

/**
 * Information about the execution of a workflow.
 *
 * @property id Unique ID for this workflow instance.
 * @property timestamp Last time the instance was updated.
 * @property workflow Associated workflow
 * @property event Serializable event linked to the workflow
 * @property nodesExecutions Information about the node executions
 * @property status Status of the execution of this workflow
 */
data class WorkflowInstance(
    val id: String,
    val timestamp: LocalDateTime,
    val workflow: Workflow,
    val event: SerializableEvent,
    val nodesExecutions: List<WorkflowInstanceNode>,
) {

    companion object {
        const val EVENT_INSTANCE_ID = "workflowInstanceId"
    }

    @get:JsonIgnore
    val startTime: LocalDateTime? by lazy {
        nodesExecutions.mapNotNull { it.startTime }.minOrNull()
    }

    @get:JsonIgnore
    val endTime: LocalDateTime? by lazy {
        nodesExecutions.mapNotNull { it.endTime }.maxOrNull()
    }

    @get:JsonIgnore
    val durationMs: Long by lazy {
        if (startTime != null && endTime != null) {
            Duration.between(startTime, endTime).toMillis()
        } else {
            0
        }
    }

    @get:JsonIgnore
    val status: WorkflowInstanceStatus
        get() {
            val nodes = nodesExecutions.map { it.status }
            return if (nodes.any { it == WorkflowInstanceNodeStatus.ERROR }) {
                WorkflowInstanceStatus.ERROR
            } else if (nodes.any { it == WorkflowInstanceNodeStatus.STOPPED }) {
                WorkflowInstanceStatus.STOPPED
            } else if (nodes.all { it == WorkflowInstanceNodeStatus.SUCCESS }) {
                WorkflowInstanceStatus.SUCCESS
            } else if (nodes.any { it == WorkflowInstanceNodeStatus.STARTED }) {
                WorkflowInstanceStatus.RUNNING
            } else {
                WorkflowInstanceStatus.STARTED
            }
        }

    private fun updateContext(
        eventToMerge: SerializableEvent? = null,
    ) = WorkflowInstance(
        id = id,
        timestamp = Time.now,
        workflow = workflow,
        event = eventToMerge ?: event,
        nodesExecutions = nodesExecutions,
    )

    private fun updateNode(nodeId: String, update: (node: WorkflowInstanceNode) -> WorkflowInstanceNode) =
        WorkflowInstance(
            id = id,
            timestamp = Time.now,
            workflow = workflow,
            event = event,
            nodesExecutions = nodesExecutions.map { node ->
                if (node.id == nodeId) {
                    update(node)
                } else {
                    node
                }
            },
        )

    fun startNode(nodeId: String, time: LocalDateTime = Time.now) = updateNode(nodeId) { node ->
        node.start(time)
    }

    fun successNode(
        nodeId: String,
        output: JsonNode,
        eventToMerge: SerializableEvent? = null,
    ) = updateNode(nodeId) { node ->
        node.success(output)
    }.run {
        updateContext(eventToMerge)
    }

    fun errorNode(nodeId: String, throwable: Throwable?, message: String?, output: JsonNode?) =
        updateNode(nodeId) { node ->
            node.error(throwable, message, output)
        }

    fun progressNode(nodeId: String, output: JsonNode) = updateNode(nodeId) { node ->
        node.progress(output)
    }

    fun getNode(nodeId: String) = nodesExecutions.firstOrNull { it.id == nodeId }
        ?: throw WorkflowNodeNotFoundException(nodeId)

    private fun collectParentsData(results: MutableMap<String, JsonNode?>, workflowNodeId: String, depth: Int) {
        val instanceNode = getNode(workflowNodeId)
        val workflowNode = workflow.getNode(workflowNodeId)
        if (depth > 0) {
            results[workflowNode.id] = instanceNode.output
        }
        workflowNode.parents.forEach { parent ->
            collectParentsData(results, parent.id, depth + 1)
        }
    }

    /**
     * Starting from a node, gets the index of all its parent's data
     */
    fun getParentsData(workflowNodeId: String): Map<String, JsonNode?> {
        val results = mutableMapOf<String, JsonNode?>()
        collectParentsData(results, workflowNodeId, 0)
        return results.toMap()
    }

    fun stopNodes(): WorkflowInstance =
        WorkflowInstance(
            id = id,
            timestamp = timestamp,
            workflow = workflow,
            event = event,
            nodesExecutions = nodesExecutions.map { nx ->
                if (nx.status.finished) {
                    nx
                } else {
                    nx.stop()
                }
            }
        )

}

