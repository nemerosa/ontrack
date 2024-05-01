package net.nemerosa.ontrack.extension.workflows.definition

import com.fasterxml.jackson.databind.JsonNode

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
)
