package net.nemerosa.ontrack.extension.workflows.definition

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.workflows.engine.WorkflowNodeNotFoundException
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.docs.DocumentationList
import net.nemerosa.ontrack.model.docs.SelfDocumented

/**
 * Definition of a workflow by another extension.
 *
 * @property name Display name for the workflow
 * @property nodes List of nodes in the workflow
 */
@SelfDocumented
data class Workflow(
    @APIDescription("Display name for the workflow")
    val name: String,
    @APIDescription("List of nodes in the workflow")
    @DocumentationList
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

    /**
     * Rename the template
     */
    fun rename(code: (name: String) -> String) = Workflow(
        name = code(name),
        nodes = nodes,
    )

    fun getNode(nodeId: String) = nodes.firstOrNull { it.id == nodeId }
        ?: throw WorkflowNodeNotFoundException(nodeId)
}
