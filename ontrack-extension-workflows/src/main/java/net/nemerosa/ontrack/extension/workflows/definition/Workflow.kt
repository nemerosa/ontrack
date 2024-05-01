package net.nemerosa.ontrack.extension.workflows.definition

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.workflows.engine.WorkflowNodeNotFoundException

/**
 * Definition of a workflow by another extension.
 *
 * @property name Display name for the workflow
 * @property data Raw data associated with the workflow, to be passed to the workflow node executor.
 */
data class Workflow(
    val name: String,
    val data: JsonNode,
    val nodes: List<WorkflowNode>,
) {
    /**
     * Given the ID of a parent, returns the next nodes in the workflow.
     */
    fun getNextNodes(parentId: String?): List<String> =
        if (parentId.isNullOrBlank()) {
            nodes.filter { it.parents.isEmpty() }.map { it.id }
        } else {
            nodes.filter { it.parents.any { ref -> ref.id == parentId } }.map { it.id }
        }

    fun getNode(nodeId: String) = nodes.firstOrNull { it.id == nodeId }
        ?: throw WorkflowNodeNotFoundException(nodeId)
}
