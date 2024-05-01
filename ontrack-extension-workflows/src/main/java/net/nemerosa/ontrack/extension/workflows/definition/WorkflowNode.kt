package net.nemerosa.ontrack.extension.workflows.definition

import com.fasterxml.jackson.databind.JsonNode

/**
 * Definition of a node in a workflow.
 *
 * @property id Unique ID of the node in its workflow.
 * @property data Raw data associated with the node, to be used by the node executor.
 * @property parents List of the IDs of the parents for this node
 */
data class WorkflowNode(
    val id: String,
    val data: JsonNode,
    val parents: List<WorkflowParentNode> = emptyList(),
)
